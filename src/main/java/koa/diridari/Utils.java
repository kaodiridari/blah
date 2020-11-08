package koa.diridari;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {

    final static Logger logger = LoggerFactory.getLogger(Utils.class);
    
	/**
    * Wraps {@link FileUtils#forceDelete}.<br>
    * Eradicates everything in the given path provided that a path-element is one of {tmp, temp} (no upper/lower case).
    * The path itself is deleted.
    * Prevents accidents like passing C:\Users\gda-raheerl to {@link FileUtils#forceDelete}.
    * Not tested for silly paths (too long, strange characters and so on).
    * 
    * @param file
    * @throws Exception the usual suspects 
    * @throws BrackingBadException The given path does not contain one of {tmp, temp, Temp}. This is case-sensitive.
    */
   public static void forceDeleteOnlyInTemp(Path fileOrDir) throws Exception{
      
      List<String> pathElements = new ArrayList<>();
      fileOrDir.forEach(p -> pathElements.add(p.toString().toLowerCase()));
      if (pathElements.contains("tmp") || pathElements.contains("temp") || pathElements.contains("Temp")) {
         if (fileOrDir.toFile().exists()) {
            FileUtils.forceDelete(fileOrDir.toFile());
         }
      } else {
         throw new Exception("The path must contain at least one of {tmp, temp, Temp} but it is: " + fileOrDir);
      }
   }
   
   /**
    * Same as {@link Utils#forceDeleteOnlyInTemp(Path)} for letTheFolderLife = false;
    * If letTheFolderLife is true the folder itself survives - and is empty then.
    * 
    * @param fileOrDir
    * @param letTheFolderLife
    * @throws Exception
    */
   public static void forceDeleteOnlyInTemp(Path fileOrDir, boolean letTheFolderLife) throws Exception {
   	   logger.debug("fileOrDir: " + fileOrDir + ", letTheFolderLife: " + letTheFolderLife);
       if (letTheFolderLife) {
   		Iterator<Path> it = Files.list(fileOrDir).iterator();
   		while (it.hasNext()) {
   			Path deleteMe = it.next();
   			forceDeleteOnlyInTemp(deleteMe);
   		}
   	} else {
   		forceDeleteOnlyInTemp(fileOrDir);
   	}
   }
   
   /**
    * Deletes the given folder and creates again. Only in temporary-directories.
    * <br> see  {@link forceDeleteOnlyInTemp}.<br>
    * @param theFolder
    * @throws Exception
    * @throws InterruptedException
    */
	public static void forceCreateNewOnlyInTemp(Path theFolder) throws Exception, InterruptedException {
		Utils.forceDeleteOnlyInTemp(theFolder);      
      File createMe = theFolder.toFile();
      int loops = 1;
      do {
         try {
            FileUtils.forceMkdir(createMe); //may fail or may not fail; maybe try several times???
         } catch (Exception e) {
            //logger.warn("Uuuups, I take a short nap and try again", e);
            loops++;
            Thread.sleep(1000);
         } 
      } while (!createMe.exists() && loops < 10);
      if (loops >= 10) {
         throw new Exception("I tried it hard to create " + createMe + " but I am such a failure.");
      }
      //logger.debug("Created: " + createMe + " after: " + loops + " attempts.");
	}
	
	public static List<Path> sortCacheFiles(List<Path> cacheFiles) {
	    
	    class HelpMe {
	        final Path all;
	        final int number;
            HelpMe(Path all) {            
                this.all = all;
                String filename = all.getFileName().toString();
                String hex = StringUtils.substringAfter(filename, "f_");
                hex = StringUtils.substringBeforeLast(hex, ".");
                hex = StringUtils.stripStart(hex, "0");
                this.number = Integer.parseInt(hex, 16);
            }
	    }
	    
	    List<HelpMe> liHelpMe = new ArrayList<HelpMe>(cacheFiles.size());
	    for (Path cacheFile : cacheFiles) {
            HelpMe helpMe = new HelpMe(cacheFile);
            liHelpMe.add(helpMe);
        }
	    
	    liHelpMe.sort(new Comparator<HelpMe>() {

            @Override
            public int compare(HelpMe hm1,HelpMe hm2) {
                if (hm1.number == hm2.number) {
                    return 0;
                } else if (hm1.number < hm2.number) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
	    
	    List<Path> sortedPaths = new ArrayList<Path>(liHelpMe.size());
	    for (HelpMe hm : liHelpMe) {
            sortedPaths.add(hm.all);
        }
	    
	    return sortedPaths;
	}
	
	/**
	 * Draws a red cross in the middle of the given picture.
	 * 
	 * @param image
	 */
    public static void drawCrossInImage(BufferedImage image) {
        Graphics2D graphics2d = image.createGraphics();
        graphics2d.setPaint(Color.RED);
        
        //center
        int center_x = image.getWidth() / 2;
        int center_y = image.getHeight() / 2;
        int rectangle_thickness = image.getWidth() / 40;
        float arcw = rectangle_thickness, arch = rectangle_thickness;
        int rectangle_length = (int) ((float)image.getHeight() * 0.8f);
        Point2D topHori = new Point2D.Float(center_x - rectangle_length / 2, center_y - rectangle_thickness / 2);
        Point2D topVerti = new Point2D.Float(center_x - rectangle_thickness / 2, center_y - rectangle_length / 2);
        
        RoundRectangle2D roundedRectangleHori =
                new RoundRectangle2D.Float((int)topHori.getX(), (int)topHori.getY(), rectangle_length, rectangle_thickness, arcw, arch);
        RoundRectangle2D roundedRectangleVerti =
                new RoundRectangle2D.Float((int)topVerti.getX(), (int)topVerti.getY(), rectangle_thickness, rectangle_length, arcw, arch);
        
        AffineTransform transform = new AffineTransform();
        transform.rotate(Math.toRadians(45), center_x, center_y);
        graphics2d.transform(transform);
        graphics2d.fill(roundedRectangleHori);
        graphics2d.fill(roundedRectangleVerti);
        graphics2d.draw(roundedRectangleHori);
        graphics2d.draw(roundedRectangleVerti);
        graphics2d.dispose();
    }
    
    /**
     * Draws a green ring in the middle of the given picture.
     * 
     * @param image
     */
    public static void drawRingInImage(BufferedImage image) {
        Graphics2D graphics2d = image.createGraphics();
        graphics2d.setPaint(Color.GREEN);        
        
        //center
        int center_x = image.getWidth() / 2;
        int center_y = image.getHeight() / 2;
        int ring_thickness = image.getWidth() / 40;
        graphics2d.setStroke(new BasicStroke(ring_thickness));
        
        int ring_radius = (int) ((float)image.getHeight() * 0.8f) / 2;
        Ellipse2D circle = new Ellipse2D.Float(center_x - ring_radius, center_y - ring_radius, ring_radius * 2, ring_radius * 2);        
        
        graphics2d.draw(circle);
        graphics2d.dispose();
    }
    
    public static final BufferedImage clone(BufferedImage image) {
        BufferedImage clone = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = clone.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return clone;
    }
}
