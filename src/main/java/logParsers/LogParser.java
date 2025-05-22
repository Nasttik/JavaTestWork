
package logParsers;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

public class LogParser {

    public static class LogEntry {
        public String timestamp;
        public String user;
        public String operation;
        public String amount;
        public String recipient;

        public LogEntry(String timestamp, String user, String operation,
                        String amount, String recipient) {
            this.timestamp = timestamp;
            this.user = user;
            this.operation = operation;
            this.amount = amount;
            this.recipient = recipient;
        }

        @Override
        public String toString() {
            if ("received".equals(operation)) {
                return String.format("[%s] %s received %s from %s",
                        timestamp, user, amount, recipient);
            }
            return String.format("[%s] %s %s %s%s",
                    timestamp, user, operation, amount,
                    recipient != null ? " to " + recipient : "");
        }
    }

    public static void processUserLogs(String logDirPath) throws IOException {
        // 1. Создаю папку для результатов (если не существует)
        Path outputDir = Paths.get(logDirPath, "transactions_by_users");
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        // 2. Получаю все log-файлы
        List<File> logFiles = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(logDirPath), "*.log")) {
            for (Path path : stream) {
                logFiles.add(path.toFile());
            }
        }

        // 3. Собираю все записи
        List<LogEntry> allEntries = new ArrayList<>();
        for (File file : logFiles) {
            allEntries.addAll(readLogFile(file));
        }

        // 4. Группирую по пользователям
        Map<String, List<LogEntry>> userEntries = new HashMap<>();
        for (LogEntry entry : allEntries) {
            // Добавляю оригинальную запись
            addEntry(userEntries, entry.user, entry);

            // Для переводов добавляю запись получателю
            if ("transferred".equals(entry.operation) && entry.recipient != null) {
                LogEntry receivedEntry = new LogEntry(
                        entry.timestamp,
                        entry.recipient,
                        "received",
                        entry.amount,
                        entry.user
                );
                addEntry(userEntries, entry.recipient, receivedEntry);
            }
        }

        // 5. Создаю файлы для каждого пользователя
        for (Map.Entry<String, List<LogEntry>> entry : userEntries.entrySet()) {
            createUserFile(entry.getKey(), entry.getValue(), outputDir);
        }
    }

    private static List<LogEntry> readLogFile(File file) throws IOException {
        List<LogEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LogEntry entry = parseLine(line);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        }
        return entries;
    }

    private static LogEntry parseLine(String line) {
        try {
            // Пример: [2023-01-01 12:00:00] user1 transferred 100.00 to user2
            String[] parts = line.split("\\] ");
            if (parts.length < 2) return null;

            String timestamp = parts[0].substring(1);
            String[] dataParts = parts[1].split(" ");

            if (dataParts.length < 3) return null;

            String user = dataParts[0];
            String operation = dataParts[1];
            String amount = "";
            String recipient = null;

            if ("transferred".equals(operation) && dataParts.length >= 5) {
                amount = dataParts[2];
                recipient = dataParts[4];
            } else if (dataParts.length >= 3) {
                amount = dataParts[2];
            }

            return new LogEntry(timestamp, user, operation, amount, recipient);
        } catch (Exception e) {
            System.err.println("Error parsing line: " + line);
            return null;
        }
    }

    private static void addEntry(Map<String, List<LogEntry>> userEntries,
                                 String user, LogEntry entry) {
        userEntries.computeIfAbsent(user, k -> new ArrayList<>()).add(entry);
    }

    private static void createUserFile(String user, List<LogEntry> entries,
                                       Path outputDir) throws IOException {
        // Сортирую по дате
        entries.sort(Comparator.comparing(e -> e.timestamp));

        // Рассчитываю баланс
        double balance = 0;
        for (LogEntry entry : entries) {
            try {
                double amount = Double.parseDouble(entry.amount);
                if ("received".equals(entry.operation)) {
                    balance += amount;
                } else if ("transferred".equals(entry.operation) ||
                        "withdrew".equals(entry.operation)) {
                    balance -= amount;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid amount format in entry: " + entry);
            }
        }

        // Создаю файл
        Path userFile = outputDir.resolve(user + ".log");
        try (BufferedWriter writer = Files.newBufferedWriter(userFile)) {
            for (LogEntry entry : entries) {
                writer.write(entry.toString());
                writer.newLine();
            }

            // Добавляю итоговый баланс
            String currentTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(String.format("[%s] %s final balance %.2f",
                    currentTime, user, balance));
            writer.newLine();
        }
    }
}