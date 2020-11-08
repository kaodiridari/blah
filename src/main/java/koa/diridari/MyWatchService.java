package koa.diridari;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyWatchService implements Runnable {
    
    final Logger logger = LoggerFactory.getLogger(MyWatchService.class);
    
    private NewFileHandler newFileHandler;
    
    private boolean stop;

    private Path watchMe;
    
    public MyWatchService(NewFileHandler nfh, Path watchMe) {
        newFileHandler = nfh;        
        this.watchMe = watchMe;
        logger.debug("watching: " + watchMe);
    }    
    
    @Override
    public void run() {
        logger.debug("MyWatchService running");
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            //Path path = Paths.get(/*Constants.getProperty("cache")*/ watchMe);
            watchMe.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey key;
            
            while (((key = watchService.take()) != null) & (!stop)) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    logger.debug("Event kind:" + event.kind() + ". File affected: " + event.context() + ".");
                    //TODO something like a configurable file-filter.
                    
                    if (event.context() != null) {
                        Path pa = ((Path)(event.context()));
                        String s = pa.toString();
                        if (s.startsWith("f_")) {
                            newFileHandler.handleNewFile(watchMe.resolve((Path)event.context()));
                        } else {
                            logger.debug("I don't like this. Wrong prefix");
                        }
                    } else {
                        logger.debug("I don't like this context == null");
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("ups", e);
        }
    }
    
    public void doStop() {
        logger.debug("stopping");
        this.stop = true;
        //newFileHandler.doStop();
    }
}
