package koa.diridari;

import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainPane extends BorderPane {

    final Logger logger = LoggerFactory.getLogger(MainPane.class);
    
	private Button buttonStartMonitor;
	private Button buttonStopMonitor;
	private Button buttonLink;
	private Button buttonDisk;
	private ButtonBase buttonFilter;

	private Button buttonDiskOff;

    public MainPane(Stage stage, Pane preview, Pane player, ToolBarHandler tbh) throws Exception {
        logger.debug("Constructor");

//	    MenuItem openItem = new MenuItem("Open"); 
//	    Menu fileMenu = new Menu("File"); 
//	    MenuBar menuBar = new MenuBar(); 
//	     
//	    fileMenu.getItems().add(openItem); 
//	    menuBar.getMenus().add(fileMenu);
//	    
//	    openItem.setOnAction(new FileOpenHandler(this, stage));
//	    setTop(menuBar);
        ToolBar toolBar = new ToolBar();
        {
        	Image image;
        	try(InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/link.png")) {            
        		image = new Image(input);
        	}
            ImageView imageView = new ImageView(image);
            buttonLink = new Button("", imageView);
            toolBar.getItems().add(buttonLink);
            buttonLink.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        tbh.linkPressed();
                    } catch (Exception e) {
                        // TODO Exception handling
                        logger.error("ups", e);
                    }
                }
            });
        }
        {
        	Image image;
        	try(InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/eye.png")){            
        		image = new Image(input);
        	}
            ImageView imageView = new ImageView(image);
            buttonStartMonitor = new Button("", imageView);
            toolBar.getItems().add(buttonStartMonitor);
            buttonStartMonitor.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        tbh.monitorPressed();
                    } catch (Exception e) {
                        // TODO Exception handling
                        logger.error("ups", e);
                    }
                }
            });
        }
        {
        	Image image;
        	try(InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/eye-off.png")){
        		image = new Image(input);
        	}
            ImageView imageView = new ImageView(image);
            buttonStopMonitor = new Button("", imageView);
            toolBar.getItems().add(buttonStopMonitor);
            buttonStopMonitor.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        tbh.stopMonitorPressed();
                    } catch (Exception e) {
                        // TODO Exception handling
                        logger.error("ups", e);
                    }
                }
            });
        }
        {
        	Image image;
        	try(InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/disk.png")){
        		image = new Image(input);
        	}
            ImageView imageView = new ImageView(image);
            buttonDisk = new Button("", imageView);
            toolBar.getItems().add(buttonDisk);
            buttonDisk.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        tbh.diskPressed();
                    } catch (Exception e) {
                        // TODO Exception handling
                        logger.error("ups", e);
                    }
                }
            });
        }
        {
        	Image image;
        	try(InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/disk-off.png")){            
        		image = new Image(input);
        	}
            ImageView imageView = new ImageView(image);
            buttonDiskOff = new Button("", imageView);
            toolBar.getItems().add(buttonDiskOff);
            buttonDiskOff.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        tbh.diskOffPressed();
                    } catch (Exception e) {
                        // TODO Exception handling
                        logger.error("ups", e);
                    }
                }
            });
        }
        {
        	Image image;
        	try(InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/filter.png")){
        		image = new Image(input);
        	}
            ImageView imageView = new ImageView(image);
            buttonFilter = new Button("", imageView);
            toolBar.getItems().add(buttonFilter);
            buttonFilter.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        tbh.filterPressed();
                    } catch (Exception e) {
                        // TODO Exception handling
                        logger.error("ups", e);
                    }
                }
            });
        }

        VBox vBox = new VBox(toolBar);
        setTop(vBox);

        SplitPane previewAndPlayerPane = new SplitPane(preview, player);
        previewAndPlayerPane.setOrientation(Orientation.VERTICAL);
        previewAndPlayerPane.setDividerPositions(0.3f);
        setCenter(previewAndPlayerPane);
    }
}
