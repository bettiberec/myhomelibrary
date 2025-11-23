package myhomelibrary.ui;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import myhomelibrary.LibraryApp;
import myhomelibrary.model.Book;
import myhomelibrary.model.ReadingStatus;
import myhomelibrary.persistence.JsonStorage;
import myhomelibrary.repo.BookRepository;

import java.util.Collections;
import java.util.List;

public class BookshelfView {

    private static final int COLUMNS = 10;
    private static final int ROWS = 5;

    private static final double SPINE_WIDTH = 65;
    private static final double SPINE_HEIGHT = 200;

    private static final double LEFT_MARGIN = 20;
    private static final double TOP_MARGIN = 8;
    private static final double H_GAP = 10;
    private static final double V_GAP = 25;

    private final BorderPane root = new BorderPane();
    private final LibraryApp app;
    private final BookRepository repo;
    private final Pane shelfPane = new Pane();

    private List<Book> readBooks;
    private VBox buttonBox;
    private boolean editMode = false;

    public BookshelfView(BookRepository repo, LibraryApp app) {
        this.repo = repo;
        this.app = app;

        // háttér
        BackgroundImage bg = new BackgroundImage(
                new Image("file:main/shelf.png"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT
        );
        root.setBackground(new Background(bg));
        root.setCenter(shelfPane);

        readBooks = repo.getByStatus(ReadingStatus.READ);

        renderShelf();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void renderShelf() {
        shelfPane.getChildren().clear();

        int cellIndex = 0;

        for (int bookIndex = 0; bookIndex < readBooks.size(); bookIndex++) {

            if (cellIndex == ROWS * COLUMNS - 1) {
                cellIndex++;
            }

            if (cellIndex >= ROWS * COLUMNS) {
                break;
            }

            Book book = readBooks.get(bookIndex);
            String spinePath = book.getSpinePath();

            Image spineImage;
            if (spinePath != null && !spinePath.isBlank()) {
                spineImage = new Image("file:" + spinePath);
            } else {
                spineImage = new Image("file:main/graphics/default_spine.png");
            }

            ImageView spineView = new ImageView(spineImage);
            spineView.setFitWidth(SPINE_WIDTH);
            spineView.setFitHeight(SPINE_HEIGHT);
            decorateSpine(spineView);

            int row = cellIndex / COLUMNS;
            int col = cellIndex % COLUMNS;

            double x = LEFT_MARGIN + col * (SPINE_WIDTH + H_GAP);
            double y = TOP_MARGIN + row * (SPINE_HEIGHT + V_GAP);

            spineView.setLayoutX(x);
            spineView.setLayoutY(y);

            final int currentIndex = bookIndex;

            // katt → detailed view
            spineView.setOnMouseClicked(e -> {
                if (!editMode) {
                    int indexInRepo = repo.getAll().indexOf(book);
                    if (indexInRepo >= 0) {
                        app.showBookDetail(indexInRepo);
                    }
                }
            });

            spineView.setOnDragDetected(e -> {
                if (!editMode) return;
                Dragboard db = spineView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(String.valueOf(currentIndex));
                db.setContent(content);
                e.consume();
            });

            spineView.setOnDragOver(e -> {
                if (!editMode) return;
                Dragboard db = e.getDragboard();
                if (e.getGestureSource() != spineView && db.hasString()) {
                    e.acceptTransferModes(TransferMode.MOVE);
                }
                e.consume();
            });

            spineView.setOnDragDropped(e -> {
                if (!editMode) return;
                Dragboard db = e.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    int fromIndex = Integer.parseInt(db.getString());
                    int toIndex = currentIndex;

                    if (fromIndex != toIndex &&
                            fromIndex >= 0 && fromIndex < readBooks.size() &&
                            toIndex >= 0 && toIndex < readBooks.size()) {

                        Collections.swap(readBooks, fromIndex, toIndex);
                        renderShelf();
                        success = true;
                    }
                }
                e.setDropCompleted(success);
                e.consume();
            });

            shelfPane.getChildren().add(spineView);
            cellIndex++;
        }
        createOrUpdateButtons();
    }

    private void createOrUpdateButtons() {
        double buttonCellX = LEFT_MARGIN + (COLUMNS - 1) * (SPINE_WIDTH + H_GAP) - 130;
        double buttonCellY = TOP_MARGIN + (ROWS - 1) * (SPINE_HEIGHT + V_GAP) - 5;

        if (buttonBox == null) {
            buttonBox = new VBox(8);
            buttonBox.setPadding(new Insets(10));

            Button backBtn = new Button("Back to menu");
            backBtn.setOnAction(e -> app.showMainMenu());
            styleControlButton(backBtn);

            Button editLayoutBtn = new Button("Edit layout");
            styleControlButton(editLayoutBtn);

            Button saveLayoutBtn = new Button("Save");
            styleControlButton(saveLayoutBtn);

            editLayoutBtn.setOnAction(e -> {
                editMode = !editMode;
                editLayoutBtn.setText(editMode ? "Done" : "Edit layout");

                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        editMode
                                ? "Layout edit mode ON.\nDrag books to reorder them."
                                : "Layout edit mode OFF.");
                alert.setHeaderText(null);
                alert.showAndWait();
            });

            saveLayoutBtn.setOnAction(e -> {
                for (int i = 0; i < readBooks.size(); i++) {
                    readBooks.get(i).setShelfIndex(i);
                }
                JsonStorage.save(repo);
                new Alert(Alert.AlertType.INFORMATION,
                        "Bookshelf layout saved.",
                        ButtonType.OK).showAndWait();
            });

            buttonBox.getChildren().addAll(backBtn, editLayoutBtn, saveLayoutBtn);
        }

        buttonBox.setLayoutX(buttonCellX);
        buttonBox.setLayoutY(buttonCellY);

        if (!shelfPane.getChildren().contains(buttonBox)) {
            shelfPane.getChildren().add(buttonBox);
        }
    }

    private void styleControlButton(Button btn) {
        String baseStyle =
                "-fx-background-color: rgba(0,0,0,0.25);" +
                        "-fx-text-fill: #f5d28b;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #f5d28b;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 6 14 6 14;";

        String hoverStyle =
                "-fx-background-color: rgba(0,0,0,0.45);" +
                        "-fx-text-fill: #ffe7ac;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-family: 'Georgia';" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #ffe7ac;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 6 14 6 14;";

        btn.setStyle(baseStyle);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(8);
        shadow.setOffsetY(2);
        shadow.setColor(Color.color(0, 0, 0, 0.55));
        btn.setEffect(shadow);

        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(baseStyle));

        btn.setPrefWidth(140);
        btn.setMinWidth(140);
        btn.setMaxWidth(140);
    }

    private void decorateSpine(ImageView spineView) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetY(4);
        shadow.setColor(Color.color(0, 0, 0, 0.6));
        spineView.setEffect(shadow);

        spineView.setScaleX(1.0);
        spineView.setScaleY(1.0);

        ScaleTransition hoverIn = new ScaleTransition(Duration.millis(120), spineView);
        hoverIn.setToX(1.05);
        hoverIn.setToY(1.05);

        ScaleTransition hoverOut = new ScaleTransition(Duration.millis(120), spineView);
        hoverOut.setToX(1.0);
        hoverOut.setToY(1.0);

        spineView.setOnMouseEntered(e -> hoverIn.playFromStart());
        spineView.setOnMouseExited(e -> hoverOut.playFromStart());
    }
}
