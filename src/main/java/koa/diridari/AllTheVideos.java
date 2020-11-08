package koa.diridari;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import koa.diridari.ffmpeg.FfmpegHelper;

/**
 * Holds a list of the video-chunks and the previews.<br>
 * Maps from the preview ids to the video-files.<br>
 * Where are the videos?<br>
 * In {@link Constants#chunksDir} they are.
 * 
 * @author user
 *
 */
public class AllTheVideos {

    final Logger logger = LoggerFactory.getLogger(AllTheVideos.class);
    
    private List<MyImage> previews;
    
    private Map<UUID, MyImage> mapIdImage = new HashMap<UUID, MyImage>();
    
    /**
     * Constructor<br>
     * Sets the preview image list {@link FfmpegHelper#getPreviewImageList()}.
     * @throws Exception
     */
    public AllTheVideos() throws Exception {
        previews = new ArrayList<MyImage>();
//        previews = FfmpegHelper.getPreviewImageList();
//        logger.debug("loaded previews: " +previews.size());
//        for (MyImage myImage : previews) {
//            mapIdImage.put(myImage.getId(), myImage);
//        }
    }
    
//    public AllTheVideos(List<MyImage> previews) throws Exception {
//        this.previews = previews;
//        for (MyImage myImage : previews) {
//            mapIdImage.put(myImage.getId(), myImage);
//        }
//    }
    
    public MyImage getImageWithId(UUID id) {
        return mapIdImage.get(id);
    }

    public List<MyImage> getPreviews() {
       return previews;        
    }

    public Path getVideoWithId(UUID id) {
        MyImage myi = mapIdImage.get(id);
        if (myi == null) {
            return null;
        } else {
            return myi.getOutputPathVideoChunck();
        }
    }
    
    /**
     * Adds at the end of the internal list.
     * 
     * @param newMyImage
     */
    public void add(MyImage newMyImage) {
        logger.debug("adding new image: " + newMyImage.getId() + " " + newMyImage.getMood());
        previews.add(newMyImage);
        mapIdImage.put(newMyImage.getId(), newMyImage);
    }
    
    public void addAll(List<MyImage> newMyImages) {
        logger.debug("adding new images: " + newMyImages.size());
        previews.addAll(newMyImages);
        for (MyImage myImage : newMyImages) {
        	mapIdImage.put(myImage.getId(), myImage);
		}        
    }
}
