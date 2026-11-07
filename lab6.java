import java.io.*;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main 
{
    // Файл со ссылками (по одной в строке) и папка для музыки
    // user.home - папка текущего пользователя (на каждом компьютере своя, например C:\Users\Admin)
    private static final String Lab = System.getProperty("user.home") + "\\Documents\\Lab\\";
    private static final String File_txt = Lab + "inFile.txt";
    private static final String PathMusic = Lab + "music\\";

    // Ссылки на песни
    private static final String File_music1 = "https://rus.hitmotop.com/get/music/20251010/Vanya_Dmitrienko_Anya_Peresild_-_Silujet_79968882.mp3";
    private static final String File_music2 = "https://rus.hitmotop.com/get/music/20200207/Artik_Asti_-_Devochka_tancujj_68289048.mp3";
    private static final String File_music3 = "https://rus.hitmotop.com/get/music/20250424/Bond_s_knopkojj_-_Kukhni_79153803.mp3";

    // Запущенный плеер, название песни и список PID
    private static Process player = null;
    private static String songName = "";
    private static final List<Long> pidList = new ArrayList<>();

    public static void main(String[] args) 
    {

        System.out.println("Выберите, что скачать: (от 1 до 4)");
        System.out.println("  1 - скачать всю музыку (ссылки берутся из файла " + File_txt + ")");
        System.out.println("  2 - скачать одну песню: Силуэт");
        System.out.println("  3 - скачать одну песню: Девочка, танцуй");
        System.out.println("  4 - скачать одну песню: Кухни");
        System.out.println("Скачанные песни сохраняются в папку: " + PathMusic);
        System.out.print("Ваш выбор: ");

        try 
        {
            Scanner scanner = new Scanner(System.in);
            int input = scanner.nextInt();   // ошибка, если ввели не число
            new File(PathMusic).mkdirs();    // создание папки music
            createLinksFile();               // создаём inFile.txt со ссылками, если его нет

            if (input == 1) 
            {
                downloadAll(File_txt);
            } 
            else if (input == 2) 
            {
                playSong(File_music1, "siluet.mp3", "Силуэт");
            } 
            else if (input == 3) 
            {
                playSong(File_music2, "devochka.mp3", "Девочка, танцуй");
            } 
            else if (input == 4) 
            {
                playSong(File_music3, "kukhni.mp3", "Кухни");
            } 
            else 
            {
                System.out.println("Неверный ввод данных");
            }
        }
        catch (InputMismatchException e) 
        {
            System.out.println("Нужно ввести число от 1 до 4");
        }
        showProcessInfo();
    }

    // Создаём файл со ссылками, если его ещё нет (если уже есть - не трогаем)
    static void createLinksFile() 
    {
        File file = new File(File_txt);
        if (file.exists())
        {
            return;
        }
        file.getParentFile().mkdirs();   // создаём папку Lab, если её нет
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) 
        {
            writer.println("https://dl3s4.muzika.fun/aHR0cDovL2YubXAzcG9pc2submV0L21wMy8wMDkvMjUyLzMyNC85MjUyMzI0Lm1wMw==");
            writer.println("https://dl3s4.muzika.fun/aHR0cDovL2YubXAzcG9pc2submV0L21wMy8wMDkvNDY0LzIwMS85NDY0MjAxLm1wMw==");
            writer.println("https://dl3s4.muzika.fun/aHR0cDovL2YubXAzcG9pc2submV0L21wMy8wMDYvMzU4LzUzMC82MzU4NTMwLm1wMw==");
            System.out.println("Создан файл со ссылками: " + File_txt);
        } 
        catch (IOException e) 
        {
            System.out.println("Не удалось создать файл со ссылками: " + e.getMessage());
        }
    }

    // Скачать все песни по ссылкам из файла
    static void downloadAll(String way) 
    {
        System.out.println("\nЧитаю ссылки из файла " + way + " и скачиваю песни, подожди...");
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(way))) 
        {
            String url;
            while ((url = reader.readLine()) != null) 
            {
                url = url.trim();                 // убирание пробелов по краям
                if (url.isEmpty()) continue;      // пустые строки пропускаем
                try 
                {
                    download(url, PathMusic + "song_" + count + ".mp3");
                    System.out.println("Скачано: song_" + count + ".mp3");
                    count++;
                } 
                catch (IOException | IllegalArgumentException e) 
                {
                    // одна плохая ссылка не ломает остальные
                    System.out.println("Не скачалась ссылка " + url + " : " + e.getMessage());
                }
            }
        }
        catch (FileNotFoundException e) 
        {
            System.out.println("Файл со ссылками не найден: " + way);
        } 
        catch (IOException e) 
        {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
        System.out.println("Всего скачано песен: " + count);
    }

    // Скачать одну песню и открыть её
    static void playSong(String url, String fileName, String title)
    {
        String path = PathMusic + fileName;
        try 
        {
            System.out.println("\nСкачиваю песню " + title + ", подожди...");
            download(url, path);
            System.out.println("Песня скачана: " + path);
            System.out.println("Размер файла: " + new File(path).length() + " байт");
            System.out.println("Открываю песню в плеере...");

            // /wait - cmd живёт, пока открыт плеер, поэтому процесс будет активен
            ProcessBuilder pb = new ProcessBuilder(
                    "cmd.exe", "/c", "start", "/wait", "", new File(path).getAbsolutePath());
            player = pb.start();
            songName = title;
            pidList.add(player.pid());
        } 
        catch (IOException | IllegalArgumentException e) 
        {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // Скачивание файла из интернета
    static void download(String strUrl, String file) throws IOException 
    {
        try (ReadableByteChannel in = Channels.newChannel(URI.create(strUrl).toURL().openStream());
             FileOutputStream out = new FileOutputStream(file)) 
        {
            out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
        }
    }

    // Информация о процессах
    static void showProcessInfo() 
    {
        System.out.println("\nИНФОРМАЦИЯ О ТЕКУЩИХ ПРОЦЕССАХ:");

        if (player != null) 
        {
            System.out.println("Процесс: " + songName);
            System.out.println("PID: " + player.pid());
            System.out.println("Активен: " + player.isAlive());
        }

        ProcessHandle current = ProcessHandle.current();
        System.out.println("Текущий PID: " + current.pid());
        System.out.println("Активен: " + current.isAlive());
        System.out.println("Дочерние процессы: " + current.children().count());
        System.out.println("Информация: " + current.info());
        System.out.println("Список PID процессов: " + pidList);
    }
}
