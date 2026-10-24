
import java.util.Scanner; // Подключаем ввод с клавиатуры
class ShortPasswordException extends Exception // Создаём своё исключение для короткого пароля
{
    public ShortPasswordException(String message) // Создаём конструктор исключения
    {
        super(message); // Передаём текст ошибки
    }
}
public class Password 
{
    public static void main(String[] args) 
  {
        Scanner scanner = new Scanner(System.in);// для ввода пароля

        try // начало блока, в котором может возникнуть ошибка
          { 
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();

            if (password.length() < 8) // проверка, меньше ли длина пароля 8 символов
            {
                throw new ShortPasswordException("Пароль должен содержать минимум 8 символов!");// Если пароль короткий, вызываем своё исключение
            }
            System.out.println("Пароль принят!");// если ошибки нет, сообщение об успехе

        } 
        catch (ShortPasswordException e) // исключение
       {
            System.out.println("Ошибка: " + e.getMessage());// вывод сообщение об ошибке

        } 
        finally// этот блок выполняется в конце
        { 
            scanner.close();// закрытие Scanner
        }
    }
}
