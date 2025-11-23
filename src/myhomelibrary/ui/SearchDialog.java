package myhomelibrary.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import myhomelibrary.LibraryApp;
import myhomelibrary.model.Book;
import myhomelibrary.model.ReadingStatus;
import myhomelibrary.repo.BookRepository;

import java.util.List;

public class SearchDialog extends Stage {

    private final BookRepository repo;
    private final LibraryApp app;

    private final TextField keywordField = new TextField();
    private final TextField genreField = new TextField();
    private final ComboBox<ReadingStatus> statusBox = new ComboBox<>();

    private final ListView<Book> resultsView = new ListView<>();
    private final ObservableList<Book> results = FXCollections.observableArrayList();

    public SearchDialog(BookRepository repo, LibraryApp app) {
        this.repo = repo;
        this.app = app;

        setTitle("Find book");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        GridPane filters = new GridPane();
        filters.setHgap(10);
        filters.setVgap(8);

        int row = 0;

        filters.add(new Label("Title / Author:"), 0, row);
        filters.add(keywordField, 1, row++);

        filters.add(new Label("Genre:"), 0, row);
        filters.add(genreField, 1, row++);

        filters.add(new Label("Status:"), 0, row);
        statusBox.getItems().addAll(ReadingStatus.TO_READ, ReadingStatus.READING, ReadingStatus.READ);
        statusBox.getItems().add(0, null);
        statusBox.getSelectionModel().selectFirst();
        filters.add(statusBox, 1, row++);

        Button searchBtn = new Button("Search");
        filters.add(searchBtn, 1, row);

        root.setTop(filters);

        resultsView.setItems(results);
        resultsView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle() + " — " + item.getAuthor());
                }
            }
        });

        root.setCenter(resultsView);

        Button openBtn = new Button("Open");
        Button closeBtn = new Button("Close");

        HBox bottom = new HBox(10, openBtn, closeBtn);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottom);

        searchBtn.setOnAction(e -> performSearch());
        openBtn.setOnAction(e -> openSelected());
        closeBtn.setOnAction(e -> close());

        resultsView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                openSelected();
            }
        });

        Scene scene = new Scene(root, 500, 400);
        setScene(scene);
    }

    private void performSearch() {
        String keyword = keywordField.getText().trim();
        String genre = genreField.getText().trim();
        ReadingStatus status = statusBox.getValue();

        if (status == null) {
        }

        List<Book> found = repo.search(
                keyword.isEmpty() ? null : keyword,
                genre.isEmpty() ? null : genre,
                status
        );

        results.setAll(found);

        if (found.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION,
                    "No books matched your search.",
                    ButtonType.OK).showAndWait();
        }
    }

    private void openSelected() {
        Book selected = resultsView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        int index = repo.getAll().indexOf(selected);
        if (index >= 0) {
            close();
            app.showBookDetail(index);
        }
    }
}
