package koa.diridari;

import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import koa.diridari.ffmpeg.FfmpegHelper;

public class NewFileHandler implements Runnable {

    final Logger logger = LoggerFactory.getLogger(NewFileHandler.class);

    private VideoStruct referenceVideoStruct = new VideoStruct(1280, 720, "h264");
    
    private BlockingQueue<Path> queue = new LinkedBlockingQueue<>();
    //private LinkedList<Path> queue = new LinkedList<Path>();

    private boolean stop;

    private NewFileConsumer newFileConsumer;
    
    public NewFileHandler(NewFileConsumer nfc) {
        newFileConsumer = nfc; 
    }
    
    public void handleNewFile(Path p) {
        logger.debug("offer: " + p);
        queue.offer(p);
    }

    public void doStop() {
        logger.debug("stopping");
        this.stop = true;
    }

    /**
     * Never processes the last inserted file (which may not be finished yet) until <b>this<b> received a stop request.
     */
    @Override
    public void run() {
        logger.debug("NewFileHandler running");
        try {
            while (!stop) {
                if (queue.size() >= 2) {
                	peekNPoll();
                }
                Thread.sleep(1000);
            }            
            logger.debug("stopping, queue size: " + queue.size());
            while (queue.size() > 0) {
            	peekNPoll();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("ups", e);
        }
    }
    
	private void peekNPoll() throws Exception {
		Path pathPeeked = queue.peek();
		Path quasiConcat;
		logger.debug("pathPeeked: " + pathPeeked);
		try {
			quasiConcat = FfmpegHelper.checkSingle(pathPeeked, referenceVideoStruct);
		} catch (Exception e1) {
			logger.error("Can't check this file, this is most peculiar. I throw it away: " + pathPeeked, e1);
			queue.poll(); // throw it away
			return;
			// continue;
		}
		if (quasiConcat != null) {
			logger.debug("quasiConcat: " + quasiConcat);//
			Date start = new Date(0);
			Date end = new Date();

			List<MyImage> pil = null;
			try {
				pil = FfmpegHelper.getPreviewImageList(start, end, referenceVideoStruct, quasiConcat);
			} catch (Exception e) {
				logger.error("Can't get preview. Maybe file is looked? Soon we try again ...", e);
				return;
				// continue;
			}
			if (pil != null && pil.size() == 1) {
				MyImage myImage = pil.get(0);
				newFileConsumer.onNewFile(myImage);
				logger.debug("I did it, I did it. " + myImage.getOutputPathVideoChunck());
				queue.poll();
			} else {
				logger.debug("Returned not exactly list with size == 1");
			}
		} else {
			logger.debug("I throw this away.");
			queue.poll(); // throw it away
		}
	}
}
