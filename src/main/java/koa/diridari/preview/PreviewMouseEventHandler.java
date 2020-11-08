package koa.diridari.preview;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import koa.diridari.Main;
import koa.diridari.MyImageView;

public class PreviewMouseEventHandler implements EventHandler<MouseEvent> {

    final Logger logger = LoggerFactory.getLogger(PreviewMouseEventHandler.class);
    
    final private Main application;
    
    public PreviewMouseEventHandler(Main app) {
        application = app;
    }
    
    @Override
    public void handle(MouseEvent event) {
        logger.debug("Mouse on Picture!");
        Object o = event.getSource();
        MyImageView miv = (MyImageView)o;
        UUID id = miv.getMyImage().getId();
        logger.debug("event from: " + id);
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            logger.debug("Left on Picture! " + id);
            application.previewChosen(id);
            event.consume();       
        } else {
            logger.debug("Not interested.");
        }        
    }
}
