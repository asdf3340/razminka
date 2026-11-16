import java.awt.Desktop; // Для открытия файлов в системе
import java.io.*;
import java.net.URL;
import java.nio.file.Files; // Работа с файлами: создание папок и запись/чтение
import java.nio.file.Path;
import java.util.Scanner;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
// Подключение всех необходимых библиотек
public class Main { // Начало класса программы
    private static final String INPUT_FILE = "input.txt"; // Имя файла настроек

    public static void main(String[] args) throws IOException { // Точка входа программы
        Scanner scanner = new Scanner(System.in); // Чтение ввода пользователя из консоли
        System.out.println("Выберите изображение:"); // Вывод меню изображений
        System.out.println("[1] Красная панда");
        System.out.println("[2] Выдра");
        System.out.println("[3] Море");
        int imageChoice = readInt(scanner, 1, 3); // Запрос выбора картинки от 1 до 3
        String imageUrl; // Переменная под ссылку на картинку
        String imagePath; // Путь сохранения изображения
        switch (imageChoice) {
            case 1 -> {
                imageUrl = "https://i.pinimg.com/736x/93/23/8d/93238da8a13bfe331e9952b90bd3f8f0.jpg";
                imagePath = "images/panda.jpg";
            }
            case 2 -> {
                imageUrl = "https://www.thecooldown.com/wp-content/uploads/2025/09/rescue-otterimage-recm4XVXAsj2BJbXR.jpg";
                imagePath = "images/otter.jpg";
            }
            default -> {
                imageUrl = "https://img.magnific.com/free-photo/breathtaking-view-natural-beach-landscape_23-2151683035.jpg?semt=ais_hybrid&w=740&q=80";
                imagePath = "images/sea.jpg";
            }
        } // Выбор ссылки и пути по номеру варианта
        System.out.println("\nВыберите песню:");
        System.out.println("[1] SoundHelix-Song-8.mp3");
        System.out.println("[2] SoundHelix-Song-1.mp3");
        System.out.println("[3] Awakening Dawn");
        System.out.println("[4] Twilight Echoes");
        int musicChoice = readInt(scanner, 1, 4);
        String musicUrl; // Ссылка на музыку
        String musicPath; // Путь сохранения музыки
        if      (musicChoice == 1) {
            musicUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3";
            musicPath = "music/song8.mp3";
        } else if (musicChoice == 2) {
            musicUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3";
            musicPath = "music/song1.mp3";
        } else if (musicChoice == 3) {
            musicUrl = "https://files.catbox.moe/cf914e.mp3";
            musicPath = "music/awakening-dawn.mp3";
        } else{
            musicUrl = "https://files.catbox.moe/1965f2.mp3";
            musicPath = "music/twilight-echoes.mp3";
        } // Выбор ссылки и пути для песни
        try (FileWriter fw = new FileWriter(INPUT_FILE)) {
            fw.write(imageUrl + " " + imagePath + "\n"); // Сохраняем данные о картинке в файл input.txt
            fw.write(musicUrl + " " + musicPath + "\n");
        } // Закрываем файл после записи
        try (Scanner fileScanner = new Scanner(new File(INPUT_FILE))) {
            String imageLine = fileScanner.nextLine(); // Читаем первую строку — картинка
            String musicLine = fileScanner.nextLine();
            String[] imageParts = imageLine.split("\\s+"); // Разбиваем строку на URL и путь к сохранению
            String[] musicParts = musicLine.split("\\s+");
            Thread downloadImageThread = new Thread(() -> { // Поток для загрузки картинки
                try {
                    download(imageParts[0], imageParts[1]); // Скачивание картинки
                    openFile(imageParts[1]); // Открытие скачанной картинки во встроенном просмотрщике ОС
                } catch (IOException e) {
                    handleError("Ошибка при скачивании или открытии картинки", e);
                }
            });
            Thread playMusicThread = new Thread(() -> { // Поток для загрузки и проигрывания музыки
                try {
                    download(musicParts[0], musicParts[1]); // Сначала качаем музыку
                    playMusic(musicParts[1]);               // Потом играем через плеер JavaLayer
                    openFile(musicParts[1]); // И открываем музыкальный файл в стандартном медиаплеере системы
                } catch (IOException | JavaLayerException e) {
                    handleError("Ошибка при работе с музыкой", e);
                }
            });
            downloadImageThread.start(); // Старт потока с картинкой
            playMusicThread.start();   // Старт потока с музыкой
        } // Завершение работы со сканером файла настроек
    }
    //Метод для безопасного ввода целого числа в диапазоне [min..max]
    private static int readInt(Scanner sc, int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(sc.nextLine());
                if (choice >= min && choice <= max)
                    return choice;
                System.out.print("Введите число от " + min + " до " + max + ": ");
            } catch (NumberFormatException ignored) {
                System.out.print("Это не число! Попробуйте снова: ");
            }
        }
    }
    //Загрузка файла по ссылке, создаёт промежуточные директории автоматически
    private static void download(String urlStr, String fileName) throws IOException {
        Files.createDirectories(Path.of(fileName).getParent()); // Создаем папку, если её нет
        try (
                InputStream in = new URL(urlStr).openStream();
                OutputStream out = Files.newOutputStream(Path.of(fileName))
        ) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("Файл сохранён: " + fileName);
        }
    }
    //Воспроизводит MP3-файл через библиотеку JavaLayer
    private static void playMusic(String path) throws IOException, JavaLayerException {
        try (FileInputStream fis = new FileInputStream(path)) {
            Player player = new Player(fis);
            player.play(); // Блокирует поток до конца трека
        }
    }
    //Вывод ошибки в stderr
    private static void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace(System.err); // Полный стек ошибок
    }
    //Открывает указанный файл в стандартной программе пользователя
    private static void openFile(String filePath) {
        try {
            Desktop.getDesktop().open(new File(filePath));
        } catch (IOException e) {
            System.err.println("Не удалось открыть файл: " + filePath);
            e.printStackTrace();
        }
    }
}
