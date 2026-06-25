package event.sync.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ImageService {
    private final List<String> ALLOWED_MIME_TYPE = List.of("image/jpeg", "image/jpg", "image/png");

    public File saveImage(String base64Image) {
        String fileMimeType = base64Image.substring(base64Image.indexOf(':') + 1, base64Image.indexOf(";"));
        if (!ALLOWED_MIME_TYPE.contains(fileMimeType)) {
            throw new IllegalArgumentException("Invalid mime type: " + fileMimeType);
        }

        String fileData = base64Image.substring(base64Image.lastIndexOf(',') + 1);
        try {
            Path savePath = Paths.get(System.getProperty("user.dir"), "speaker_images");
            if (!Files.exists(savePath)) {
                Files.createDirectory(Paths.get(System.getProperty("user.dir"), "speaker_images"));
            }

            File imageDirectory = Paths.get(System.getProperty("user.dir"), "speaker_images").toFile();
            String fileName = UUID.randomUUID() + "." + fileMimeType.split("/")[1];
            byte[] fileBytes = Base64.getDecoder().decode(fileData);

            File file = new File(imageDirectory, fileName);
            return Files.write(file.toPath(), fileBytes).toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBase64Image(String fileName) {
        try {
            File file = Paths.get(System.getProperty("user.dir"), "speaker_images", fileName).toFile();
            return "data:image/"+ fileName.substring(fileName.lastIndexOf('.') + 1) +
                    ";base64," +
                    Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
        } catch (IOException ignored) {
            return null;
        }
    }

    public void deleteImage(String fileName) {
        File file = Paths.get(System.getProperty("user.dir"), "speaker_images", fileName).toFile();
        if (file.exists()) {
            file.delete();
        };
    }
}

