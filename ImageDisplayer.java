import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class ImageDisplayer extends JPanel {
	private BufferedImage image;
	
	public ImageDisplayer(BufferedImage image) {
		this.image = image;
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(image.getScaledInstance(-1, this.getHeight(), Image.SCALE_SMOOTH), 0, 0, null);
		//-1 so that it will maintain aspect ratio of image
	}
}
