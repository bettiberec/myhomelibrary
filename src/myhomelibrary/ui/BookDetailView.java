package myhomelibrary.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import myhomelibrary.LibraryApp;
import myhomelibrary.model.Book;
import myhomelibrary.repo.BookRepository;


public class BookDetailView {

    private final BorderPane root = new BorderPane();
    private final BookRepository repo;
    private final LibraryApp app;
    private int currentIndex;

    private Label titleLabel;
    private Label authorLabel;
    private Label genreLabel;
    private Label yearLabel;
    private Label statusLabel;

    private TextArea notesArea;
    private TextArea descriptionArea;

    private HBox ratingBox;
    private ImageView coverView;

    public BookDetailView(BookRepository repo, LibraryApp app, int startIndex) {
        this.repo = repo;
        this.app = app;
        this.currentIndex = startIndex;

        BackgroundImage bg = new BackgroundImage(
                new Image("file:main/page1.png", 730, 1100, false, true),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        );
        root.setBackground(new Background(bg));

        createTopBar();
        createCenterContent();
        createBottomNav();

        updateView();
    }

    private void createTopBar() {
        Button menuBtn   = new Button("Menu");
        Button editBtn   = new Button("Edit");
        Button deleteBtn = new Button("Delete");

        HBox top = new HBox(10, menuBtn, editBtn, deleteBtn);
        top.setPadding(new Insets(10));
        root.setTop(top);

        menuBtn.setOnAction(e -> app.showMainMenu());

        editBtn.setOnAction(e -> {
            Book b = getCurrent();
            if (b == null) return;

            EditBookDialog dialog = new EditBookDialog(repo, b);
            dialog.showAndWait();

            updateView();
        });

        deleteBtn.setOnAction(e -> {
            Book b = getCurrent();
            if (b == null) return;

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete book \"" + b.getTitle() + "\" ?",
                    ButtonType.OK, ButtonType.CANCEL);
            confirm.setHeaderText("Confirm delete");
            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.OK) {
                    repo.remove(b);
                    if (repo.getAll().isEmpty()) {
                        app.showMainMenu();
                    } else {
                        if (currentIndex >= repo.getAll().size()) {
                            currentIndex = repo.getAll().size() - 1;
                        }
                        updateView();
                    }
                }
            });
        });
    }

    private void createCenterContent() {

        coverView = new ImageView();
        coverView.setFitWidth(220);
        coverView.setFitHeight(300);
        VBox left = new VBox(coverView);
        left.setPadding(new Insets(30, 20, 30, 40));
        left.setAlignment(Pos.TOP_CENTER);
        root.setLeft(left);

        VBox right = new VBox(10);
        right.setPadding(new Insets(30, 40, 30, 10));

        titleLabel  = new Label("Book title");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");

        authorLabel = new Label("Author");
        genreLabel  = new Label("Genre");
        yearLabel   = new Label("Year");
        statusLabel = new Label("Status");

        Label ratingLbl = new Label("Rating:");
        ratingBox = createRatingBox();

        Label notesLbl = new Label("Notes:");
        notesArea = new TextArea();
        notesArea.setPrefRowCount(3);

        Label descLbl = new Label("Summary:");
        descriptionArea = new TextArea();
        descriptionArea.setPrefRowCount(8);

        right.getChildren().addAll(
                titleLabel,
                authorLabel,
                genreLabel,
                yearLabel,
                statusLabel,
                ratingLbl,
                ratingBox,
                notesLbl,
                notesArea,
                descLbl,
                descriptionArea
        );

        root.setCenter(right);
    }

    private void createBottomNav() {
        Button prevBtn = new Button("Prev");
        Button nextBtn = new Button("Next");

        HBox bottom = new HBox();
        bottom.setPadding(new Insets(10, 40, 20, 40));
        bottom.setAlignment(Pos.CENTER);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        bottom.getChildren().addAll(prevBtn, spacer, nextBtn);
        root.setBottom(bottom);

        prevBtn.setOnAction(e -> {
            if (currentIndex > 0) {
                currentIndex--;
                updateView();
            }
        });

        nextBtn.setOnAction(e -> {
            if (currentIndex < repo.getAll().size() - 1) {
                currentIndex++;
                updateView();
            }
        });
    }

    private HBox createRatingBox() {
        HBox box = new HBox(5);
        for (int i = 1; i <= 5; i++) {
            final int value = i;
            Label star = new Label("☆");
            star.setStyle("-fx-font-size: 22px;");

            star.setOnMouseClicked(e -> {
                Book b = getCurrent();
                if (b != null) {
                    b.setRating(value);
                    updateStars(box, value);
                }
            });

            box.getChildren().add(star);
        }
        return box;
    }

    private void updateStars(HBox box, double rating) {
        int full = (int) Math.round(rating);
        for (int i = 0; i < box.getChildren().size(); i++) {
            Label star = (Label) box.getChildren().get(i);
            star.setText(i < full ? "★" : "☆");
        }
    }

    private Book getCurrent() {
        if (repo.getAll().isEmpty()) return null;
        return repo.getAll().get(currentIndex);
    }

    private void updateView() {
        Book b = getCurrent();
        if (b == null) return;

        titleLabel.setText(b.getTitle());
        authorLabel.setText("Author: " + b.getAuthor());
        genreLabel.setText("Genre: " + (b.getGenre() == null ? "" : b.getGenre()));
        yearLabel.setText("Year: " + (b.getYear() == 0 ? "" : b.getYear()));
        statusLabel.setText("Status: " + (b.getStatus() == null ? "" : b.getStatus().name()));

        notesArea.setText(b.getNotes() == null ? "" : b.getNotes());
        descriptionArea.setText(b.getSummary() == null ? "" : b.getSummary());

        updateStars(ratingBox, b.getRating());

        Image img;
        if (b.getCoverPath() != null && !b.getCoverPath().isBlank()) {
            img = new Image("file:" + b.getCoverPath(), 220, 300, false, true);
        } else {
            img = new Image("file:main/graphics/default_cover.png", 220, 300, false, true);
        }
        coverView.setImage(img);
    }

    public BorderPane getRoot() {
        return root;
    }
}
