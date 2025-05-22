package org.example;

import logParsers.LogParser;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            String logDirPath = "./logs";
            LogParser.processUserLogs(logDirPath);
            System.out.println("Обработка завершена успешно!");
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}