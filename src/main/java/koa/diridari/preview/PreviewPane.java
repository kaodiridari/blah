package koa.diridari.preview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import koa.diridari.MyImage;
import koa.diridari.MyImage.MOOD;
import koa.diridari.MyImageView;

public class PreviewPane extends BorderPane {

	final Logger logger = LoggerFactory.getLogger(PreviewPane.class);

	final private PreviewMouseEventHandler previewMouseEventHandler;// = new PreviewMouseEventHandler();

	final private PreviewContextMenueRequestedHandler previewContextMenueRequestedHandler;

	private ScrollPane scrollPane;

	private ArrayList<MyImageView> views;

	private PreviewModell previewModel;

	private Map<UUID, MOOD> mapImagesById = new HashMap<>();

	private Map<UUID, MyImageView> viewsById = new HashMap<>();

	private HBox hbox;

	public PreviewPane(PreviewContextMenueRequestedHandler pcmrh, PreviewMouseEventHandler pmeh, PreviewModell pm)
			throws Exception {
		previewMouseEventHandler = pmeh;
		previewContextMenueRequestedHandler = pcmrh;
		previewModel = pm;

		Button buttonLeft = new Button("<");
		Button buttonRight = new Button(">");

		EventHandler<ActionEvent> eventButtonLeft = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				logger.debug("left button " + scrollPane.getHvalue());
				scrollPane.setHvalue(0.0);
			}
		};

		EventHandler<ActionEvent> eventButtonRight = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				logger.debug("right button" + scrollPane.getHvalue());
				scrollPane.setHvalue(1.0);
			}
		};

		buttonLeft.setOnAction(eventButtonLeft);
		buttonRight.setOnAction(eventButtonRight);

		setLeft(buttonLeft);
		setRight(buttonRight);

		scrollPane = new ScrollPane();
		hbox = new HBox(20);

		// from the model
		List<MyImage> previews = previewModel.getPreviews();
		logger.debug("previews: " + previews.size());

		// and make the fx ImageViews
		views = new ArrayList<MyImageView>();
		for (MyImage myImage : previews) {
			prepareNewImageView(myImage);
		}

		// List<MyImageView> ivs =
		// myImageViews(Paths.get("C:\\Users\\user\\Documents\\java\\swing\\blah\\src\\test\\resources\\previews"));
		hbox.getChildren().setAll(views);
		scrollPane.setContent(hbox);
		setCenter(scrollPane);
	}

	/**
	 * Updates the view. For new previews (and only for new ones) this can be called
	 * from another thread.
	 * 
	 * @throws Exception
	 */
	public void update() throws Exception {
		logger.debug("updating...");

		// Get all the previews and choose the dirty or new ones.
		List<MyImage> justAll = previewModel.getPreviews();
		for (MyImage myImage : justAll) {
			logger.debug(myImage.getOutputPathVideoChunck() + ", " + myImage.getMood());
		}

		for (MyImage justFromTheModel : justAll) {
			MOOD myMood = mapImagesById.get(justFromTheModel.getId());
			if (myMood == null) {
				logger.debug("I don't know this: " + justFromTheModel.getId() + " asuming it's new.");

				// Add the new preview at the end. Remember this is called from the
				// NewFileHandler - thread.
				MyImageView newImageView = prepareNewImageView(justFromTheModel);
				if (Platform.isFxApplicationThread()) {
					logger.debug("runNow....");
					hbox.getChildren().add(newImageView);
					scrollPane.setHvalue(1.0);					
				} else {
					Platform.runLater(() -> {
						logger.debug("runLater....");
						hbox.getChildren().add(newImageView);
						scrollPane.setHvalue(1.0);
					});
				}
			} else {
				if (!myMood.equals(justFromTheModel.getMood())) {
					logger.debug("This is a dirty one: " + justFromTheModel.getId());

					// do something clever
					MyImageView dirtyView = viewsById.get(justFromTheModel.getId());
					dirtyView.setImage(justFromTheModel.getByMood());

					// update our state
					mapImagesById.put(justFromTheModel.getId(), justFromTheModel.getMood());
				} else {
					logger.debug("This is a clean one: " + justFromTheModel.getId() + " doing nothing.");
				}
			}
		}
	}

	private MyImageView prepareNewImageView(MyImage myImage) throws Exception {
		MyImageView imageView = new MyImageView(myImage);
		imageView.setOnMouseClicked(previewMouseEventHandler);
		imageView.setOnContextMenuRequested(previewContextMenueRequestedHandler);
		views.add(imageView);
		mapImagesById.put(imageView.getMyImage().getId(), imageView.getMyImage().getMood());
		viewsById.put(imageView.getMyImage().getId(), imageView);
		return imageView;
	}
}
