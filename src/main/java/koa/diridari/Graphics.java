package koa.diridari;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Graphics {

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }
    
    private static void createAndShowGUI() throws Exception {
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        BufferedImage image = ImageIO.read(new File("C:\\Users\\user\\Documents\\java\\swing\\blah\\src\\test\\resources\\previews\\f_0000b6.jpg"));
        //Utils.drawCrossInImage(image);
        Utils.drawRingInImage(image);
        JPanel p = new MyPanel(image); 
        frame.getContentPane().add(p, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
}

class MyPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private Image image;

    MyPanel(BufferedImage image) {
        super(new BorderLayout());
        this.image = image;
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }
    
    @Override
    protected void paintComponent(java.awt.Graphics g) {
        System.out.println("paintComponent");
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this); 
    }
}
