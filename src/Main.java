import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


/**
 * Created by Marcus on 2016-08-16.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Parent root = null;
        try{
            root = FXMLLoader.load(getClass().getResource("GuideView.fxml"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Scene scene = new Scene(root, 300, 275);

        stage.setTitle("FXML Welcome");
        stage.setScene(scene);
        stage.show();
    }
}

