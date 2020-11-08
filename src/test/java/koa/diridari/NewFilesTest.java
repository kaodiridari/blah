package koa.diridari;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In a certain folder new files do appear. A NewFileConsumer has to be notified for each one.
 * 
 * @author user
 *
 */
public class NewFilesTest {

    final Logger logger = LoggerFactory.getLogger(NewFilesTest.class);
    
    /**
     * Where we find the movie-parts. 
     * The google cache may be at:
     * C:/Users/user/AppData/Local/Google/Chrome/User Data/Default/Cache
     */
    final public Path cacheDir = Paths.get("C:\\Users\\user\\Videos\\temp");
    
    
//    @Test
//    public void test(@TempDir Path tempDir) throws Exception {
//        
//        NewFileConsumer tnfc = new TestNewFileConsumer();
//        NewFileHandler nfh = new NewFileHandler(tnfc);
//        MyWatchService ws = new MyWatchService(nfh, tempDir);
//        Thread t_nfh = new Thread(nfh);
//        t_nfh.start();
//        Thread t_ws = new Thread(ws);
//        t_ws.start();
//        
//        Thread.sleep(1000); //Watch service needs time to start.
//        
//        //we copy Files        
//        File cacheFolder = cacheDir.toFile();
//        int n = 0;
//        for (File f : cacheFolder.listFiles()){
//            Path source = cacheFolder.toPath().resolve(f.toPath());
//            Path target = tempDir.resolve(f.getName());
//            logger.debug("source: " + source + " target: " + target);
//            Files.copy(source, target);
//            n++;
//            Thread.sleep(5000);
//        } 
//        logger.debug("n: " + n);
//        assertEquals(n-1, ((TestNewFileConsumer)tnfc).received);
//    }
}

class TestNewFileConsumer implements NewFileConsumer {
    
    final Logger logger = LoggerFactory.getLogger(TestNewFileConsumer.class);
    
    int received = 0;
    
    @Override
    public void onNewFile(MyImage myImage) throws Exception {
        logger.debug("received: " + myImage.getOutputPathVideoChunck());
        received++;
    }    
}
