// Класс Chicken - это поток (задача, которая выполняется параллельно с другими).
class Chicken extends Thread 
{

    // Общий объект-замок (монитор). Он один на оба потока, потому что static.
    // Поток, который зашёл в synchronized-блок с этим объектом,
    // "закрывает замок", и другой поток ждёт снаружи, пока замок не откроется.
    // final - чтобы ссылка на замок никогда не менялась.
    static final Object lock = new Object();

    // Код, который выполнится, когда поток запустят через start().
    @Override
    public void run() 
    {
        for (int i = 0; i < 5; i++) 
        {
            try 
            {
                // Пауза от 0 до 100 мс (случайная).
                // Паузу делаем ВНЕ synchronized-блока, чтобы поток
                // не держал замок, пока спит, и не задерживал другой поток.
                Thread.sleep((long) (Math.random() * 100));
            }
            catch (InterruptedException e)
            {
                // Обязательная обработка ошибки, которая может возникнуть при паузе.
                e.printStackTrace();
            }

            // synchronized (lock) - вход в синхронизированный блок.
            // Внутрь может попасть только один поток за раз:
            // пока Курица внутри, Яйцо ждёт у входа (и наоборот).
            synchronized (lock)
            {
                System.out.println("Курица");
            } // здесь замок освобождается, и другой поток может войти
        }
    }
}

// Класс Egg - второй поток, устроен так же, как Chicken.
class Egg extends Thread 
{

    @Override
    public void run() 
    {
        for (int i = 0; i < 5; i++) 
        {
            try 
            {
                Thread.sleep((long) (Math.random() * 100));
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }

            // Блокируем тот же самый объект Chicken.lock.
            // Важно, чтобы замок был именно общим: если бы у каждого
            // потока был свой объект, синхронизация бы не работала.
            synchronized (Chicken.lock) 
            {
                System.out.println("Яйцо");
            }
        }
    }
}

public class Main
{
    public static void main(String[] args) 
    {

        // Создаём два потока (пока не запущены).
        Chicken chicken = new Chicken();
        Egg egg = new Egg();

        // Запускаем оба потока, дальше они работают одновременно.
        chicken.start();
        egg.start();

        try 
        {
            
            chicken.join();//главный поток ждёт, пока chicken завершится.
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }

        
        
        if (egg.isAlive()) //проверка, работает ли поток ещё.Если egg ещё работает, значит chicken закончил раньше.
        {

            try 
            {
                egg.join();/// Ждём, пока egg тоже завершится
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }

            // egg закончил последним, значит победило Яйцо.
            System.out.println("Спор окончен. Победило Яйцо");

        } 
        else 
        {
            // egg уже завершился раньше, значит последней закончила Курица.
            System.out.println("Спор окончен. Победила Курица");
        }
    }
}
