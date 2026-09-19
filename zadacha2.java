import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("17-342.txt"));

        int[] numbers = new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!line.isEmpty()) {
                numbers[i] = Integer.parseInt(line);
            }
        }

        //Поиск границ интервала
        int minMult37 = Integer.MAX_VALUE;
        int maxMult73 = Integer.MIN_VALUE;

        for (int num : numbers) {
            if (num % 37 == 0 && num < minMult37) {
                minMult37 = num;
            }
            if (num % 73 == 0 && num > maxMult73) {
                maxMult73 = num;
            }
        }

        // Упорядочиваем границы — это была главная ошибка
        int lo = Math.min(minMult37, maxMult73);
        int hi = Math.max(minMult37, maxMult73);

        System.out.println("Минимальное кратное 37: " + minMult37);
        System.out.println("Максимальное кратное 73: " + maxMult73);
        System.out.println("Интервал: (" + lo + ", " + hi + ")");
        System.out.println();

        //Поиск пар и минимальной суммы
        int pairCount = 0;
        int minSum = Integer.MAX_VALUE;

        for (int i = 0; i < numbers.length - 1; i++) {
            int a = numbers[i];
            int b = numbers[i + 1];

            boolean aInRange = (a > lo && a < hi);
            boolean bInRange = (b > lo && b < hi);

            if (aInRange ^ bInRange) {
                pairCount++;
                int sum = a + b;
                if (sum < minSum) {
                    minSum = sum;
                }
            }
        }

        System.out.println(pairCount);
        System.out.println(minSum);
    }
}
