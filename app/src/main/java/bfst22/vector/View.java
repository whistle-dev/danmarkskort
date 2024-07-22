package bfst22.vector;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class View {

    public View(Model model, Stage primaryStage) throws IOException {

        primaryStage.show();
        FXMLLoader loader = new FXMLLoader(View.class.getResource("View.fxml"));
        primaryStage.getIcons().add(new Image(View.class.getResourceAsStream("images/icon.png")));
        primaryStage.setScene(loader.load());
        Controller controller = loader.getController();
        controller.init(model);
        primaryStage.setTitle("MapIT");
        primaryStage.setMaximized(true);
    }
}
