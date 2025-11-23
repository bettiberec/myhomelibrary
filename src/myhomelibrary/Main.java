package myhomelibrary;

import myhomelibrary.persistence.JsonStorage;
import myhomelibrary.repo.BookRepository;

public class Main {
    public static void main(String[] args){
        BookRepository repo = new BookRepository();
        JsonStorage.load(repo);

        System.out.println("Books loaded: " + repo.getAll().size());
    }
}