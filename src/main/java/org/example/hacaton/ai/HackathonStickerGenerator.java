package org.example.hacaton.ai;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import java.net.http.*;
import java.net.URI;

public class HackathonStickerGenerator {

    private static HackathonStickerGenerator instance;

    private static final String API_URL = "https://api-inference.huggingface.co/models/CompVis/stable-diffusion-v1-4";
    private static final String API_KEY = "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"; // Используйте свой ключ API
    private static final String IMAGES_DIR = "hackathon_images"; // Папка для сохранения стикеров
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_DELAY_MS = 5000;
    private static final int MAX_WAIT_TIME = 30000;

    static {
        try {
            Files.createDirectories(Paths.get(IMAGES_DIR));
        } catch (IOException e) {
            System.err.println("Не удалось создать директорию для изображений: " + e.getMessage());
        }
    }

    private HackathonStickerGenerator() {}

    public static HackathonStickerGenerator getInstance() {
        if (instance == null) {
            instance = new HackathonStickerGenerator();
        }
        return instance;
    }

    public String generateHackathonSticker(String name, String description) throws IOException {
        System.out.println("Генерируется стикер для хакатона: " + name);

        // Формируем запрос для генерации изображения
        String prompt = String.format("Sticker design for hackathon with name '%s' and description '%s'. High quality, professional design, modern style, 4k resolution, bright colors", name, description);
        byte[] imageData = generateImageWithRetry(prompt);

        // Создаем изображение из полученных данных
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

        // Сохраняем изображение в файл
        String fileName = name.toLowerCase().replace(" ", "_") + "_sticker.png";
        Path imagePath = Paths.get(IMAGES_DIR, fileName);
        ImageIO.write(image, "PNG", imagePath.toFile());

        System.out.println("Стикер сохранен: " + imagePath.getFileName());
        return imagePath.toString();
    }

    private byte[] generateImageWithRetry(String prompt) throws IOException {
        IOException lastException = null;
        long startTime = System.currentTimeMillis();

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                return generateImage(prompt);
            } catch (IOException e) {
                lastException = e;
                if (e.getMessage().contains("503") && e.getMessage().contains("loading")) {
                    if (System.currentTimeMillis() - startTime > MAX_WAIT_TIME) {
                        throw new IOException("Превышено максимальное время ожидания загрузки модели", e);
                    }
                    System.out.printf("Попытка %d из %d: Модель загружается, ожидание %d секунд...%n",
                            attempt, MAX_RETRIES, RETRY_DELAY_MS/1000);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Прервано ожидание повторной попытки", ie);
                    }
                } else {
                    break;
                }
            }
        }

        try {
            return generateImageFallback(prompt);
        } catch (IOException e) {
            throw lastException;
        }
    }

    private static byte[] generateImage(String prompt) throws IOException {
        String requestBody = "{\"inputs\":\"" + prompt + "\"}";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .header("Accept", "image/png")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                byte[] body = response.body();
                if (body == null || body.length == 0) {
                    throw new IOException("Получен пустой ответ от API");
                }
                return body;
            } else {
                String errorBody = new String(response.body());
                throw new IOException("Ошибка API: " + response.statusCode() + ", " + errorBody);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Запрос был прерван", e);
        }
    }

    private static byte[] generateImageFallback(String prompt) throws IOException {
        String fallbackUrl = "https://api-inference.huggingface.co/models/runwayml/stable-diffusion-v1-5";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fallbackUrl))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .header("Accept", "image/png")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"inputs\":\"" + prompt + "\"}"))
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                byte[] body = response.body();
                if (body == null || body.length == 0) {
                    throw new IOException("Получен пустой ответ от резервного API");
                }
                return body;
            } else {
                throw new IOException("Ошибка резервного API: " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Запрос к резервному API был прерван", e);
        }
    }
}