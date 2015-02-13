import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_highgui.imread;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.bytedeco.javacpp.opencv_core.MatVector;

public class FaceRecognizer {
	private int confidenceThreshold;
	
	public FaceRecognizer() {
		this.confidenceThreshold = 60;
	}
	
	public int[] recognizeFace(String filename) {
		org.bytedeco.javacpp.opencv_contrib.FaceRecognizer fr = org.bytedeco.javacpp.opencv_contrib.createLBPHFaceRecognizer();
		org.bytedeco.javacpp.opencv_core.Mat faceImage;
		
		int counter = 0;
		File trainingFacesDir = new File(ImageClassifier.trainingFacesLoc);
		MatVector trainingFaces = new MatVector(trainingFacesDir.list().length);
		org.bytedeco.javacpp.opencv_core.Mat labels = new org.bytedeco.javacpp.opencv_core.Mat(
															trainingFacesDir.list().length, 
															1, 
															CV_32SC1);
		IntBuffer labelBuf = labels.getIntBuffer();
		
		for(String imageFile: trainingFacesDir.list()) {
			faceImage = imread(ImageClassifier.trainingFacesLoc + imageFile, CV_LOAD_IMAGE_GRAYSCALE);
			trainingFaces.put(counter, faceImage);
			labelBuf.put(counter, counter);
			counter++;
		}
		
		faceImage = imread(filename, CV_LOAD_IMAGE_GRAYSCALE);
		fr.train(trainingFaces, labels);
		
		int[] predictedLabel = new int[1];
		double[] confidence = new double[1];
		
		fr.predict(faceImage, predictedLabel, confidence);
		
		int[] predictionArray = new int[2];
		
		if(confidence[0] <= 60)
			predictionArray[0] = predictedLabel[0];
		else
			predictionArray[0] = -1;
		
		if((int) confidence[0] == 0)	//No need to add to Training faces. Same image.
			predictionArray[1] = 0;
		else
			predictionArray[1] = 1;
		
		return predictionArray;
	}
	
	public ArrayList<String> loadFaces() {
		File cache = new File("Cache");
		ArrayList<String> faces = new ArrayList<String>();
		
		for(String face: cache.list()) {
			faces.add(face);
		}
		
		return faces;
	}
}
