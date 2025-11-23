package myhomelibrary.repo;

import myhomelibrary.model.Book;
import myhomelibrary.model.ReadingStatus;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Comparator;

public class BookRepository {
    private final List<Book> books = new ArrayList<>();

    public List<Book> getAll(){
        return Collections.unmodifiableList(books);
    }

    public void add(Book book){
        books.add(book);
    }

    public void remove(Book book){
        books.remove(book);
    }

    public Optional<Book> findById(String id){
        return books.stream()
                .filter(b -> Objects.equals(b.getId(), id))
                .findFirst();
    }

    public List<Book> search(String keyword, String genre, ReadingStatus status){
        return books.stream()
                .filter(b -> keyword == null || keyword.isBlank()
                        || b.getTitle().toLowerCase().contains(keyword.toLowerCase())
                        || b.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
                .filter(b -> genre == null || genre.isBlank()
                        || genre.equalsIgnoreCase(b.getGenre()))
                .filter(b -> status == null || b.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void clear(){
        books.clear();
    }

    public void addAll(Collection<Book> list){
        books.addAll(list);
    }

    public String generateNextId() {
        int max = 0;

        for (Book b : books) {
            String id = b.getId();
            if (id == null) continue;
            try {
                int n = Integer.parseInt(id);
                if (n > max) {
                    max = n;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        int next = max + 1;
        return String.format("%02d", next);
    }

    public List<Book> getByStatus(ReadingStatus status) {
        return books.stream()
                .filter(b -> b.getStatus() == status)
                .sorted(Comparator.comparingInt(Book::getShelfIndex))
                .collect(Collectors.toList());
    }

}