import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;


public class FaceDetector {
	public ArrayList<Mat> detectFaces(String filename) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		CascadeClassifier faceDetector = new CascadeClassifier("C:\\OpenCV\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
		Mat image = Highgui.imread(filename);
		ArrayList<Mat> listOfFaces = new ArrayList<Mat>();
	
		if(filename.substring(filename.lastIndexOf('.') + 1).equals("png")) {
			listOfFaces.add(image);
			Highgui.imwrite("Cache/0.png", image);
			return listOfFaces;
		}
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);
		System.out.println(faceDetections.toArray().length);
		
		int counter = 0;
		for (Rect rect : faceDetections.toArray()) {
			Core.rectangle(
					image, 
					new Point(rect.x, rect.y), 
					new Point(rect.x + rect.width, rect.y + rect.height),
					new Scalar(0, 255, 0));
			
			Mat face = image.submat(rect);
			listOfFaces.add(face);
			Highgui.imwrite("Cache/" + counter + ".png", face);	//Saving the faces
			counter++;
		}
		
		Highgui.imwrite("temp.png", image);
		return listOfFaces;
	}
	
	public BufferedImage getBufferedImage(Mat img) {
    	Mat image_tmp = img;
	
	    MatOfByte matOfByte = new MatOfByte();
	
	    Highgui.imencode(".jpg", image_tmp, matOfByte); 
	
	    byte[] byteArray = matOfByte.toArray();
	    BufferedImage bufImage = null;
	
	    try {
	    	InputStream in = new ByteArrayInputStream(byteArray);
	    	bufImage = ImageIO.read(in);
	    } catch (Exception e) { e.printStackTrace(); }
	    
	    return bufImage;
    }
	
	public void show(BufferedImage img) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new ImageDisplayer(img), BorderLayout.CENTER);
		JPanel p = new JPanel();
		p.add(new JButton("Woah"));
		frame.getContentPane().add(p, BorderLayout.EAST);
//		frame.getContentPane().add(new JPanel(), BorderLayout.SOUTH);
	//	frame.getContentPane().add(new JPanel(), BorderLayout.WEST);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new FaceDetector().detectFaces("C:\\Users\\Akash\\workspace\\OpenCV\\Test\\TestProject\\WP_20150124_13_35_07_Pro.jpg");
	}
}
