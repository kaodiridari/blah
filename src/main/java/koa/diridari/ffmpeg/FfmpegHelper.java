package koa.diridari.ffmpeg;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import koa.diridari.Constants;
import koa.diridari.MyImage;
import koa.diridari.Utils;
import koa.diridari.VideoStruct;

/**
 * ffmpeg -f concat -safe 0 -i files.txt -c copy output2.mp4
 *
 * Example for files.txt 
 * file 'C:/Users/user/AppData/Local/Google/Chrome/User Data/Default/Cache/f_0000a0'
 * file 'C:/Users/user/AppData/Local/Google/Chrome/User Data/Default/Cache/f_0000a1'
 * file 'C:/Users/user/AppData/Local/Google/Chrome/User Data/Default/Cache/f_0000a2'
 * 
 * @author user
 *
 */
public class FfmpegHelper {	
	
    final static Logger logger = LoggerFactory.getLogger(FfmpegHelper.class);
    
    /**
     * Concatenates cute little video chunks.
     * 
     * @param theJunks
     * @param outputFileName The movie's name. The whole path is then {@link Constants#chunksDir} / outputFileName
     * @param redirectedOutputLines 
     * @return  {@link Constants#chunksDir} / outputFileName
     * @throws Exception
     */
	public static Path concat(List<Path> theJunks, String outputFileName, List<String> redirectedOutputLines) throws Exception {
		
	    Path tempDir = Files.createTempDirectory("concat");
		Path tmpPathFilesTxt = tempDir.resolve("files.txt");		
		Path chunksDirectory = Paths.get(Constants.getProperty("chunksdir"));
		if (!Files.exists(chunksDirectory)) {
		    Files.createDirectory(chunksDirectory);
		}
		Path chunksDirectoryOutputFile = chunksDirectory.resolve(outputFileName);
		
		//write the files-file
		for (Path path : theJunks) {
			Files.writeString(tmpPathFilesTxt, "file \'" + path + "\'\n" ,StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
		
		//start ffmpeg
		ProcessBuilder processBuilder = new ProcessBuilder();

        // Run this on Windows, cmd, /c = terminate after this run, output is in StdErr
//        processBuilder.command("cmd.exe", "/c",
//        		"ffmpeg -f concat -safe 0 -i C:\\Users\\user\\Documents\\java\\swing\\blah\\temp\\files.txt -c copy C:\\Users\\user\\Documents\\java\\swing\\blah\\temp\\output2.mp4");
		processBuilder.command(
		        "cmd.exe", "/c",
                "ffmpeg -f concat -safe 0 -i " + tmpPathFilesTxt + " -c copy " + chunksDirectoryOutputFile
                );		
         
		Path tempOutput = tempDir.resolve("output.txt");
        Process process = processBuilder.redirectError(tempOutput.toFile()).start();
        process.waitFor();            
                
        //Read the output.
        List<String> outputTxt = Files.readAllLines(tempOutput);
        redirectedOutputLines.addAll(outputTxt);
        
        //delete the output and files.txt
        try {
            Utils.forceDeleteOnlyInTemp(tempDir, false);
        } catch (Exception e) {
            logger.warn("Can't delete temp directory.", e);
        }
        
        return chunksDirectoryOutputFile;
	}
	
	/**
	 * Checks if the given file is a Video.<br>
	 * This is done with ffmpeg concat.<br>
	 * <br>
	 * If it is not a video, the output shows something like:<br>
	 * Stream #0:0: Video: mjpeg (Baseline), yuvj444p(pc, bt470bg/unknown/unknown), 300x250, 25 tbr, 25 tbn, 25 tbc<br>
	 * <br>
	 * If it is a video, the output shows something like:<br>
	 * Stream #0:0: Video: h264 (High) ([27][0][0][0] / 0x001B), yuv420p(progressive), 1280x720 [SAR 1:1 DAR 16:9], 30 fps, 30 tbr, 90k tbn, 60 tbc<br>
	 * <br>
	 * If the resulting file is good (= a video) it is in {@link Constants#chunksDir}<br>
	 * 
	 * @param videosPath
	 * @param referenceVideoStruct For high-quality video files this may be a good choice:<br> referenceVideoStruct = new VideoStruct(1280, 720, "h264");
	 * @return The path of the resulting file or <b>null</b> for a no good file.
	 * @throws IOException  
	 * @throws FfmpegFailedException 
	 */
	public static Path checkSingle(Path videosPath, VideoStruct referenceVideoStruct) throws Exception, FfmpegFailedException {
		logger.debug("videosPath: " + videosPath);
	    List<Path> liPa = new ArrayList<Path>();
	    liPa.add(videosPath);
	    UUID id = UUID.randomUUID();
	    List<String> redirectedOutputLines = new ArrayList<String>(); 
	    Path resultIsIn = concat(liPa, id.toString() + ".mp4", redirectedOutputLines);
	    logger.debug("resultIsIn: " + resultIsIn);
	    logger.debug("Output has " + redirectedOutputLines.size() + " lines.");
	    
		//read output and search video-properties
		//We need lines beetween
		//Input #0
		//and
		//Output #0
	    FfmpegOutput ffmpegOutput = new FfmpegOutput();
	    try {
            ffmpegOutput.parseOutputOfConcatSingle(/*Constants.getTmpPathRedirectedOutput()*/ redirectedOutputLines);
        } catch (Exception e) {
            logger.debug("ups", e);
            //logger.debug(Files.readString(Constants.tmpPathRedirectedOutput, StandardCharsets.UTF_8));
            logger.debug("returning null");
            return null;
        }
	    VideoStruct vs = ffmpegOutput.getVideoStruct();
	    boolean isGood = vs.equals(referenceVideoStruct);
	    logger.debug("isGood: " + isGood);
	    if (!isGood) {	       
	        Files.deleteIfExists(resultIsIn);
	        return null;
	    } else {
	        return resultIsIn;
	    }
	}
	
	/**
	 * Example:
	 * ffmpeg -i [Path to source] -vframes 1 -f image2 [Path to target]
	 * @param source
	 * @param target
	 * @throws Exception
	 * @throws FfmpegFailedException
	 */
    public static void extractFirstImage(Path source, Path target) throws Exception, FfmpegFailedException {
        logger.debug("extractFirstImage source: " + source + ", target: " + target);
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("cmd.exe", "/c", "ffmpeg -i " + source.toString() + " -vframes 1 -f image2 " + target.toString());

        // Process process =
        File tmp = Files.createTempDirectory("extractFirstImage").resolve("blah.txt").toFile();
        processBuilder.redirectError(tmp);
        Process process = processBuilder.start();
        process.waitFor();
        List<String> outputTxt = Files.readAllLines(tmp.toPath());
        String outTxt = String.join("\n", outputTxt);
        logger.debug(outTxt);
    }
	
	public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth) throws Exception {
	    int originalWidth = originalImage.getWidth();
	    float scaleFaktor = (float)targetWidth / (float)originalWidth;
	    int targetHeight = Math.round(originalImage.getHeight() * scaleFaktor);
	    Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

	/**
	 * Resolution = 1280x720<br>
	 * Video-codec: h264 
	 * start: 1970<br>
	 * end: now<br>
	 * @return
	 * @throws Exception
	 */
	public static List<MyImage> getPreviewImageList() throws Exception {
	    Date start, end;
	    start = new Date(0);
	    end = new Date();
	    VideoStruct referenceVideoStruct = new VideoStruct(1280, 720, "h264");
        return  getPreviewImageList(start, end, referenceVideoStruct, null);
    }	
	
	/**
	 * Iterates over the chrome-cache and delivers a list of cute little preview images. The width of the previews is the configuration property "preview_width".
	 * 
	 * @param start not used yet
	 * @param end not used yet
	 * @param doJustSingle only this file is processed. This has to be already checked and the path points to a video which has been processed by concat.
	 * @return
	 */
	//TODO quite a mess ...
    public static List<MyImage> getPreviewImageList(Date start, Date end, VideoStruct referenceVideoStruct, Path doJustSingle) throws Exception {
        logger.debug("doJustSingle: " + doJustSingle);
        if (start == null || end == null) {
            throw new IllegalArgumentException("You must provide start and end date.");
        }
        
        File dir = new File(Constants.getProperty("cache"));          
        if (!Files.exists(dir.toPath())) {
            throw new Exception("In the configuration the cache is set to: " + dir + " which does not exist.");
        }
        String tempdirPrefix = (String)Constants.getProperty("tempdir_prefix").trim();         
        Path tempDirWithPrefix = Files.createTempDirectory(tempdirPrefix);  //C:/Users/user/AppData/Local/Temp/aabc14508413460554052887
        int previewWidth = Integer.valueOf(Constants.getProperty("preview_width").trim());
        
        File[] files;
        List<MyImage> previews = new ArrayList<MyImage>(1000);
        if (doJustSingle == null) {
            files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith("f_");
                }
            });
        } else {            
            files = new File[1];
            files[0] = doJustSingle.toFile();
            logger.debug("one file: " + files[0]);
        }
        for (File file : files) {
            Path path = file.toPath();
            logger.debug("path: " + path);
            Path outputPathVideoChunck;
            if (doJustSingle == null) {
                outputPathVideoChunck = checkSingle(path, referenceVideoStruct);
            } else {
                outputPathVideoChunck = doJustSingle;
            }
            if (outputPathVideoChunck != null) {
                Path target = tempDirWithPrefix.resolve(path.getFileName()); //the temporary file for the extracted image           
                logger.debug("path: " + path + " Path target: " + target);
                extractFirstImage(outputPathVideoChunck, target);
                logger.debug("after extract target exists: " + Files.exists(target));
                BufferedImage originalImage = ImageIO.read(target.toFile());
                BufferedImage firstImage = resizeImage(originalImage, previewWidth);
                previews.add(new MyImage(firstImage, target, outputPathVideoChunck));
            }
        }
        
        return previews;
    }
}
