package org.example;
import java.io.File;          // Для работы с файлами (класс File)
import java.io.IOException;   // Для обработки исключений ввода/вывода
import java.nio.file.Files;   // Утилиты для работы с файлами (NIO)
import java.nio.file.Path;    // Интерфейс для представления путей
import java.nio.file.Paths;   // для создания Path объектов
import java.util.List;        // Интерфейс списка
import java.util.stream.Collectors; // Для работы со Stream API

import static readLogDirectory.logReader.readLogDirectory;

public class Main {
    public static void main(String[] args) {
        try {
            // Указываю путь к директории с логами
            String logDirPath = "./logs";

            // Получаю список .log файлов
            List<File> logFiles = readLogDirectory(logDirPath);

            // Вывожу информацию о файлах
            System.out.println("Найдено файлов: " + logFiles.size());
            for (File file : logFiles) {
                System.out.println("Имя файла: " + file.getName());
                System.out.println("Путь: " + file.getAbsolutePath());
            }

        } catch (IOException e) {
            // Обработка возможных ошибок
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}