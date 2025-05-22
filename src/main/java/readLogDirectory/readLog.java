package readLogDirectory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class readLog {
    public static List<File> readLogDirectory(String dirPath) throws IOException {
        Path dir = Paths.get(dirPath);

        //проверка директории
        if (!Files.exists(dir)){
            throw new IOException("Дириктория не существует: " + dirPath);
        }
        if (!Files.isDirectory(dir)) {
            throw new IOException("Путь не является директорией" + dirPath);
        }
        // Получение .log файлов
        return Files.list(dir)
                .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".log"))
                .map(Path::toFile)
                .collect(Collectors.toList());
    }

}
