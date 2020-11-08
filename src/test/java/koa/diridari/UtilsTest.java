package koa.diridari;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import koa.diridari.Utils;

public class UtilsTest {
    
    final Logger logger = LoggerFactory.getLogger(UtilsTest.class);
    
    @Test
    public void testSortCacheFiles() {
        
        Path path = Paths.get("C:\\Users\\user\\Documents\\java\\swing\\blah\\src\\test\\resources\\previews");
        String[] pictures = path.toFile().list();
        List<Path> cacheFiles = new ArrayList<Path>();
        for (String string : pictures) {
            logger.debug(string);
            cacheFiles.add(path.resolve(string));
        }
        
        List<Path> cf = Utils.sortCacheFiles(cacheFiles);
        logger.debug("sorted:");
        for (Path p : cf) {
            logger.debug(p.toString());
        }
        
        assertEquals(pictures.length, cf.size());
    }
}
