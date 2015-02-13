import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class ObjectState implements Serializable {
	public HashMap<String, HashSet<String>> faceImageMap;	//Maps names and gallery pictures
	public HashMap<Integer, String> labelFaceMap;	//Maps labels and training images
	public HashMap<String, String> imageFaceMap;	//Maps training images with names
	public HashSet<String> taggedNames;
	public HashSet<String> filterNames;
	public HashSet<String> taggedImages;
	public HashSet<String> filteredImages;
	public int noOfTrainingFaces;
}
