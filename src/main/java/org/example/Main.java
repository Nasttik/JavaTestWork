package org.example;

import logParsers.logParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) {
        try {
            String logDirPath = "./logs";
            List<File> logFiles = readLogDirectory(logDirPath);

            System.out.println("Найдено файлов: " + logFiles.size());

            for (File file : logFiles) {
                System.out.println("\nОбработка файла: " + file.getName());

                List<logParser.LogEntry> entries = logParser.parseLogFile(file.getAbsolutePath());
                for (logParser.LogEntry entry : entries) {
                    System.out.println(entry);
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
            }

    public static List<File> readLogDirectory(String dirPath) throws IOException {
        Path dir = Paths.get(dirPath);

        if (!Files.exists(dir)) {
            throw new IOException("Директория не существует: " + dirPath);
        }
        if (!Files.isDirectory(dir)) {
            throw new IOException("Указанный путь не является директорией: " + dirPath);
        }


        return Files.list(dir)
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".log"))
                .map(Path::toFile)
                .collect(Collectors.toList());
    }
}