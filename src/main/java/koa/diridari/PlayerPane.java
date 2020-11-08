package koa.diridari;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.Pane; 
import javafx.scene.media.Media; 
import javafx.scene.media.MediaPlayer; 
import javafx.scene.media.MediaView; 

public class PlayerPane extends BorderPane 
{ 
    final Logger logger = LoggerFactory.getLogger(PlayerPane.class);
    
	Media media; 
	MediaPlayer mediaPlayer; 
	MediaView view; 
	Pane mediaPane; 
	MediaBar bar; 
	
//	public PlayerPane(String file) { 
//		media = new Media(file); 
//		mediaPlayer = new MediaPlayer(media); 
//		view = new MediaView(mediaPlayer); 
//		mediaPane = new Pane(); 
//		mediaPane.getChildren().add(view); // Calling the function getChildren 
//		
//		setCenter(mediaPane); 
//		bar = new MediaBar(mediaPlayer); // Passing the player to MediaBar 
//		setBottom(bar); // Setting the MediaBar at bottom 
//		setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar 
//		mediaPlayer.play(); // Making the video play 
//	}

    public PlayerPane() {
        
    }
    
    public void startVideo(URI videoFile) {
        logger.debug("startVideo: " + videoFile);
        media = new Media(videoFile.toString()); 
        mediaPlayer = new MediaPlayer(media); 
        view = new MediaView(mediaPlayer); 
        mediaPane = new Pane(); 
        mediaPane.getChildren().add(view); // Calling the function getChildren 
        
        setCenter(mediaPane); 
        bar = new MediaBar(mediaPlayer); // Passing the player to MediaBar 
        setBottom(bar); // Setting the MediaBar at bottom 
        setStyle("-fx-background-color:#bfc2c7"); // Adding color to the mediabar 
        mediaPlayer.play(); // Making the video play    
    }
} 
