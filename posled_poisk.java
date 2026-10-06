import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    // Делитель, на который должно делиться произведение (21 = 3 * 7)
    private static final int DIVISOR = 21;
    // Множитель 3 (нужен для проверки делимости на 3)
    private static final int FACTOR_3 = 3;
    // Множитель 7 (нужен для проверки делимости на 7)
    private static final int NOT_FOUND = -1; // Значение, которое вернём, если подходящего произведения нет
    private static final int INF = Integer.MAX_VALUE; // Начальное значение для поиска минимума
    // Путь к файлу с данными
    private static final String FILE_PATH = "C:\\Users\\Admin\\Downloads\\Посл поиск.txt";

    public static void main(String[] args) {
        File file = new File(FILE_PATH);

        // Проверяем, существует ли файл по указанному пути
        if (!file.exists()) {
            System.out.println("Файл не найден по пути: " + file.getAbsolutePath());
            System.out.println("Проверьте:");
            System.out.println("  1. Правильно ли указан путь (регистр букв не важен).");
            System.out.println("  2. Существует ли файл на самом деле.");
            System.out.println("  3. Нет ли опечаток в имени файла (особенно в пробеле).");
            return;
        }

        // Проверяем, что путь ведёт именно к файлу, а не к папке
        if (!file.isFile()) {
            System.out.println("Путь существует, но это не файл (возможно, папка).");
            return;
        }

        // Пытаемся открыть файл и прочитать данные
        try (Scanner scanner = new Scanner(file, StandardCharsets.UTF_8)) {
            System.out.println("Файл найден. Начинаем чтение...");

            // Проверяем, есть ли в файле хотя бы одно число
            if (!scanner.hasNextInt()) {
                System.out.println("Файл пуст или не содержит чисел.");
                return;
            }

            int sequenceSize = scanner.nextInt();
            System.out.println("В файле указано количество элементов: " + sequenceSize);

            // Количество элементов должно быть положительным
            if (sequenceSize <= 0) {
                System.out.println("Количество элементов должно быть положительным.");
                return;
            }

            int[] sequence = new int[sequenceSize];
            // Читаем все числа из файла в массив
            for (int i = 0; i < sequenceSize; i++) {
                if (!scanner.hasNextInt()) {
                    System.out.println("В файле недостаточно чисел: ожидалось " + sequenceSize + ", прочитано " + i);
                    return;
                }
                sequence[i] = scanner.nextInt();
            }

            System.out.println("Успешно считано " + sequenceSize + " чисел.");

            // Ищем минимальное произведение двух чисел, кратное 21
            int result = findMinimalProductMultipleOf21(sequence);

            if (result == NOT_FOUND) {
                System.out.println("Подходящее произведение не найдено. Результат: " + NOT_FOUND);
            } else {
                System.out.println("Минимальное произведение, кратное " + DIVISOR + ": " + result);
            }

        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException: файл не найден — " + e.getMessage());
        } catch (IOException e) {
            // Обработка ошибок ввода‑вывода
            System.out.println("IOException: ошибка при работе с файлом — " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Ловим любые другие непредвиденные ошибки
            System.out.println("Неожиданная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Ищет минимальное произведение любых двух элементов массива, которое делится на 21.
     * Возвращает NOT_FOUND, если такого произведения нет.
     */
    public static int findMinimalProductMultipleOf21(int[] sequence) {
        int minElement = INF;      // Минимальный элемент среди всех прочитанных
        int minDiv3 = INF;         // Минимальный элемент, кратный 3
        int minDiv7 = INF;         // Минимальный элемент, кратный 7
        int minDiv21 = INF;        // Минимальный элемент, кратный 21
        int result = INF;          // Текущее минимальное найденное произведение, кратное 21

        for (int x : sequence) {
            // Если уже встречали число, кратное 21, то умножаем его на текущий элемент x
            if (minDiv21 != INF) {
                result = Math.min(result, x * minDiv21);
            }

            // Если текущий элемент кратен 21, умножаем его на самый маленький из ранее встреченных
            if (x % DIVISOR == 0 && minElement != INF) {
                result = Math.min(result, x * minElement);
            }

            // Если текущий элемент кратен 3, ищем ранее встреченный элемент, кратный 7
            if (x % FACTOR_3 == 0 && minDiv7 != INF) {
                result = Math.min(result, x * minDiv7);
            }

            // Если текущий элемент кратен 7, ищем ранее встреченный элемент, кратный 3
            if (x % FACTOR_7 == 0 && minDiv3 != INF) {
                result = Math.min(result, x * minDiv3);
            }

            // Обновляем минимальные значения для разных категорий
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

        // Если результат не обновился, значит, подходящего произведения не нашли
        return result == INF ? NOT_FOUND : result;
    }
}

        return result == INF ? NOT_FOUND : result;
    }
}
