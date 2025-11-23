package myhomelibrary;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import myhomelibrary.persistence.JsonStorage;
import myhomelibrary.repo.BookRepository;
import myhomelibrary.ui.BookDetailView;
import myhomelibrary.ui.MainMenuView;
import myhomelibrary.ui.BookshelfView;


public class LibraryApp extends Application {

    private Stage primaryStage;
    private BookRepository repository;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.repository = new BookRepository();

        JsonStorage.load(repository);
        showMainMenu();

        primaryStage.setTitle("My Home Library");
        primaryStage.show();
    }

    public void showMainMenu() {
        MainMenuView view = new MainMenuView(repository, this);
        Scene scene = new Scene(view.getRoot(), 730, 1100);
        primaryStage.setScene(scene);
    }

    public void showBookDetail(int index) {
        if (repository.getAll().isEmpty()) return;
        if (index < 0) index = 0;
        if (index >= repository.getAll().size()) index = repository.getAll().size() - 1;

        BookDetailView view = new BookDetailView(repository, this, index);
        Scene scene = new Scene(view.getRoot(), 730, 1100);
        primaryStage.setScene(scene);
    }
    public void showBookshelf() {
        BookshelfView view = new BookshelfView(repository, this);
        Scene scene = new Scene(view.getRoot(), 730, 1100);
        primaryStage.setScene(scene);
    }

    public BookRepository getRepository() {
        return repository;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
