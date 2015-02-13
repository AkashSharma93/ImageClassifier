import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class GuiComponent extends Application {
	public static Stage stage;
	public static ImageClassifier ic;
	
	public void start(Stage primaryStage) {
		startup();
		stage = primaryStage;
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/GalleryUI.fxml"));
			Scene scene = new Scene(
							root,
							Screen.getPrimary().getVisualBounds().getWidth(), 
							Screen.getPrimary().getVisualBounds().getHeight());
			
			primaryStage.setTitle("Personal Archival System");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startup() {
		File saveFile = new File("saveFile.cache");
		if(!saveFile.exists()) {
			ic = new ImageClassifier();
		}
		else {
			ic = ImageClassifier.loadState();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
