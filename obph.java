import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.*;

public class Main {
    // Ключевые слова Java — не переименовываем
    private static final Set<String> KEYWORDS = new HashSet<>(Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double",
            "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int",
            "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while",
            "true", "false", "null", "var"
    ));
    // Имена стандартной библиотеки — тоже не трогаем
    private static final Set<String> SKIP = new HashSet<>(Arrays.asList(
            "System", "out", "in", "err", "println", "print", "printf","main", "args","String", "Integer", "Double", "Float", "Long", "Short", "Byte",
            "Boolean", "Character", "Object", "Math", "Scanner", "Exception", "RuntimeException", "Throwable", "Error",
            "List", "ArrayList", "Map", "HashMap", "Set", "HashSet", "Arrays", "Collections", "Override",
            "toString", "equals", "hashCode", "length", "size", "get", "put", "add", "remove", "valueOf",
            "parseInt", "parseDouble", "nextInt", "nextLine", "next", "StringBuilder", "StringBuffer", "Thread", "Runnable",
            "Comparable", "Iterator", "Random", "Files", "Paths", "IOException", "FileNotFoundException"
    ));
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Использование: java Obfuscator <путь к папке>");
            return;
        }
        // Берём только .java-файлы из указанной папки.
        File dir = new File(args[0]);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".java"));
        if (files == null || files.length == 0) {
            System.out.println("В указанной папке не найдено файлов .java");
            return;
        }
        for (File f : files) {
            obfuscateFile(f);
        }
    }
    // Полная обработка файла
    private static void obfuscateFile(File file) throws IOException {
        String code = new String(Files.readAllBytes(file.toPath()), "UTF-8");
        code = removeComments(code);//убираем комментарии 
        String oldClassName = findClassName(code);// Имя класса нужно для отдельного переименования и имени файла.
        if (oldClassName == null) {
            System.out.println("Не найден класс в файле " + file.getName() + " — пропускаем");
            return;
        }
        List<String> identifiers = findIdentifiers(code, oldClassName);// Собираем идентификаторы, которые можно безопасно переименовать.
        Map<String, String> renameMap = generateNames(identifiers);// Генерируем короткие имена 
        String newClassName = "A";// Имя класса переименовываем отдельно
        renameMap.put(oldClassName, newClassName);
        code = replaceIdentifiers(code, renameMap);// Применяем замены по границам слова — заодно переименует конструкторы и new Old().
        code = compressWhitespace(code);//сжимаем пробелы
        // Сохраняем под новым именем класса.
        File newFile = new File(file.getParentFile(), newClassName + ".java");
        try (Writer w = new OutputStreamWriter(new FileOutputStream(newFile), "UTF-tring> buildNamePool(int count) {
        List<String> pool = new ArrayList<>();
        String letters = "abcdefghijklmnopqrstuvwxyz";
        // Однобуквенные
        for (char c : letters.toCharArray()) {
            String name = String.valueOf(c);
            if (!KEYWORDS.contains(name)) pool.add(name);
            if (pool.size() >= count) return pool;
        }
        // Двубуквенные
        for (char c1 : letters.toCharArray()) {
            for (char c2 : letters.toCharArray()) {
                String name = "" + c1 + c2;
                if (!KEYWORDS.contains(name)) pool.add(name);
                if (pool.size() >= count) return pool;
            }
        }
        return pool;
    }
    // Замена по границам слова
    // Pattern.quote защищает старое имя, Matcher.quoteReplacement — новое.
    private static String replaceIdentifiers(String code, Map<String, String> renameMap) {
        for (Map.Entry<String, String> entry : renameMap.entrySet()) {
            String oldName = entry.getKey();
            String newName = entry.getValue();
            code = code.replaceAll("\\b" + Pattern.quote(oldName) + "\\b",
                    Matcher.quoteReplacement(newName));
        }
        return code;
    }
    // Сжатие пробелов посимвольно: не трогает содержимое "..." и '...',
    // Убирает пустые строки, отступы и хвостовые пробелы
    private static String compressWhitespace(String code) {
        StringBuilder sb = new StringBuilder(code.length());
        int n = code.length();
        boolean inString = false;
        boolean inChar = false;
        boolean lastWasSpace = false;
        boolean lineStart = true;
        for (int i = 0; i < n; i++) {
            char c = code.charAt(i);
            if (inString) {
                sb.append(c);
                if (c == '\\' && i + 1 < n) { sb.append(code.charAt(i + 1)); i++; continue; }
                if (c == '"') inString = false;
                continue;
            }
            if (inChar) {
                sb.append(c);
                if (c == '\\' && i + 1 < n) { sb.append(code.charAt(i + 1)); i++; continue; }
                if (c == '\'') inChar = false;
                continue;
            }
            if (c == '"')  { sb.append(c); inString = true;  lastWasSpace = false; lineStart = false; continue; }
            if (c == '\'') { sb.append(c); inChar   = true;  lastWasSpace = false; lineStart = false; continue; }
            if (c == '\n' || c == '\r') {
                if (!lineStart) sb.append('\n');// пустую строку не добавляем
                lineStart = true;
                lastWasSpace = false;
                continue;
            }
            if (c == ' ' || c == '\n') {
                if (lineStart) continue; // отступ в начале строки убираем
                if (lastWasSpace) continue;// повторный пробел пропускаем
                sb.append(' ');
                lastWasSpace = true;
                continue;
            }
            sb.append(c);
            lastWasSpace = false;
            lineStart = false;
        }
        // Хвостовые пробелы и переносы в конце файла не нужны.
        while (sb.length() > 0) {
            char last = sb.charAt(sb.length() - 1);
            if (last == '\n' || last == ' ' || last == '\n') sb.setLength(sb.length() - 1);
            else break;
        }
        return sb.toString();
    }
}
