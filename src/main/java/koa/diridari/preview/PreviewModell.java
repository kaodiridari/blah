package koa.diridari.preview;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import koa.diridari.AllTheVideos;
import koa.diridari.MyImage;
import koa.diridari.MyImage.MOOD;
import koa.diridari.NewFileConsumer;
import koa.diridari.ToolBarHandler;

/**
 * The model for PreviewPane.<br>
 * Holds first, last, removed (video-chunks).
 * @author user
 *
 */
public class PreviewModell implements NewFileConsumer {

    final Logger logger = LoggerFactory.getLogger(PreviewModell.class);
    
    private UUID first;
    
    private UUID last;
        
    private Set<UUID> liRemoved = new HashSet<>();
    
    private AllTheVideos allTheVideos;

    private PreviewPane theView;   
    
    public PreviewModell() throws Exception {
    	allTheVideos = new AllTheVideos();
	}
    
//    public PreviewModell(AllTheVideos all) {
//        this.allTheVideos = all;        
//    }

    public UUID getFirst() {
        return first;
    }

    public void setFirst(UUID first) throws Exception {
        this.first = first;
        allTheVideos.getImageWithId(first).setMood(MOOD.STARTEND);
        theView.update();
    }

    public UUID getLast() {
        return last;
    }
    
    public Set<UUID> getRemoved() {
        return liRemoved;
    }
    
    public void setLast(UUID last) throws Exception {
        this.last = last;
        allTheVideos.getImageWithId(last).setMood(MOOD.STARTEND);
        theView.update();
    }
    
    public void setRemoved(UUID removed) throws Exception {    
        if ((first != null && first.equals(removed)) ||
             (last != null && last.equals(removed))) {
            throw new PreviewException();
        }
        liRemoved.add(removed);
        allTheVideos.getImageWithId(removed).setMood(MOOD.DELETED);
        theView.update();
    }
    
    public List<MyImage> getPreviews() {
        return allTheVideos.getPreviews();
    }
    
    public Path getVideoWithId(UUID id) {
        return allTheVideos.getVideoWithId(id);
    }

    /**
     * For setting the panel, which should show this model.
     * 
     * @param previewPane
     */
    public void setView(PreviewPane previewPane) {
        this.theView = previewPane;        
    }

    @Override
    public void onNewFile(MyImage myImage) throws Exception {
        logger.debug("New video-chunck: " + myImage.getOutputPathVideoChunck());
        allTheVideos.add(myImage);
        
        //TODO do something with the new chunck.
        theView.update();        
    }
}
