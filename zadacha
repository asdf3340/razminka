import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    private static final int DIVISOR = 21;
    private static final int FACTOR_3 = 3;
    private static final int FACTOR_7 = 7;
    private static final int NOT_FOUND = -1;
    private static final int INF = Integer.MAX_VALUE;
    private static final String FILE_PATH = "C:\Users\Jk\Downloads\Посл поиск.txt";

    public static void main(String[] args) {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.out.println("Файл не найден по пути: " + file.getAbsolutePath());
            System.out.println("Проверьте:");
            System.out.println("  1. Правильно ли указан путь (регистр букв не важен).");
            System.out.println("  2. Существует ли файл на самом деле.");
            System.out.println("  3. Нет ли опечаток в имени файла (особенно в пробеле).");
            return;
        }

        if (!file.isFile()) {
            System.out.println("Путь существует, но это не файл (возможно, папка).");
            return;
        }

        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            System.out.println("Файл найден. Начинаем чтение...");

            if (!scanner.hasNextInt()) {
                System.out.println("Файл пуст или не содержит чисел.");
                return;
            }

            int sequenceSize = scanner.nextInt();
            System.out.println("В файле указано количество элементов: " + sequenceSize);

            if (sequenceSize <= 0) {
                System.out.println("Количество элементов должно быть положительным.");
                return;
            }

            int[] sequence = new int[sequenceSize];
            for (int i = 0; i < sequenceSize; i++) {
                if (!scanner.hasNextInt()) {
                    System.out.println("В файле недостаточно чисел: ожидалось " + sequenceSize + ", прочитано " + i);
                    return;
                }
                sequence[i] = scanner.nextInt();
            }

            System.out.println("Успешно считано " + sequenceSize + " чисел.");

            int result = findMinimalProductMultipleOf21(sequence);

            if (result == NOT_FOUND) {
                System.out.println("Подходящее произведение не найдено. Результат: " + NOT_FOUND);
            } else {
                System.out.println("Минимальное произведение, кратное " + DIVISOR + ": " + result);
            }

        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: файл не найден — " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: ошибка при работе с файлом — " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int findMinimalProductMultipleOf21(int[] sequence) {
        int minElement = INF;
        int minDiv3 = INF;
        int minDiv7 = INF;
        int minDiv21 = INF;
        int result = INF;

        for (int x : sequence) {
            // Случай: ранее встреченный элемент кратен 21 — x может быть любым
            if (minDiv21 != INF) {
                result = Math.min(result, x * minDiv21);
            }

            // Случай: x кратен 21 — ранее встреченный может быть любым
            if (x % DIVISOR == 0 && minElement != INF) {
                result = Math.min(result, x * minElement);
            }

            // Случай: x кратен 3 — нужен ранее встреченный, кратный 7
            if (x % FACTOR_3 == 0 && minDiv7 != INF) {
                result = Math.min(result, x * minDiv7);
            }

            // Случай: x кратен 7 — нужен ранее встреченный, кратный 3
            if (x % FACTOR_7 == 0 && minDiv3 != INF) {
                result = Math.min(result, x * minDiv3);
            }

            // Обновляем минимумы
            minElement = Math.min(minElement, x);
            if (x % DIVISOR == 0) {
                minDiv21 = Math.min(minDiv21, x);
            }
            if (x % FACTOR_3 == 0) {
                minDiv3 = Math.min(minDiv3, x);
            }
            if (x % FACTOR_7 == 0) {
                minDiv7 = Math.min(minDiv7, x);
            }
        }

        return result == INF ? NOT_FOUND : result;
    }
}
