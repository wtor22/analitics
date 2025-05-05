package quartztop.analitics.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path destinationPath = Paths.get(uploadDir).resolve(fileName).normalize();

        System.out.println("Сохраняем файл: " + destinationPath);

        Files.createDirectories(destinationPath.getParent());
        file.transferTo(destinationPath.toFile());

        return "/uploads/" + fileName;
    }

    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        // Вырезаем имя файла из URL (например, из "/uploads/abc.jpg" → "abc.jpg")
        String filename = Paths.get(imageUrl).getFileName().toString();

        // Путь к файлу на диске
        Path filePath = Paths.get(uploadDir).resolve(filename).normalize();

        System.out.println("Удаляем файл: " + filePath);
        // Удаляем, если существует
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}


