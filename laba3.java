// Подключаем класс Thread для работы с потоками
public class RabbitAndTurtle {
   
    
    private static final int DISTANCE = 40;  // Длина дистанции.Животное достигает это количество метров, и его поток завершает работу
    public static void main(String[] args) throws InterruptedException {
        AnimalThread rabbit = new AnimalThread("Кролик", 8); // Создаём поток для Кролика, 8 — начальный приоритет потока
        AnimalThread turtle = new AnimalThread("Черепаха", 8);// Создаём поток для Черепахи. Начальный приоритет тоже равен 8
        
        System.out.println("Старт!");// Выводим сообщение о начале соревнования.

        rabbit.start();// Запускаем поток Кролика, автоматически начнёт выполняться метод run()
        turtle.start(); // Запускаем поток Черепахи
        
        while (rabbit.isAlive() && turtle.isAlive()) //Цикл работает до тех пор, пока оба животных не закончили гонку
        {
            // Главный поток делает паузу 200 миллисекунд
            // Это нужно для того, чтобы не проверять положение животных слишком часто
            Thread.sleep(200);
            
            if (rabbit.getMeters() > turtle.getMeters()) //Проверяем, кто сейчас впереди. Если Кролик прошёл больше метров, значит, Черепаха отстаёт
            {
                rabbit.setPriority(Thread.MIN_PRIORITY);// Уменьшение приоритета кролика
                turtle.setPriority(Thread.MAX_PRIORITY);// Увеличение приоритета Черепахи, она отстаёт и должна догнать Кролика
                
                System.out.println("Черепаха отстаёт. Ей повышен приоритет.");
            }
            
            else if (turtle.getMeters() > rabbit.getMeters()) //Если Черепаха прошла больше метров, значит, Кролик сейчас отстаёт
            {
                turtle.setPriority(Thread.MIN_PRIORITY);// Уменьшение приоритета Черепахи, потому что она находится впереди
                rabbit.setPriority(Thread.MAX_PRIORITY);// Увеличение приоритет Кролика, чтобы он смог догнать Черепаху
                
                System.out.println("Кролик отстаёт. Ему повышен приоритет.");
            }
            System.out.println(rabbit.getThreadName() + ": " + rabbit.getMeters() + " м; " + turtle.getThreadName() + ": " + turtle.getMeters() + " м");// текущее положение обоих животных
        }
        
        rabbit.join();//Метод join() заставляет главный поток дождаться полного завершения потока Кролика
        turtle.join();//Также ждём завершения потока Черепахи
        System.out.println("\nФиниш!");//гонка завершилась

        
        if (rabbit.getMeters() >= DISTANCE) //Определение победителя, если Кролик достиг дистанции - победил Кролик
        {
            System.out.println("Победил Кролик!");
        }
        else
        {
            System.out.println("Победила Черепаха!");//Если дистанцию достигла Черепаха, значит, победила Черепаха
        }
    }
   
    static class AnimalThread extends Thread // создание класса AnimalThread
        {
        // Переменные для хранения имени животного и начального приоритета потока
        private String threadName;
        private int threadPriority;
        private volatile int meters; //Количество метров, которое прошло животное (volatile означает, что значение переменной может изменяться одним потоком и читаться другим потоком)
        public AnimalThread(String threadName, int threadPriority) //конструктор
            {
            this.threadName = threadName;
            this.threadPriority = threadPriority;
            this.meters = 0;
            
            setName(threadName);// имя потока
            setPriority(threadPriority);//приоритет потока
        }
        @Override
        public void run()
        {
            
            while (meters < DISTANCE) //Цикл продолжается до тех пор, пока животное не пройдёт всю дистанцию
                {
                meters++;// Животное проходит ещё один метр
                System.out.println(threadName + " пробежал "+ meters + " м");//  имя животного и им пройденное расстояние
               
                try  //Обработка возможного прерывания потока
                {
                   
                    if (getPriority() == Thread.MAX_PRIORITY)  // Если у потока максимальный приоритет, животное делает маленькую паузу, из-за этого оно движется быстрее
                    {
                        Thread.sleep(50);//пауза
                    }
                    
                    else // Если приоритет обычный или минимальный, животное делает большую паузу, из-за этого оно движется медленнее
                    {
                        Thread.sleep(150);//пауза
                    }
                }
                catch (InterruptedException e) //если блок прерывается, то выполянется catch
                    {
                    
                    System.out.println(threadName + " остановлен.");
                    return;
                }
            }
        }

        public String getThreadName() //получение имени животного
        {
            return threadName;
        }
        public int getMeters() //получение кол-ва метров, провденное животным
        {
            return meters;
        }
    }
}
