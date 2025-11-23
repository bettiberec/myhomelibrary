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

public class EditBookDialog extends Stage {

    private File newCoverFile;
    private File newSpineFile;

    public EditBookDialog(BookRepository repo, Book book) {
        setTitle("Edit book");

        TextField titleField = new TextField(book.getTitle());
        TextField authorField = new TextField(book.getAuthor());
        TextField genreField = new TextField(book.getGenre() == null ? "" : book.getGenre());
        TextField yearField = new TextField(book.getYear() == 0 ? "" : String.valueOf(book.getYear()));

        TextArea summaryArea = new TextArea(book.getSummary() == null ? "" : book.getSummary());
        summaryArea.setPrefRowCount(4);

        ComboBox<ReadingStatus> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(ReadingStatus.TO_READ, ReadingStatus.READING, ReadingStatus.READ);
        if (book.getStatus() != null) {
            statusBox.getSelectionModel().select(book.getStatus());
        } else {
            statusBox.getSelectionModel().select(ReadingStatus.TO_READ);
        }

        Button chooseCoverBtn = new Button("Change cover...");
        Button chooseSpineBtn = new Button("Change spine...");

        Label coverLabel = new Label(book.getCoverPath() == null ? "No cover" : book.getCoverPath());
        Label spineLabel = new Label(book.getSpinePath() == null ? "No spine" : book.getSpinePath());

        chooseCoverBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png")
            );
            File file = fc.showOpenDialog(this);
            if (file != null) {
                newCoverFile = file;
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
                newSpineFile = file;
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

            book.setTitle(title);
            book.setAuthor(author);
            book.setGenre(genreField.getText().trim());
            book.setYear(year);
            book.setSummary(summaryArea.getText().trim());
            book.setStatus(statusBox.getValue());

            String id = book.getId();
            try {
                if (newCoverFile != null) {
                    String coverPath = ImageStorage.saveCover(newCoverFile, id);
                    book.setCoverPath(coverPath);
                }
                if (newSpineFile != null) {
                    String spinePath = ImageStorage.saveSpine(newSpineFile, id);
                    book.setSpinePath(spinePath);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR,
                        "Error while saving images.",
                        ButtonType.OK).showAndWait();
                return;
            }

            new Alert(Alert.AlertType.INFORMATION,
                    "Book updated successfully.",
                    ButtonType.OK).showAndWait();

            close();
        });

        cancelBtn.setOnAction(e -> close());

        Scene scene = new Scene(root, 500, 450);
        setScene(scene);
    }
}
