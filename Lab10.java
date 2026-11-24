import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

//формирование исходного текстового файла
static void createFile(Path file, int count) throws Exception {
    StringBuilder content = new StringBuilder();
    for (int i = 1; i <= count; i++) {
        content.append("Строка номер ")
                .append(i)
                .append("\n");
    }
    Files.write(file, content.toString().getBytes(StandardCharsets.UTF_8));
}
//обычное последовательное копирование файла
static void synchronousCopy(Path original, Path copy) throws Exception {
    try (FileChannel reader = FileChannel.open(original, StandardOpenOption.READ);
         FileChannel writer = FileChannel.open(copy,
                 StandardOpenOption.WRITE,
                 StandardOpenOption.CREATE,
                 StandardOpenOption.TRUNCATE_EXISTING)) {
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        while (reader.read(buffer) != -1) {
            buffer.flip();
            writer.write(buffer);
            buffer.clear();
        }
    }
}
//полное чтение файла асинхронным каналом
static void readFile(AsynchronousFileChannel channel,
                     ByteBuffer buffer,
                     Future<Integer> operation) throws Exception {
    long position = 0;
    while (buffer.hasRemaining()) {
        int bytes = operation.get();
        if (bytes == -1) {
            throw new Exception("Не удалось прочитать весь файл((");
        }
        position += bytes;
        if (buffer.hasRemaining()) {
            operation = channel.read(buffer, position);
        }
    }
}
//полная запись данных в асинхронном режиме
static void writeFile(AsynchronousFileChannel channel,
                      ByteBuffer buffer,
                      Future<Integer> operation) throws Exception {
    long position = 0;
    while (buffer.hasRemaining()) {
        int bytes = operation.get();
        if (bytes < 0) {
            throw new Exception("Ошибка записи файла((");
        }
        position += bytes;
        if (buffer.hasRemaining()) {
            operation = channel.write(buffer, position);
        }
    }
}
//одновременное копирование двух файлов
static void asynchronousCopy(Path firstFile, Path firstCopy,
                             Path secondFile, Path secondCopy) throws Exception {
    try (AsynchronousFileChannel reader1 =
                 AsynchronousFileChannel.open(firstFile, StandardOpenOption.READ);

         AsynchronousFileChannel reader2 =
                 AsynchronousFileChannel.open(secondFile, StandardOpenOption.READ);

         AsynchronousFileChannel writer1 =
                 AsynchronousFileChannel.open(firstCopy,
                         StandardOpenOption.WRITE,
                         StandardOpenOption.CREATE,
                         StandardOpenOption.TRUNCATE_EXISTING);

         AsynchronousFileChannel writer2 =
                 AsynchronousFileChannel.open(secondCopy,
                         StandardOpenOption.WRITE,
                         StandardOpenOption.CREATE,
                         StandardOpenOption.TRUNCATE_EXISTING)) {
        long size1 = reader1.size();
        long size2 = reader2.size();
        if (size1 > Integer.MAX_VALUE || size2 > Integer.MAX_VALUE) {
            throw new Exception("Размер файла слишком большой");
        }
        ByteBuffer buffer1 = ByteBuffer.allocate((int) size1);
        ByteBuffer buffer2 = ByteBuffer.allocate((int) size2);
        //начинаем чтение двух файлов
        Future<Integer> read1 = reader1.read(buffer1, 0);
        Future<Integer> read2 = reader2.read(buffer2, 0);
        //получаем данные из обоих файлов
        readFile(reader1, buffer1, read1);
        readFile(reader2, buffer2, read2);

        buffer1.flip();
        buffer2.flip();
        //начинаем запись двух копий
        Future<Integer> write1 = writer1.write(buffer1, 0);
        Future<Integer> write2 = writer2.write(buffer2, 0);
        //ожидаем завершения записи
        writeFile(writer1, buffer1, write1);
        writeFile(writer2, buffer2, write2);
    }
}
void main() throws Exception {
    //подготовка исходных файлов
    Path first = Paths.get("file1.txt");
    Path second = Paths.get("file2.txt");

    createFile(first, 200000);
    createFile(second, 200000);

    System.out.println("Исходные файлы созданы.");
    //измерение времени синхронного копирования
    long startTime = System.nanoTime();

    synchronousCopy(first, Paths.get("copy_sync_1.txt"));
    synchronousCopy(second, Paths.get("copy_sync_2.txt"));

    long syncResult = (System.nanoTime() - startTime) / 1000;
    System.out.println("Синхронное копирование: " + syncResult + " мкс");
    //измерение времени асинхронного копирования
    startTime = System.nanoTime();
    asynchronousCopy(
            first, Paths.get("copy_async_1.txt"),
            second, Paths.get("copy_async_2.txt")
    );
    long asyncResult = (System.nanoTime() - startTime) / 1000;
    System.out.println("Асинхронное копирование: " + asyncResult + " мкс");
    //сравнение полученных результатов
    System.out.println("\nРЕЗУЛЬТАТЫ");
    if (syncResult > asyncResult) {
        System.out.println("Асинхронное копирование оказалось быстрее.");
    }
    else if (syncResult < asyncResult) {
        System.out.println("Синхронное копирование оказалось быстрее.");
    }
    else {
        System.out.println("Время выполнения оказалось одинаковым.");
    }
}
