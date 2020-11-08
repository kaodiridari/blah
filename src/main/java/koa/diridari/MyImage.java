package koa.diridari;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import koa.diridari.MyImage.MOOD;

/**
 * Struct for an preview-image and the path to the film it came from.
 * 
 * @author user
 *
 */
public class MyImage {

    final Logger logger = LoggerFactory.getLogger(MyImage.class);

    private final BufferedImage preview;
    private final Path pathPreview;
    private final Path outputPathVideoChunck;
    
    public enum MOOD {DELETED, MARKED, STARTEND, INDIFFERENT/*, NEW*/};
    
    private MOOD myMoodIs = MOOD.INDIFFERENT;
    
    /**
     * 
     * @return The path of the mp4 - video.
     */
    public Path getOutputPathVideoChunck() {
        return outputPathVideoChunck;
    }

    private final UUID id;
    /**
     * original
     */
    private final Image fxImage;
    /**
     * highlighted
     */
    private final WritableImage markedFxImage;
    /**
     * red cross
     */
    private final Image markedFxImageDeleted;
    /**
     * green circle
     */
    private final Image markedFxImageStartEnd;
    
    private boolean dirty = true;
    
    public MyImage(BufferedImage preview, Path pathPreview, Path outputPathVideoChunck) throws Exception {    
        long start = System.currentTimeMillis();
        this.preview = preview;
        this.pathPreview = pathPreview;
        this.outputPathVideoChunck = outputPathVideoChunck;
        id = UUID.fromString(StringUtils.substringBeforeLast(outputPathVideoChunck.getFileName().toString(), "."));   
        BufferedImage previewCross = Utils.clone(preview);
        Utils.drawCrossInImage(previewCross);
        BufferedImage previewRing = Utils.clone(preview);
        Utils.drawRingInImage(previewRing);
        
        //original and highlighted
        try (InputStream is = bufferedImageToInputStream(preview)) {
            fxImage = new Image(is);
            markedFxImage = new WritableImage((int)fxImage.getWidth(), (int)fxImage.getHeight());
            createMarkedFxImage();
        }
        try (InputStream is = bufferedImageToInputStream(previewCross)) {
            markedFxImageDeleted = new Image(is);
        }
        try (InputStream is = bufferedImageToInputStream(previewRing)) {
            markedFxImageStartEnd = new Image(is);
        }
        
        logger.debug(pathPreview + " done in (ms:) " + (System.currentTimeMillis() - start));
    }
    
    public BufferedImage getPreview() {
        return preview;
    }

    public Path getPathPreview() {
        return pathPreview;
    }

    /**
     * This is the uuid of the video from which this preview picture was generated.<br>
     * The video is in {@link Constants#chunksDir}. The filename is <uuid>.mp4
     * @return
     */
    public UUID getId() {
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MyImage other = (MyImage) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public Image getFxImage() {       
        return fxImage;
    }

    public WritableImage getMarkedFxImage() {
        return markedFxImage;
    }

    public Image getMarkedFxImageDeleted() {
        return markedFxImageDeleted;
    }

    public Image getMarkedFxImageStartEnd() {
        return markedFxImageStartEnd;
    }
    
    public Image getByMood() throws Exception {
        logger.debug("I'm in the mood: " + myMoodIs);
        
        switch (myMoodIs) {
        case DELETED:
            return getMarkedFxImageDeleted();           
        case MARKED:
            return getMarkedFxImage();            
        case STARTEND:
            return getMarkedFxImageStartEnd();            
        case INDIFFERENT:
            return getFxImage();            
        default:
            throw new Exception("unhandled: " + myMoodIs);            
        }
    }

    /**
     * 
     * @param buffImage
     * @return
     * @throws Exception
     */
    private InputStream bufferedImageToInputStream(BufferedImage buffImage) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ImageIO.write(buffImage, "jpeg", outStream);
        InputStream is = new ByteArrayInputStream(outStream.toByteArray());
        return is;
    }
    
    private void createMarkedFxImage() {
        // Obtain the pixel reader from the image
        PixelReader pixelReader = fxImage.getPixelReader();       
        PixelWriter brighterWriter = markedFxImage.getPixelWriter();
        //PixelWriter semiTransparentWriter = semiTransparentFxImage.getPixelWriter();
 
        // Read one pixel at a time from the source and
        // write it to the destinations
        for(int y = 0; y < fxImage.getHeight(); y++) {
            for(int x = 0; x < fxImage.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                brighterWriter.setColor(x, y, color.brighter());
                //wsemiTransparentWriter.setColor(x, y, Color.color(color.getRed(), color.getGreen(),color.getBlue(), 0.50));
            }
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public MOOD getMood() {
       return myMoodIs;        
    }

    public void setMood(MOOD mood) {
        myMoodIs = mood;
    }   
}
