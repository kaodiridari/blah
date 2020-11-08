package koa.diridari;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import koa.diridari.ffmpeg.FfmpegFailedException;
import koa.diridari.ffmpeg.FfmpegHelper;
import koa.diridari.preview.PreviewModell;

public class ToolBarHandler {

    final Logger logger = LoggerFactory.getLogger(ToolBarHandler.class);

    private PreviewModell previewModel;

    private NewFileHandler nfh;

    private MyWatchService ws;
    
    private MyScanService ss;

    public ToolBarHandler(PreviewModell previewModel) {
        this.previewModel = previewModel;
        nfh = NewFileHandler.getInstance();
        nfh.addConsumer(previewModel);
    }

    /**
     * Concatenates movie chunks.
     * @throws BreakingBadException 
     * 
     * @throws Exception
     */
    // TODO ineffective, but does it matter?
    public Path linkPressed() throws BreakingBadException, Exception {
        logger.debug("linkPressed()");

        UUID firstMovieChunkId = previewModel.getFirst();
        UUID lastMovieChunkId = previewModel.getLast();
        Set<UUID> removedMovieChunks = previewModel.getRemoved();

        List<MyImage> chunks = previewModel.getPreviews();

        int firstindex = 0;
        int lastindex = 0;

        // run to first
        if (firstMovieChunkId != null) {
            int i = 0;
            for (; i < chunks.size(); i++) {
                UUID uuid = chunks.get(i).getId();  
                if (uuid.equals(firstMovieChunkId)) {
                    break;
                }
            }
            firstindex = i;
        }
        logger.debug("indexOfFirst: " + firstindex);

        // run to last
        if (lastMovieChunkId != null) {
            int i = 0;
            for (; i < chunks.size(); i++) {
                if (chunks.get(i).getId().equals(lastMovieChunkId)) {
                    break;
                }
            }
            lastindex = i;
        }
        logger.debug("indexOfLast: " + lastindex);
        
        if (lastindex < firstindex) {
            throw new BreakingBadException("lastindex: " + lastindex + " firstindex: " + firstindex + " this is bad.");
        }
        logger.debug("lastindex: " + lastindex + " firstindex: " + firstindex);

        // remove the deleted chunks
        // ...and finally call concat. First prepare the list of paths.
        List<Path> paths = new ArrayList<>();
        for (int i = firstindex; i <= lastindex; i++) {
            MyImage aVideoChunk = chunks.get(i);
            if (!removedMovieChunks.contains(aVideoChunk.getId())) {
                paths.add(aVideoChunk.getOutputPathVideoChunck());
            }
        }
        
        logger.debug("calling with: " + paths.size() + " paths.");
        List<String> outputTxt = new ArrayList<>();
        Path result;
        try {
            result = FfmpegHelper.concat(paths, "concat" + paths.size() + "_" + (new Date()).getTime() + ".mp4", outputTxt);
        } catch (Exception e) {
            logger.error("Concat failed. Famous last words:\n " + String.join("\n", outputTxt), e);
            throw e;
        }
        logger.debug("result: " + result);
        return result;
    }

    /**
     * Triggers cache-folder monitoring.
     * 
     * @throws Exception
     */
    public void monitorPressed() throws Exception {
        logger.debug("monitorPressed");        
        
        ws = new MyWatchService(nfh, Paths.get(Constants.getProperty("cache")));        
        Thread thread = new Thread(ws);
        thread.start();
        
        //Watch service needs time to start. Maybe we need to wait here
        
    }
    
    /**
     * Stops cache-folder monitoring.
     * 
     * @throws Exception
     */
    public void stopMonitorPressed() throws Exception {
        logger.debug("stopMonitorPressed");
        
        ws.doStop();
    }
	
	/**
     * Starts processing of a whole folder.<br>
     * The folder is configured in the property cache.  
     * @throws Exception 
     */
//	public void diskPressed2() throws Exception {		
//		logger.debug("diskPressed2()");
//		Runnable runMe = new Runnable() {
//			
//			@Override
//			public void run() {
//				try {
//					VideoStruct referenceVideoStruct = new VideoStruct(1280, 720, "h264");
//					Iterator<Path> it = Files.list(Paths.get(Constants.getProperty("cache"))).iterator();		
//					while (it.hasNext()) {
//						Path path = it.next();
//						Path quasiConcat = FfmpegHelper.checkSingle(path, referenceVideoStruct);
//						if (quasiConcat != null) {
//							MyImage myImage = 
//									FfmpegHelper.getPreviewImageList(
//											new Date(0), new Date(), referenceVideoStruct,
//											quasiConcat).get(0);
//							previewModel.onNewFile(myImage);
//						}
//					}
//					logger.debug("All files in cache are well done.");
//				} catch (Exception e) {
//					logger.error("ups", e);
//				}
//			}
//		};
//		
//		Thread t = new Thread(runMe);
//		t.start();
//	}

	public void diskPressed() throws Exception {
		logger.debug("diskPressed()");
		
		ss = new MyScanService(nfh, Paths.get(Constants.getProperty("cache")));		
		Thread thread = new Thread(ss);
		thread.start();
	}
	
	public void diskOffPressed() throws Exception {
		logger.debug("diskOffPressed()");
		
		ss.doStop();
	}
	
	
	/**
	 * Choose a video-file in the cache for getting values for resolution, video-codec ...
	 */
	public void filterPressed() {
		logger.debug("filterPressed()");
	}    
}
