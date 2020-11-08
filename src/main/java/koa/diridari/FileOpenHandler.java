package koa.diridari;

import java.io.File;
import java.net.MalformedURLException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Creates a new Panel for a single movie-part.
 * 
 * @author user
 *
 */
public class FileOpenHandler implements EventHandler<ActionEvent> {

    final BorderPane parentPane;
    final Stage primaryStage;
    // final Player player;

    public FileOpenHandler(BorderPane parentPane, Stage primaryStage) {
        this.parentPane = parentPane;
        this.primaryStage = primaryStage;
    }

    @Override
    public void handle(ActionEvent event) {
        // Pausing the video while switching
        // player.player.pause();
        File file = (new FileChooser()).showOpenDialog(primaryStage);

        // Choosing the file to play
        if (file != null) {
            PlayerPane player = new PlayerPane();
            player.startVideo(file.toURI());
            parentPane.setCenter(player);
        }
    }
}
