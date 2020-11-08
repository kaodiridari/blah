package koa.diridari;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import koa.diridari.ffmpeg.FfmpegHelper;

public class FfmpegOutputTest {

    //TODO Test for output. ffmpeg may change.
//    @Test
//    public void testParseOutputOfConcatSingle() throws Exception {
//        FfmpegOutput ffmpegOutput = new FfmpegOutput(); 
//        Path tmpPathOutput = Paths.get("C:\\Users\\user\\Documents\\java\\swing\\blah\\src\\test\\resources\\output.txt");
//        ffmpegOutput.parseOutputOfConcatSingle(tmpPathOutput);
//        
//        List<String> theLinesBetween = ffmpegOutput.getTheLinesBetween();
//        String theOutputLine = ffmpegOutput.getTheOutputLine();       
//        assertEquals(theLinesBetween.size(), 4);
//        assertEquals(theOutputLine, "Output #0, mp4, to 'C:\\Users\\user\\Documents\\java\\swing\\blah\\temp\\output2.mp4':");
//        
//        VideoStruct vs = ffmpegOutput.getVideoStruct();
//        VideoStruct referenceVideoStruct = new VideoStruct(1280, 720, "h264");
//        assertTrue(vs.equals(referenceVideoStruct));        
//    }
    
    @Test
    public void testCheckSingle() throws Exception {
        Path chromeCacheFolder = Paths.get((String) Constants.getProperties().get("cache"));
        VideoStruct referenceVideoStruct = new VideoStruct(1280, 720, "h264");
        {
            Path testMe = chromeCacheFolder.resolve("f_00007b");
            boolean isVideo = FfmpegHelper.checkSingle(testMe, referenceVideoStruct) != null;
            assertTrue(isVideo);
        }
        {
            Path testMe = chromeCacheFolder.resolve("f_00007c");
            boolean isVideo = FfmpegHelper.checkSingle(testMe, referenceVideoStruct) != null;
            assertFalse(isVideo);
        }
        {
            Path testMe = chromeCacheFolder.resolve("f_00007d");
            boolean isVideo = FfmpegHelper.checkSingle(testMe, referenceVideoStruct)  != null;
            assertTrue(isVideo);
        }
    }    
   
    private BufferedImage testExtractFirstImage(@TempDir Path tempDir) throws Exception{
        System.out.println("tempDir: " + tempDir);
        Path chromeCacheFolder = Paths.get((String) Constants.getProperties().get("cache"));
        Path source = chromeCacheFolder.resolve("f_00007a");
        Path target = tempDir.resolve("f_00007a.jpg");
        FfmpegHelper.extractFirstImage(source, target);
        assertTrue(Files.exists(target));
        
        //read it and return it
        BufferedImage image = ImageIO.read(target.toFile());
        return image;
    }
    
    @Test
    public void testResizeImage(@TempDir Path tempDir) throws Exception{
        BufferedImage image = testExtractFirstImage(tempDir);
        System.out.println("width x height: " + image.getWidth() + " x " + image.getHeight());
        BufferedImage resizedImage = FfmpegHelper.resizeImage(image, 200);
        File scaledJpg = new File(tempDir.toFile(), "scaled.jpg");
        ImageIO.write(resizedImage, "jpg", new File(tempDir.toFile(), "scaled.jpg"));
        assertTrue(Files.exists(scaledJpg.toPath()));
    }
    
    @Test
    public void testGetPreviewImageList() throws Exception {
        List<MyImage> list = FfmpegHelper.getPreviewImageList();
        System.out.println("We have " + list.size() + " files.");
        for (MyImage myImage : list) {
            System.out.println(myImage.getPathPreview());
        }
        System.out.println("done now saving.");
        String tempdirPrefix = (String)Constants.getProperty("tempdir_prefix").trim();         
        Path tempDirWithPrefix = Files.createTempDirectory(tempdirPrefix); 
        System.out.println("previews in:" + tempDirWithPrefix);
        for (MyImage myImage : list) {
            Path fileName = myImage.getPathPreview().getFileName();
            ImageIO.write(myImage.getPreview(), "jpg", new File (tempDirWithPrefix.toFile(), fileName.toString() + ".jpg"));            
        }
    }
}
