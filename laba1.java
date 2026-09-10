import java.io.IOException;
import java.util.Scanner;

void main() throws InterruptedException {
    Scanner scan=new Scanner(System.in);
    int i=1;
    while(i!=0){
        System.out.print("Введите имя программы для запуска (notepad/calc.exe/mspaint): ");
        String program = scan.nextLine();
        try {
            Process process = new ProcessBuilder(program).start();
            System.out.println("Процесс успешно запущен");
            long PID = process.pid();

            System.out.printf("\nPID процесса: %d \n", PID);
            System.out.printf("Имя процесса: %s \n", program);
            System.out.printf("Активен: %b \n", process.isAlive());
            System.out.println();

            System.out.println("Завершить процесс? (yes/no): ");
            String answer=scan.nextLine();
            if(answer.equalsIgnoreCase("yes")){
                process.destroy();
                process.waitFor();
                System.out.println("Процесс завершен");
            }
            else {
                System.out.println("Процесс оставлен работать");
            }
            System.out.print("Хотите запустить еще один процесс? (yes/no): ");
            String cont=scan.nextLine();
            if("no".equals(cont)) {
                i=0;
            }
        }
        catch(IOException exception){
            System.out.println("Ошибка: процесс не найден или его нельзя запустить.");
        }
    }
}
    }
}
