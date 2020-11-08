package koa.diridari;

import javafx.scene.image.ImageView;

public class MyImageView extends ImageView {

    private final MyImage myImage;
    
    public MyImageView(MyImage image) throws Exception {
        //super(image.getFxImage());
        super(image.getByMood());
        myImage = image;
    }

    public MyImage getMyImage() {
        return myImage;
    }
}
