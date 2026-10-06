
public class RabbitAndTurtle {
   
    
    private static final int DISTANCE = 40; 
    public static void main(String[] args) throws InterruptedException {
        AnimalThread rabbit = new AnimalThread("Кролик", 8); 
        AnimalThread turtle = new AnimalThread("Черепаха", 8);
        
        System.out.println("Старт!");

        rabbit.start();
        turtle.start(); 
        
        while (rabbit.isAlive() && turtle.isAlive()) //Цикл работает до тех пор, пока оба животных не закончили гонку
        {
            
            Thread.sleep(200);
            
            if (rabbit.getMeters() > turtle.getMeters()) //Проверяем, кто сейчас впереди. Если Кролик прошёл больше метров, значит, Черепаха отстаёт
            {
                rabbit.setPriority(Thread.MIN_PRIORITY);
                turtle.setPriority(Thread.MAX_PRIORITY);
                
                System.out.println("Черепаха отстаёт. Ей повышен приоритет.");
            }
            
            else if (turtle.getMeters() > rabbit.getMeters()) //Если Черепаха прошла больше метров, значит, Кролик сейчас отстаёт
            {
                turtle.setPriority(Thread.MIN_PRIORITY);
                rabbit.setPriority(Thread.MAX_PRIORITY);
                
                System.out.println("Кролик отстаёт. Ему повышен приоритет.");
            }
            System.out.println(rabbit.getThreadName() + ": " + rabbit.getMeters() + " м; " + turtle.getThreadName() + ": " + turtle.getMeters() + " м");// текущее положение обоих животных
        }
        
        rabbit.join();//Метод join() заставляет главный поток дождаться полного завершения потока 
        turtle.join();
        System.out.println("\nФиниш!");

        
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
        
        private String threadName;
        private int threadPriority;
        private volatile int meters; 
        public AnimalThread(String threadName, int threadPriority) 
            this.threadName = threadName;
            this.threadPriority = threadPriority;
            this.meters = 0;
            
            setName(threadName);
            setPriority(threadPriority);//приоритет потока
        }
        @Override
        public void run()
        {
            
            while (meters < DISTANCE) //Цикл продолжается до тех пор, пока животное не пройдёт всю дистанцию
                {
                meters++;
                System.out.println(threadName + " пробежал "+ meters + " м");
               
                try  //Обработка возможного прерывания потока
                {
                   
                    if (getPriority() == Thread.MAX_PRIORITY)  
                    {
                        Thread.sleep(50);
                    }
                    
                    else 
                    {
                        Thread.sleep(150);
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
