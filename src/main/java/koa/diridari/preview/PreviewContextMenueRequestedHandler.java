package koa.diridari.preview;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
import koa.diridari.MyImageView;

public class PreviewContextMenueRequestedHandler implements EventHandler<ContextMenuEvent> {

    final Logger logger = LoggerFactory.getLogger(PreviewContextMenueRequestedHandler.class);
    
    private ContextMenu contextMenu;

    private UUID lastContextMenuEventFrom;

    private PreviewModell previewModel;
    
    public PreviewContextMenueRequestedHandler(PreviewModell p) {
        this.previewModel = p;
        contextMenu = new ContextMenu();        
        MenuItem item1 = new MenuItem("First");
        item1.setOnAction((ActionEvent event) -> {
            logger.debug("First chosen " + lastContextMenuEventFrom);
            try {
                previewModel.setFirst(lastContextMenuEventFrom);
            } catch (Exception e) {
                // TODO Auto-generated catch block
               logger.debug("ups.", e);
            }
            event.consume();
         });
        MenuItem item2 = new MenuItem("Last");
        item2.setOnAction((ActionEvent event) -> {
            logger.debug("Last chosen "  + lastContextMenuEventFrom);
            try {
                previewModel.setLast(lastContextMenuEventFrom);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                logger.debug("ups.", e);
            }
            event.consume();
        });
        MenuItem item3 = new MenuItem("Remove");
        item3.setOnAction((ActionEvent event) -> {
            logger.debug("Remove chosen " + lastContextMenuEventFrom);
            try {
                previewModel.setRemoved(lastContextMenuEventFrom);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                logger.debug("Can't remove.");
            }
            event.consume();
        });
        contextMenu.getItems().addAll(item1, item2, item3);
    }
    
    @Override
    public void handle(ContextMenuEvent event) {     
        logger.debug("ContextMenuEvent occured.");                
        contextMenu.show((MyImageView)event.getSource(), event.getScreenX(), event.getScreenY());
        getIdOfEventSource(event);
        event.consume();   
    }
    
    private void getIdOfEventSource(ContextMenuEvent event) {        
        MyImageView miv = (MyImageView)event.getSource();
        lastContextMenuEventFrom = miv.getMyImage().getId();
        logger.debug("event from: " + lastContextMenuEventFrom);        
    }
}
