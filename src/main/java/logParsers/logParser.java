package logParsers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class logParser {

    public static class LogEntry {
        private final String timestamp;
        private final String user;
        private final String operation;
        private final String amount;
        private final String recipient;

        public LogEntry(String timestamp, String user, String operation,
                        String amount, String recipient) {
            this.timestamp = timestamp;
            this.user = user;
            this.operation = operation;
            this.amount = amount;
            this.recipient = recipient;
        }
        //переодпределение метода класса
        @Override
        public String toString() {
            return String.format("[%s] %s %s %s%s",
                    timestamp, user, operation, amount,
                    recipient != null ? " to " + recipient : "");
        }
    }

    public static List<LogEntry> parseLogFile(String filePath) {
        List<LogEntry> entries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                LogEntry entry = parseLine(line);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        return entries;
    }

    private static LogEntry parseLine(String line) {
        try {
            // парсинг
            int firstBracket = line.indexOf('[');
            int lastBracket = line.indexOf(']');

            String timestamp = line.substring(firstBracket + 1, lastBracket).trim();
            String remaining = line.substring(lastBracket + 1).trim();

            String[] parts = remaining.split(" ");
            if (parts.length < 4) return null;

            String user = parts[0];
            String operation = parts[1] + (parts.length > 4 ? " " + parts[2] : "");
            String amount = parts[parts.length > 4 ? 3 : 2];
            String recipient = null;

            if (operation.contains("transferred")) {
                recipient = parts[parts.length - 1];
            }

            return new LogEntry(timestamp, user, operation, amount, recipient);
        } catch (Exception e) {
            System.out.println("Ошибка разбора строки: " + line);
            return null;
        }
    }
}