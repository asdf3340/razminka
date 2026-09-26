// Подключаем класс Thread для работы с потоками
public class RabbitAndTurtle {
    // Длина дистанции
    // Когда животное достигает этого количества метров, его поток завершает работу
    private static final int DISTANCE = 30;
    public static void main(String[] args) throws InterruptedException {
        // Создаём поток для Кролика
        // 8 — начальный приоритет потока
        AnimalThread rabbit = new AnimalThread("Кролик", 8);
        // Создаём поток для Черепахи
        // Начальный приоритет тоже равен 8
        AnimalThread turtle = new AnimalThread("Черепаха", 8);
        // Выводим сообщение о начале соревнования.
        System.out.println("Старт!");
        // Запускаем поток Кролика
        // После этого автоматически начнёт выполняться метод run()
        rabbit.start();
        // Запускаем поток Черепахи
        turtle.start();
        //Цикл работает до тех пор, пока оба животных не закончили гонку
        while (rabbit.isAlive() && turtle.isAlive()) {
            // Главный поток делает паузу 200 миллисекунд
            // Это нужно для того, чтобы не проверять положение животных слишком часто
            Thread.sleep(200);
            //Проверяем, кто сейчас впереди
             //Если Кролик прошёл больше метров, значит, Черепаха отстаёт
            if (rabbit.getMeters() > turtle.getMeters()) {
                // Уменьшаем приоритет Кролика
                rabbit.setPriority(Thread.MIN_PRIORITY);
                // Увеличиваем приоритет Черепахи, на отстаёт и должна догнать Кролика
                turtle.setPriority(Thread.MAX_PRIORITY);
                // Выводим сообщение на экран
                System.out.println(
                        "Черепаха отстаёт. Ей повышен приоритет."
                );
            }
            //Если Черепаха прошла больше метров, значит, Кролик сейчас отстаёт
            else if (turtle.getMeters() > rabbit.getMeters()) {
                // Уменьшаем приоритет Черепахи, потому что она находится впереди
                turtle.setPriority(Thread.MIN_PRIORITY);
                // Увеличиваем приоритет Кролика, чтобы он смог догнать Черепаху
                rabbit.setPriority(Thread.MAX_PRIORITY);
                // Выводим сообщение на экран
                System.out.println(
                        "Кролик отстаёт. Ему повышен приоритет."
                );
            }
            //Показываем текущее положение обоих животных
            System.out.println(
                    rabbit.getThreadName()
                            + ": " + rabbit.getMeters() + " м; "
                            + turtle.getThreadName()
                            + ": " + turtle.getMeters() + " м"
            );
        }
        //Метод join() заставляет главный поток дождаться полного завершения потока Кролика
        rabbit.join();
        //Также ждём завершения потока Черепахи
        turtle.join();
        // Сообщаем, что гонка завершилась
        System.out.println("\nФиниш!");

        //Определяем победителя, если Кролик достиг дистанции, выводим, что победил Кролик
        if (rabbit.getMeters() >= DISTANCE) {
            System.out.println("Победил Кролик!");
        }
        //Если дистанцию достигла Черепаха, значит, победила Черепаха
        else {
            System.out.println("Победила Черепаха!");
        }
    }
    // Создаём класс AnimalThread
    static class AnimalThread extends Thread {
        // Переменная хранит имя животного
        private String threadName;
        // Переменная хранит начальный приоритет потока
        private int threadPriority;
        //Количество метров, которое прошло животное
        //volatile означает, что значение переменной может изменяться одним потоком и читаться другим потоком
        private volatile int meters;
         //Конструктор класса
         //Он получает имя животного и его приоритет
        public AnimalThread(String threadName, int threadPriority) {
            // Запоминаем имя животного
            this.threadName = threadName;
            // Запоминаем приоритет животного
            this.threadPriority = threadPriority;
            // В начале гонки животное прошло 0 метров
            this.meters = 0;
            //Устанавливаем имя потока
            setName(threadName);
            //Устанавливаем приоритет потока
            setPriority(threadPriority);
        }
        @Override
        public void run() {
            //Цикл продолжается до тех пор, пока животное не пройдёт всю дистанцию
            while (meters < DISTANCE) {
                // Животное проходит ещё один метр
                meters++;
                // Выводим на экран имя животного и пройденное расстояние
                System.out.println(
                        threadName + " пробежал "
                                + meters + " м"
                );
                //Обрабатываем возможное прерывание потока
                try {
                    // Если у потока максимальный приоритет, животное делает маленькую паузу, из-за этого оно движется быстрее
                    if (getPriority() == Thread.MAX_PRIORITY) {
                        //Пауза 50 миллисекунд.
                        Thread.sleep(50);
                    }
                    // Если приоритет обычный или минимальный, животное делает большую паузу, из-за этого оно движется медленнее
                    else {
                        // Пауза 150 миллисекунд
                        Thread.sleep(150);
                    }
                }
                //Этот блок выполняется, если поток был прерван
                catch (InterruptedException e) {
                    // Сообщаем, что поток остановлен
                    System.out.println(
                            threadName + " остановлен."
                    );
                    return;
                }
            }
        }

        // Метод возвращает имя животного
        public String getThreadName() {
            return threadName;
        }
        //Метод возвращает количество метров, которое прошло животное
        public int getMeters() {
            return meters;
        }
    }
}
