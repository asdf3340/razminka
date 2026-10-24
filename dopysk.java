import java.util.ArrayList;
import java.util.Random;

public class Main
{

    public static void main(String[] args) throws InterruptedException
    {

        int count = 100000; // Количество чисел
        int threadCount = 4; // Количество потоков

        ArrayList<Integer> numbers = new ArrayList<>(); // список чисел
        Random random = new Random(); // генератор случайных чисел

        // Заполнение списка
        for (int i = 0; i < count; i++)
        {
            numbers.add(random.nextInt(100)); // Добавляем число от 0 до 99
        }
        long start = System.nanoTime(); // Запоминаем время начала
        long simpleSum = 0; // Переменная для общей суммы

        for (int number : numbers) // берутся числа по очереди
        {
            simpleSum += number;
        }
        long end = System.nanoTime(); // Запоминаем время окончания

        long simpleTime = (end - start) / 1000; // Считаем время в микросекундах

        long[] sums = new long[threadCount];// Создаём массив для сумм четырёх потоков
        Thread[] threads = new Thread[threadCount];// Создаём массив для самих потоков
        int partSize = count / threadCount;// Сколько чисел будет обрабатывать каждый поток

        // Запоминаем начало параллельного сложения
        long parallelStart = System.nanoTime();


        for (int i = 0; i < threadCount; i++) // Создаём четыре потока
        {
            int number = i; // Запоминаем номер потока
            int startIndex = i * partSize; // Начало его части
            int endIndex = startIndex + partSize; // Конец его части

            // Создаём поток
            threads[i] = new Thread(() -> {
                long sum = 0; // сумма чисел этого потока
                for (int j = startIndex; j < endIndex; j++) // складываем числа своей части
                {
                    sum += numbers.get(j); // Прибавление очередное число
                }
                sums[number] = sum; // Сохранение результата потока
            });
            threads[i].start(); // запуск потока
        }

        for (int i = 0; i < threadCount; i++)
        {
            threads[i].join(); // Ждём завершения потока
        }

        long parallelSum = 0;

        for (int i = 0; i < threadCount; i++) //сложение результатов всех потоков
        {
            parallelSum += sums[i]; // добавляем сумму очередного потока
        }
        long parallelEnd = System.nanoTime(); // Запоминаем конец параллельного сложения
        long parallelTime = (parallelEnd - parallelStart) / 1000;

        //вывод результатов
        System.out.println("Обычная сумма: " + simpleSum);
        System.out.println("Время обычного сложения: " + simpleTime + " мкс");

        System.out.println("Сумма четырёх потоков: " + parallelSum);
        System.out.println("Время работы потоков: " + parallelTime + " мкс");


        if (simpleSum == parallelSum)// одинаковые ли суммы
        {
            System.out.println("Суммы совпадают");
        }
        else
        {
            System.out.println("Суммы не совпадают");
        }
    }
}
