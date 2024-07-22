package bfst22.vector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
    public final static String defaultMap = "data/map.osm";

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Load.fxml"));
        primaryStage.getIcons().add(new Image(View.class.getResourceAsStream("images/icon.png")));
        primaryStage.setScene(loader.load());
        primaryStage.setTitle("MapIT");
        primaryStage.centerOnScreen();
    }
}
