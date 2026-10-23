import java.util.concurrent.atomic.AtomicInteger;

private static final int HUNDRED_MILLION = 100_000_000;//целевой рез-тат сколько раз надо прибавть 1
private static final AtomicInteger counter = new AtomicInteger();//атомарный счетсик (стоп-сигнал)
    private static int i;//общ переменная для 2 потоков

    static boolean shouldStop() //проверка надо ли остановиться
    {
        return counter.incrementAndGet() > HUNDRED_MILLION;
    }

    static synchronized void increment() //SYNCHRONIZED - пока 1 поток выполняет метод второй ждет -> прибавки не потеряются
    { 
        i++;
    }

    static class InterferenceThread extends Thread//поток добавляет 1 к i
      {
        @Override
        public void run() //выполняется при вызове start()
        { 
            while (!shouldStop()) //  цикл прервется только когда shouldStop вернет true
            { 
                increment();//прибавляем 1
            }
        }
    }
    void main() throws InterruptedException 
    {
        //создаём два потока, которые работают с одной и той же переменной i
        InterferenceThread thread1 = new InterferenceThread();
        InterferenceThread thread2 = new InterferenceThread();
        long startTime = System.currentTimeMillis();

        // запускаем потоки (работают одновременно)
        thread1.start();
        thread2.start();

        thread1.join(); //ждем пока оба потока закончат работу
        thread2.join();
        long endTime = System.currentTimeMillis();

        System.out.println("Ожидаемый результат: " + HUNDRED_MILLION);
        System.out.println("Фактический результат: " + i);
        System.out.println("Потеряно: " + (HUNDRED_MILLION - i));
        System.out.println("Время: " + (endTime - startTime) + " мс");
    }
