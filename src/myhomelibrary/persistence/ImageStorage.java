package myhomelibrary.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class ImageStorage {

    private static final String COVER_DIR = "cover";
    private static final String SPINE_DIR = "spine";

    private static void ensureDir(String dir) throws IOException {
        Path path = Paths.get(dir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    public static String saveCover(File source, String bookId) throws IOException {
        return saveImage(source, bookId, COVER_DIR, "cover");
    }

    public static String saveSpine(File source, String bookId) throws IOException {
        return saveImage(source, bookId, SPINE_DIR, "spine");
    }

    private static String saveImage(File source,
                                    String bookId,
                                    String dir,
                                    String type) throws IOException {
        if (source == null) {
            return null;
        }

        ensureDir(dir);

        String extension = getExtension(source.getName());
        if (extension == null || extension.isBlank()) {
            extension = "jpg";
        }

        String fileName = String.format("%s_%s.%s", bookId, type, extension);
        Path target = Paths.get(dir, fileName);

        Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

        return target.toString().replace("\\", "/");
    }

    private static String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot == -1) return null;
        return fileName.substring(dot + 1);
    }
}
