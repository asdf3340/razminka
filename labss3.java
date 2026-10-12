static String lastWord="";//для хранения последнего слова
void main() throws InterruptedException{
    Thread chicken = new Thread(()->{//создаем поток курица
        for(int i=0;i<10;i++) {//поток будет 10 раз повторять слово "курица"
            System.out.println("Курица");
            lastWord = "курица";//запоминаем последнее слово
            try {
                Thread.sleep(100);//задержка между словами чтобы потоки успевали перемешиваться при выводе (чтобы не было одинаковых слов подряд)
            }
            catch (InterruptedException exception){//обработка исключения
                String message = exception.getMessage();
                System.out.println(message);
            }
        }
    });
    Thread egg = new Thread(()->{//создаем поток яйцо
        for(int i=0;i<10;i++) {//поток будет 10 раз повторять слово "яйцо"
            System.out.println("Яйцо");
            lastWord = "яйцо";//запоминаем последнее слово
            try {
                Thread.sleep(100);//задержка между словами
            }
            catch (InterruptedException exception){//обработка исключения
                String message = exception.getMessage();
                System.out.println(message);
            }
        }
    });
    chicken.start();//запускаем потоки
    egg.start();
    while(chicken.isAlive()||egg.isAlive()){//проверяем выполняются ли еще потоки
        chicken.join();//ждем полного завершения потоков
        egg.join();
        System.out.print("Спор окончен. Победитель - "+lastWord);//обьявляем победителя
    }
}
