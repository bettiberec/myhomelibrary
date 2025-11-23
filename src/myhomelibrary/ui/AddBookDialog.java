package myhomelibrary.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import myhomelibrary.model.Book;
import myhomelibrary.model.ReadingStatus;
import myhomelibrary.persistence.ImageStorage;
import myhomelibrary.repo.BookRepository;
import java.io.File;
import java.io.IOException;

public class AddBookDialog extends Stage {

    private File coverFile;
    private File spineFile;

    public AddBookDialog(BookRepository repo) {
        setTitle("Add new book");

        TextField titleField = new TextField();
        TextField authorField = new TextField();
        TextField genreField = new TextField();
        TextField yearField = new TextField();

        TextArea summaryArea = new TextArea();
        summaryArea.setPrefRowCount(4);

        ComboBox<ReadingStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(ReadingStatus.TO_READ, ReadingStatus.READING, ReadingStatus.READ);
        statusBox.getSelectionModel().select(ReadingStatus.TO_READ);

        Button chooseCoverBtn = new Button("Choose cover image...");
        Button chooseSpineBtn = new Button("Choose spine image...");

        Label coverLabel = new Label("No file selected");
        Label spineLabel = new Label("No file selected");

        chooseCoverBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png")
            );
            File file = fc.showOpenDialog(this);
            if (file != null) {
                coverFile = file;
                coverLabel.setText(file.getName());
            }
        });

        chooseSpineBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png")
            );
            File file = fc.showOpenDialog(this);
            if (file != null) {
                spineFile = file;
                spineLabel.setText(file.getName());
            }
        });

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.setPadding(new Insets(10));

        int row = 0;
        form.add(new Label("Title:"), 0, row);
        form.add(titleField, 1, row++);

        form.add(new Label("Author:"), 0, row);
        form.add(authorField, 1, row++);

        form.add(new Label("Genre:"), 0, row);
        form.add(genreField, 1, row++);

        form.add(new Label("Year:"), 0, row);
        form.add(yearField, 1, row++);

        form.add(new Label("Status:"), 0, row);
        form.add(statusBox, 1, row++);

        form.add(new Label("Summary:"), 0, row);
        form.add(summaryArea, 1, row++);
        GridPane.setHgrow(summaryArea, Priority.ALWAYS);

        HBox coverBox = new HBox(10, chooseCoverBtn, coverLabel);
        HBox spineBox = new HBox(10, chooseSpineBtn, spineLabel);

        VBox root = new VBox(10, form, coverBox, spineBox);
        root.setPadding(new Insets(10));

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");

        HBox buttons = new HBox(10, saveBtn, cancelBtn);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        root.getChildren().add(buttons);

        saveBtn.setOnAction(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();

            if (title.isEmpty() || author.isEmpty()) {
                new Alert(Alert.AlertType.WARNING,
                        "Title and author are required.",
                        ButtonType.OK).showAndWait();
                return;
            }

            int year = 0;
            String yearText = yearField.getText().trim();
            if (!yearText.isEmpty()) {
                try {
                    year = Integer.parseInt(yearText);
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.WARNING,
                            "Year must be a number.",
                            ButtonType.OK).showAndWait();
                    return;
                }
            }

            String id = repo.generateNextId();
            Book book = new Book(id, title, author);
            book.setGenre(genreField.getText().trim());
            book.setYear(year);
            book.setSummary(summaryArea.getText().trim());
            book.setStatus(statusBox.getValue());

            try {
                if (coverFile != null) {
                    String coverPath = ImageStorage.saveCover(coverFile, id);
                    book.setCoverPath(coverPath);
                }
                if (spineFile != null) {
                    String spinePath = ImageStorage.saveSpine(spineFile, id);
                    book.setSpinePath(spinePath);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR,
                        "Error while saving images.",
                        ButtonType.OK).showAndWait();
                return;
            }

            repo.add(book);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Book saved");
            alert.setHeaderText("Success");
            alert.setContentText("The book has been saved successfully!");
            alert.showAndWait();
            close();
        });

        cancelBtn.setOnAction(e -> close());

        Scene scene = new Scene(root, 900, 450);
        setScene(scene);
    }
}
