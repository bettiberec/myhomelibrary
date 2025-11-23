package myhomelibrary.persistence;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import myhomelibrary.model.Book;
import myhomelibrary.repo.BookRepository;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class JsonStorage {
    private static final String FILE_NAME = "books.json";
    private static final Gson GSON = new Gson();

    public static void load(BookRepository repo) {
        Path path = Paths.get(FILE_NAME);
        if (!Files.exists(path)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Type listType = new TypeToken<List<Book>>(){}.getType();
            List<Book> books = GSON.fromJson(reader, listType);
            if (books != null) {
                repo.clear();
                repo.addAll(books);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save(BookRepository repo) {
        Path path = Paths.get(FILE_NAME);

        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(repo.getAll(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
