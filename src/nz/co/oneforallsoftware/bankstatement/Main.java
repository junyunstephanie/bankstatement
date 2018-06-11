package nz.co.oneforallsoftware.bankstatement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Locale;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            Locale.setDefault(new Locale("en", "GB"));
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("MainPane.fxml"));
            BorderPane mainPane = (BorderPane) loader.load();

            Scene scene = new Scene(mainPane);
            scene.getStylesheets().add(Main.class.getResource("style.css").toExternalForm());
            primaryStage.setScene(scene);

            MainPane pane = (MainPane) loader.getController();
            if( pane != null ){
                pane.setStage(primaryStage);
            }
            primaryStage.setMaximized(true);
            primaryStage.show();
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }
}
