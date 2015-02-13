/*
 * 	<---- For 11/02/2015 ---->
 *	Need to add code to save/load the state of the app. [DONE]
 *	Should add code to not recognize face unless there are at least 5 faces in the training set. [DONE]
 *	In case of less than 5 images, user should name the faces by himself. [DONE]
 *	Otherwise, the system will recognize the face for him and let him decide whether it is worth keeping. [No face cropping by user right now] [Partially DONE. Haven't handled wrong predictions.]
 *	Test the code in CLI right now, implement GUI later. [DONE]
 *	Good luck!! :)
 *
 *	<---- For 12/02/2015 ---->
 *	Add GUI.
 *	Let user select the face on the image.
 *	Haven't tried detecting faces of only the face sample images. Try it
 *	Need to deal with wrong predictions. Better do that after adding GUI.
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;


public class ImageClassifier {
	public static String trainingFacesLoc = "Training/";
	public static String galleryLoc = "Gallery/";
	String n = "Name";
	
	static ObjectState os;
	BufferedReader br;
	
	public ImageClassifier() {
		os = new ObjectState();
		os.faceImageMap = new HashMap<String, HashSet<String>>();	//Check if obj saved before creating new obj.
		os.labelFaceMap = new HashMap<Integer, String>();
		os.imageFaceMap = new HashMap<String, String>();
		os.taggedNames = new HashSet<String>();
		os.filterNames = new HashSet<String>();
		os.taggedImages = new HashSet<String>();
		refreshImages();
		os.noOfTrainingFaces = 0;
		loadTrainingFaces();
		
		br = new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void refreshImages() {
		os.filteredImages = new HashSet<String>();
		for(String file: new File(galleryLoc).list()) {
			os.filteredImages.add(galleryLoc + file);
		}

	}
	
/*	public static void main(String[] args) {
		File saveFile = new File("saveFile.cache");
		String image = ImageClassifier.galleryLoc + "WP_20150124_13_35_53_Pro.jpg";
		ImageClassifier ic;
		if(!saveFile.exists()) {
			ic = new ImageClassifier();
			ic.tagFaces(image);
		}
		else {
			ic = loadState();
			ic.tagFaces(image);
		}
		
		System.out.println(ic.getImages("karthik"));

	} */
	
	public static ImageClassifier loadState() {
		System.out.println("Loading state.");
		ObjectInputStream ois;
		ImageClassifier ic = new ImageClassifier();
		try {
			ois = new ObjectInputStream(new FileInputStream("saveFile.cache"));
			ic.os = (ObjectState) ois.readObject();
			ois.close();
		} catch (Exception e) { e.printStackTrace(); }
		
		ic.br = new BufferedReader(new InputStreamReader(System.in));
		ic.loadTrainingFaces();
		return ic;
	}
	
	public void tagFaces(String filename) {
		loadTrainingFaces();
		FaceDetector fd = new FaceDetector();
		FaceRecognizer fr = new FaceRecognizer();
//		ArrayList<Mat> listOfFaces = fd.detectFaces(filename); If you want to highlight face.
		System.out.println("Running face detector.");
		fd.detectFaces(filename);
		
		System.out.println("Running face recognizer.");
		ArrayList<String> faces = fr.loadFaces();
		for(String face: faces) {
			String predictedFace;
			int[] predictionArray = new int[2];
			predictionArray[1] = 1;	//Should move image to Training dir.
			if(os.imageFaceMap.size() < 5) {
				System.out.println("Not enough training images. Make new entry. #" + face);
				predictedFace = newFaceEntry(face);
				if(predictedFace.equals("unknown"))
					predictionArray[1] = 0;
			}
			else {
				System.out.println("Recognizing: " + face);
				predictionArray = fr.recognizeFace("Cache/" + face);
				if(predictionArray[0] < 0 || os.imageFaceMap.size() < 5) {
					System.out.println("Face not found. Make new entry. #" + face);
					predictedFace = newFaceEntry(face);
					if(predictedFace.equals("unknown"))
						predictionArray[1] = 0;
				}
				else {
					predictedFace = os.labelFaceMap.get(predictionArray[0]);
					n = predictedFace;
					System.out.println(predictedFace);
				}
			}
			
			moveImage(face, predictionArray[1]);	//Moving image from Cache to Training dir.
			if(!predictedFace.equals("unknown")) {
				os.faceImageMap.get(predictedFace).add(filename);
				if(predictionArray[1] != 0) {
					os.imageFaceMap.put(os.noOfTrainingFaces + ".png", predictedFace);
					os.taggedNames.add(predictedFace);
					loadTrainingFaces();
					os.noOfTrainingFaces++;
				}
			}
		}
		
		saveState();
	}
	
	public HashSet<String> getImages(String name) {
		return os.faceImageMap.get(name);
	}
	
	public String newFaceEntry(String face) {
		System.out.println("Enter name:");
		final JFrame f = new JFrame("Enter name");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			f.getContentPane().add(new ImageDisplayer(ImageIO.read(new File("Cache/" + face))), BorderLayout.CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		final JTextField tf = new JTextField(n);
		JButton b = new JButton("OK");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				n = tf.getText();
				f.dispose();
				synchronized(f) {f.notify();}
			}
		});
		JButton discard = new JButton("Discard");
		discard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				n = "unknown";
				f.dispose();
				synchronized(f) {f.notify();}
			}
		});
		panel.add(tf);
		panel.add(Box.createRigidArea(new Dimension(5,0)));
		panel.add(b);
		panel.add(Box.createRigidArea(new Dimension(5,0)));
		panel.add(discard);
		f.getContentPane().add(panel, BorderLayout.SOUTH);
		f.setSize(300, 300);
		f.setResizable(false);
		f.setVisible(true);
		
		try {
			synchronized(f){f.wait();}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String name = n;
		if(!os.faceImageMap.containsKey(name))
			os.faceImageMap.put(name, new HashSet<String>());
		return name;
	}
	
	public void saveState() {
		ObjectOutputStream oos;
		System.out.println("Saving state.");
		try {
			oos = new ObjectOutputStream(new FileOutputStream("saveFile.cache"));
			oos.writeObject(this.os);
			oos.close();
		} catch (IOException e) { e.printStackTrace(); }
	}

	public void moveImage(String face, int flag) {
		if(flag != 0)	{	//Distance is 0. Same image. No need to move.
			System.out.println("Moving image: " + face);
			Mat faceImg = Highgui.imread("Cache/" + face);
			Highgui.imwrite("Training/" + os.noOfTrainingFaces + ".png", faceImg);
		}
		
		File file = new File("Cache/" + face);	//Clearing Cache.
		file.delete();
	}
	
	public void loadTrainingFaces() {		//Here the String value should be the name of the person!!
		//Check if obj saved before creating new obj.
		os.labelFaceMap = new HashMap<Integer, String>();
		File trainingFacesDir = new File(ImageClassifier.trainingFacesLoc);
		int counter = 0;
		
		for(String faceImage: trainingFacesDir.list()) {
			String face = os.imageFaceMap.get(faceImage);
			os.labelFaceMap.put(counter, face);
			counter++;
		}
	}
}
