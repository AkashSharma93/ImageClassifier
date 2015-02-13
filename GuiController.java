
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class GuiController {
	@FXML TilePane tile;
	@FXML ListView listView;
	@FXML ComboBox comboBox;
	@FXML Label statusLabel;
	@FXML ImageView imageView;
	
	GuiController gc = this;

	ObservableList<String> filters = FXCollections.observableArrayList();
	ObservableList<String> taggedNames = FXCollections.observableArrayList();
	
	String name;
	
	@FXML protected void filterImages(ActionEvent ae) {
	//	for(String name: ImageClassifier.os.filterNames) {
	//		System.out.println(name + "\t" + ImageClassifier.os.faceImageMap.get(name));
	//		putImages(ImageClassifier.os.faceImageMap.get(name));
	//	} 
		putImages(ImageClassifier.os.filteredImages);
	}
	
	@FXML protected void addFilter(ActionEvent ae) {
		String name = (String) comboBox.getSelectionModel().getSelectedItem();
		System.out.println(name);
		ImageClassifier.os.filterNames.add(name);
		filters.add(name);
		listView.setItems(filters);
		ImageClassifier.os.filteredImages = new HashSet<String>();
		ImageClassifier.os.filteredImages.addAll(ImageClassifier.os.faceImageMap.get(name));
	}
	
	@FXML protected void reset(ActionEvent ae) {
		System.out.println("Clicked.");
		filters = FXCollections.observableArrayList();
		taggedNames = FXCollections.observableArrayList();
		comboBox.getSelectionModel().clearSelection();
		comboBox.getItems().clear();
		
		if(ImageClassifier.os.taggedNames.size() > 0) {
			taggedNames.addAll(ImageClassifier.os.taggedNames);
			comboBox.setItems(taggedNames);
		}
		ImageClassifier.os.filterNames = new HashSet<String>();
		GuiComponent.ic.refreshImages();
		putImages(ImageClassifier.galleryLoc);
	}
	
	public void putImages(String filename) {
		tile.getChildren().clear();
        File folder = new File(filename);
        File[] listOfFiles = folder.listFiles();

        for (final File file : listOfFiles) {
                ImageView imageView;
                imageView = createImageView(file);
                tile.getChildren().addAll(imageView);
        }
	}
	
	public void putImages(HashSet<String> images) {
		tile.getChildren().clear();
		for(final String file: images) {
			ImageView imageView;
			imageView = createImageView(new File(file));
			tile.getChildren().addAll(imageView);
		}
	}
	
	private ImageView createImageView(final File imageFile) {
        // DEFAULT_THUMBNAIL_WIDTH is a constant you need to define
        // The last two arguments are: preserveRatio, and use smooth (slower)
        // resizing

        ImageView imageView = null;
        try {
            final Image image;
            image = new Image(new FileInputStream(imageFile), 150, 0, true,
                    true);
            imageView = new ImageView(image);
            imageView.setFitWidth(150);
            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent mouseEvent) {

                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){

                        if(mouseEvent.getClickCount() == 2){
                            try {
                            	if(!ImageClassifier.os.taggedImages.contains(imageFile.toString())) {           
                            		System.out.println(imageFile.toString());
                            		GuiComponent.ic.tagFaces(imageFile.toString());
                            		taggedNames = FXCollections.observableArrayList();
                            		taggedNames.addAll(ImageClassifier.os.taggedNames);
                            		comboBox.setItems(taggedNames);
                            		ImageClassifier.os.taggedImages.add(imageFile.toString());
                            	}
                            	else {
                            		System.out.println("Full screen mode");
                            		BorderPane borderPane = new BorderPane();
                                	ImageView imageView = new ImageView();
                                	Image image = null;
                                	image = new Image(new FileInputStream(imageFile));
                                	imageView.setImage(image);
                                	imageView.setStyle("-fx-background-color: BLACK");
                                	imageView.setFitHeight(GuiComponent.stage.getHeight() - 10);
                                	imageView.setPreserveRatio(true);
                                	imageView.setSmooth(true);
                                	imageView.setCache(true);
                                	borderPane.setCenter(imageView);
                                	borderPane.setStyle("-fx-background-color: BLACK");
                                	Stage newStage = new Stage();
                                	newStage.setWidth(GuiComponent.stage.getWidth());
                                	newStage.setHeight(GuiComponent.stage.getHeight());
                                	newStage.setTitle(imageFile.getName());
                                	Scene scene = new Scene(borderPane,Color.BLACK);
                                	newStage.setScene(scene);
                                	newStage.show();
                            	}
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        return imageView;
    }
}
