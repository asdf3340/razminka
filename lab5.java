import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileCopy 
{
    static final String FROM1 = "file1.txt";
    static final String TO1 = "copy1.txt";
    static final String FROM2 = "file2.txt";
    static final String TO2 = "copy2.txt";

  
  
  // Создаётcя текстовый файл с 200000 строк
static void createFile(String name) throws IOException 
{
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 200000; i++) 
        {
            text.append("Строка номер ").append(i).append("\n");
        }
        Files.write(Paths.get(name), text.toString().getBytes());
        System.out.println("Создан файл " + name);
}

    // Копируется один файл: откуда, куда
    static void copyFile(String from, String to) throws IOException 
    {
        System.out.println("Копируется " + from + " -> " + to);
        Files.copy(Paths.get(from), Paths.get(to), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Скопирован " + from + " -> " + to);
    }

    // Поток копирует один файл
    static class CopyThread extends Thread 
    {
        String from;
        String to;

        CopyThread(String from, String to) 
        {
            this.from = from;
            this.to = to;
        }

        public void run() 
        {
            try 
            {
                copyFile(from, to);
            } 
            catch (IOException e) 
            {
                System.out.println("Не удалось скопировать " + from + ": " + e);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException 
    {

        // 0. Создаются исходные файлы
        try 
        {
            createFile(FROM1);
            createFile(FROM2);
        } 
        catch (IOException e) 
        {
            System.out.println("Не удалось создать файл: " + e);
            return; // без файлов копировать нечего
        }

        // 1. Последовательное копирование: сначала первый файл, потом второй
        System.out.println("--- Последовательное копирование ---");
        long start = System.nanoTime();
        try 
        {
            copyFile(FROM1, TO1);
            copyFile(FROM2, TO2);
        } 
        catch (IOException e)
        {
            System.out.println("Не удалось скопировать файл: " + e);
        }
        long sequentialTime = (System.nanoTime() - start) / 1000; // в микросекундах
        System.out.println("Готово за " + sequentialTime + " мкс");

        // 2. Параллельное копирование: каждый файл в своём потоке
        System.out.println("--- Параллельное копирование ---");
        start = System.nanoTime();
        CopyThread t1 = new CopyThread(FROM1, TO1);
        CopyThread t2 = new CopyThread(FROM2, TO2);
        t1.start();
        t2.start();
        t1.join(); // ждём оба потока
        t2.join();
        long parallelTime = (System.nanoTime() - start) / 1000; // в микросекундах
        System.out.println("Готово за " + parallelTime + " мкс");
        System.out.println("--- Итог ---");
        System.out.println("Последовательно: " + sequentialTime + " мкс");
        System.out.println("Параллельно: " + parallelTime + " мкс");
    }
}
