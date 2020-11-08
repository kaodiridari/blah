package koa.diridari;

import java.nio.file.Path;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import koa.diridari.preview.PreviewContextMenueRequestedHandler;
import koa.diridari.preview.PreviewModell;
import koa.diridari.preview.PreviewMouseEventHandler;
import koa.diridari.preview.PreviewPane;

// launches the application 
public class Main extends Application {

    public final Logger logger = LoggerFactory.getLogger(Main.class);

    private FileChooser fileChooser;

    private FileOpenHandler fileOpenHandler;

    private PlayerPane playerPane;

    private PreviewModell previewModel;

    // Main function to launch the application
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // Adding functionality to switch to different videos
        // fileChooser = new FileChooser();

        // here you can choose any video
        // player = new Player("file:///C:/Users/user/Music/cleopatra/03Track03.mp3");
        // player = new Player(/*"file:///C:/Users/user/Videos/blacked/f_0000a9.mp4"*/);
        // Setting the menu at the top
        // player.setTop(menuBar);

        // Adding player to the Scene
        // Scene scene = new Scene(player, 720, 535, Color.BLACK);

        // height and width of the video player
        // background color set to Black
        // primaryStage.setScene(scene); // Setting the scene to stage
        
        previewModel = new PreviewModell();
        PreviewContextMenueRequestedHandler pcmrh = new PreviewContextMenueRequestedHandler(previewModel);
        PreviewMouseEventHandler pmeh = new PreviewMouseEventHandler(this);        
        PreviewPane previewPane = new PreviewPane(pcmrh, pmeh, previewModel);// new MainPane(primaryStage);
        previewModel.setView(previewPane);
        
        playerPane = new PlayerPane();

        Pane mainPane = new MainPane(primaryStage, previewPane, playerPane, new ToolBarHandler(previewModel));

        Scene scene = new Scene(mainPane, 720, 535, Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Cache Viewer");
        primaryStage.show(); // Showing the stage
    }

    /**
     * A preview with the given id has been chosen (left-click).<br>
     * We get the video-chunk and pass it to the player.
     * 
     * @param id
     */
    public void previewChosen(UUID id) {
        logger.debug("id: " + id);
        Path p = previewModel.getVideoWithId(id);
        logger.debug("path: " + p.toUri());
        playerPane.startVideo(p.toUri());
    }
}
