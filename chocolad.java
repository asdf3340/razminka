import java.util.Scanner;

public class Main 
{
    public static void main(String[] args) 
    {
        Scanner in = new Scanner(System.in);

        System.out.println("Cколько у вас денег: ");
        int money = in.nextInt();
        System.out.println("Введите цену за шоколадку: ");
        int price = in.nextInt();
        System.out.println("Введите кол-во обёрток для получения 1 шоколадки: ");
        int wrap = in.nextInt();

        int bought = money / price;
        System.out.println("Куплено за деньги: " + bought);

        int total = bought;
        int wrappers = bought;
        while (wrappers >= wrap) {
            int newChocolates = wrappers / wrap;
            total += newChocolates;
            wrappers = wrappers % wrap + newChocolates;
        }

        System.out.println("Всего шоколадок: " + total);
        in.close();
    }
}
