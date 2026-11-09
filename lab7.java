import java.io.*;
import java.net.URI;
import java.net.http.HttpClient; // Для отправки HTTP-запросов
import java.net.http.HttpRequest; // Объект запроса
import java.net.http.HttpResponse; // Ответ от сервера
import java.nio.file.Files; // Работа с файлами на диске
import java.nio.file.Path; // Путь к папке или файлу
import java.time.Duration; // Время ожидания
import java.util.Scanner; // Ввод данных пользователем
public class Main 
{
    // Ссылки на картинки в интернете
    private static final String HOUSE_URL = "https://i.pinimg.com/originals/c6/a8/0d/c6a80d2c31b4623bd25010028d22d6c8.jpg?nii=t";
    private static final String HORSE_URL = "https://i.pinimg.com/736x/dc/c5/8f/dcc58f5dcacfffdb8347761793f83e5e.jpg";
    private static final String LEMON_URL = "https://avatars.mds.yandex.net/i?id=4da442041a9a5257bcc3c6cc786644a4_l-10231558-images-thumbs&n=13";
    private static final String PANDA_URL = "https://i.pinimg.com/originals/c6/2f/7e/c62f7e33bd163f592800e09f07211a2f.png?nii=t";
    // Папка, куда будут сохраняться файлы images, создастся автоматически.
    private static final Path FOLDER_PATH = Path.of("images");
    public static void main(String[] args) throws IOException 
    { // Точка входа программы
        Scanner scanner = new Scanner(System.in); // Создаём объект для чтения ввода пользователя
        System.out.println("Выберите что хотите скачать:"); // Меню выбора
        System.out.println("1 - Домик");
        System.out.println("2 - Лошадь");
        System.out.println("3 - Лимон");
        System.out.println("4 - Панда");
        System.out.println("5 - Своя ссылка"); // Позволяет пользователю ввести любую прямую ссылку
        int choice = scanner.nextInt(); // Читаем выбор пользователя
        scanner.nextLine(); // Убираем символ переноса строки после nextInt()
        switch (choice) 
        { // Обрабатываем выбор
            case 1 -> download(HOUSE_URL, "house.jpg"); // Скачиваем домик
            case 2 -> download(HORSE_URL, "horse.jpg"); // Скачиваем лошадь
            case 3 -> download(LEMON_URL, "lemon.jpg"); // Скачиваем лимон
            case 4 -> download(PANDA_URL, "panda.jpg"); // Скачиваем панду
            case 5 -> manualDownload(scanner); // Пользователь вводит свою ссылку
            default -> System.err.println("Ошибка: неверный выбор."); // Если пользователь ввёл не то число
        }
    }
    //Основной метод скачивания файла по прямой ссылке.
    private static void download(String url, String fileName) throws IOException 
    {
        try 
        {
            Files.createDirectories(FOLDER_PATH); // Проверяем наличие папки images и создаем её, если нет
            HttpRequest request = HttpRequest.newBuilder(URI.create(url)) // Строим запрос
                    .header("User-Agent", "Mozilla/5.0") // Имитация браузера
                    .timeout(Duration.ofSeconds(30))
                    .GET() // Метод запроса — GET
                    .build();
            // Отправляем запрос и указываем, что ответ нужно записать в файл.
            // Файл будет лежать в папке images под именем fileName.
            HttpResponse<Path> response = HttpClient.newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofFile(FOLDER_PATH.resolve(fileName)));
            if (response.statusCode() != 200) 
            { // Код 200 означает «всё хорошо»
                throw new IOException("Сервер вернул код ответа: " + response.statusCode());
            }
            System.out.println("Файл успешно сохранён в папку 'images': " + fileName);
        }
        catch (IOException | InterruptedException e) 
        {
            System.err.println("Ошибка при загрузке: " + e.getMessage()); // Сообщение об ошибке
        }
    }
    //Этот метод позволяет пользователю самому ввести URL.
    private static void manualDownload(Scanner scanner) throws IOException 
    {
        System.out.print("Введите прямую ссылку на изображение: ");
        String url = scanner.nextLine().trim(); // Получили ссылку
        if (url.isEmpty()) 
        { // Защита от пустой ссылки
            System.err.println("Ссылка не может быть пустой!");
            return;
        }
        // Берем имя файла из самой ссылки (то, что идет после последнего слэша).
        String fileName = url.substring(url.lastIndexOf('/') + 1);
        download(url, fileName); // Запускаем основной метод скачивания
    }
}
