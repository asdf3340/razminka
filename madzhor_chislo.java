import java.util.Scanner;

public class Main 
{
    public static void main(String[] args) 
  {
        System.out.println("Введите размер массива: ");
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        System.out.println("Введите элементы массива: ");
        int[] nums = new int[a];
        for(int i =0; i< a; i++)
        {
            System.out.println("Введите элемент массива " + (i+1));
            Scanner in = new Scanner(System.in);
            nums[i] = in.nextInt();
        }
        if(Element(nums) == 0 )
        {
            System.out.println("Мажоритарного числа нет ");
        }
        else
        {
            System.out.println("Мажоритарное число: " + Element(nums));
        }
    }
    public static int Element( int [] nums)
  {
        int canditat = nums[0];
        int count = 1;
        for(int i = 1; i< nums.length; i++)
        {
            if(count == 0)
            {
                canditat = nums[i];
                count = 1;
            } 
            else if (nums[i] == canditat) 
            {
                count ++;
            }
            else
            {
                count --;
            }
        }
        count = 0;
        for(int num :nums)
        {
            if(num == canditat)
            {
                count++;
            }
        }
        if (count > nums.length/2)
        {
            return canditat;

        }
        else
        {
            return 0;
        }
    }
}
