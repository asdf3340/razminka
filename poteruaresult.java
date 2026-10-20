import java.util.concurrent.atomic.AtomicInteger;

public class Main 
{    
    private static final int HUNDRED_MILLION = 100_000_000;//сколько раз всего нужно прибавить единицу (100 миллионов)
    private static final AtomicInteger counter = new AtomicInteger();// Атомарный счётчик, "стоп-сигнал".
    private static int i; // Общая переменная БЕЗ синхронизации. Оба потока меняют её одновременно, поэтому часть прибавок будет потеряна

    static boolean shouldStop() // Проверка надо ли останавливаться. Названа shouldStop, потому что у Thread уже есть свой метод stop()
    {
        return counter.incrementAndGet() > HUNDRED_MILLION;
    }
    static class InterferenceThread extends Thread // Поток, который прибавляет 1 к общей переменной i
    {
        @Override
        public void run() // run() выполняется, когда вызывается start()
        {
            while (!shouldStop()) // работает, пока shouldStop() не вернёт true
            {
                i++; // Операция НЕ атомарна, она состоит из трёх шагов: 1) прочитать i, 2) прибавить 1, 3) записать обратно.
                // Если два потока прочитают одно и то же значение, оба запишут одинаковый результат, и одна прибавка пропадёт
            }
        }
    }

    public static void main(String[] args) throws InterruptedException 
    {
        // Создаём два потока, которые работают с одной и той же переменной i
        InterferenceThread thread1 = new InterferenceThread();
        InterferenceThread thread2 = new InterferenceThread();
        // Запускаем оба потока, они работают одновременно
        thread1.start();
        thread2.start();
      
        thread1.join();// join() заставляет main подождать, пока потоки закончат работу
        thread2.join();

        System.out.println("Expected: " + HUNDRED_MILLION);// Ожидание 
        System.out.println("Result: " + i);// Получили меньше из-за гонки потоков
        System.out.println("Lost: " + (HUNDRED_MILLION - i));// Разница показывает, сколько прибавок потерялось
    }
}
