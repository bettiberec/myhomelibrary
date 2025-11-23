package myhomelibrary.ui;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import myhomelibrary.LibraryApp;
import myhomelibrary.persistence.JsonStorage;
import myhomelibrary.repo.BookRepository;


public class MainMenuView {

    private final BorderPane root = new BorderPane();

    public MainMenuView(BookRepository repo, LibraryApp app) {

        BackgroundImage bg = new BackgroundImage(
                new Image("file:main/main.png"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(
                        BackgroundSize.AUTO,
                        BackgroundSize.AUTO,
                        false,
                        false,
                        true,
                        true
                )
        );
        root.setBackground(new Background(bg));

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);

        Button addBtn = createImageButton(
                "main/graphics/btn_add.png",
                "main/graphics/btn_add_hover.png",
                () -> {
                    AddBookDialog dialog = new AddBookDialog(repo);
                    dialog.showAndWait();
                }
        );

        Button findBtn = createImageButton(
                "main/graphics/btn_find.png",
                "main/graphics/btn_find_hover.png",
                () -> {
                    if(repo.getAll().isEmpty()){
                        new Alert(Alert.AlertType.INFORMATION,
                                "No books in the library yet.",
                                ButtonType.OK).showAndWait();
                        return;
                    }
                    SearchDialog dialog = new SearchDialog(repo, app);
                    dialog.showAndWait();
                }
        );

        Button shelfBtn = createImageButton(
                "main/graphics/btn_shelf.png",
                "main/graphics/btn_shelf_hover.png",
                () -> app.showBookshelf()
        );

        Button saveBtn = createImageButton(
                "main/graphics/btn_save.png",
                "main/graphics/btn_save_hover.png",
                () -> JsonStorage.save(repo)
        );

        Button exitBtn = createImageButton(
                "main/graphics/btn_exit.png",
                "main/graphics/btn_exit_hover.png",
                () -> {
                    JsonStorage.save(repo);
                    System.exit(0);
                }
        );

        box.getChildren().addAll(addBtn, findBtn, shelfBtn, saveBtn, exitBtn);
        root.setCenter(box);
    }

    public BorderPane getRoot() {
        return root;
    }

    private Button createImageButton(String normalPath,
                                     String hoverPath,
                                     Runnable action) {

        Image normal = new Image("file:" + normalPath);
        Image hover = new Image("file:" + hoverPath);

        ImageView view = new ImageView(normal);
        view.setPreserveRatio(true);
        view.setFitWidth(220);

        Button button = new Button();
        button.setGraphic(view);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        button.setOnMouseEntered(e -> view.setImage(hover));
        button.setOnMouseExited(e -> view.setImage(normal));

        if (action != null) {
            button.setOnAction(e -> action.run());
        }

        return button;
    }
}
