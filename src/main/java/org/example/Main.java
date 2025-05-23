package org.example;

import logParsers.LogParser;
import java.io.File;
import java.io.IOException;
import java.util.List;


import static readLogDirectory.logReader.readLogDirectory;

public class Main {
    public static void main(String[] args) {
        try {
            String logDirPath = "./logs";
            List<File> logFiles = readLogDirectory(logDirPath);

            // Вывод информации о файлах
            System.out.println("Найдено файлов: " + logFiles.size());
            for (File file : logFiles) {
                System.out.println("Имя файла: " + file.getName());
                System.out.println("Путь: " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
        try {
            String logDirPath = "./logs";
            LogParser.processUserLogs(logDirPath);
            System.out.println("Обработка завершена успешно!");
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
