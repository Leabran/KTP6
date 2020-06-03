import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class JImageDisplay extends JPanel {

	private int width;
	private int height;

	private BufferedImage bImg;

	private Graphics g;

	public JImageDisplay() {
		
	}
	
	public JImageDisplay(int size) {
		this(size, size);
	}
	
	public JImageDisplay(int width, int height) {
		this.width = width;
		this.height = height;	
		
		/** Создание буферной картинки **/
		bImg = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);

		g = bImg.getGraphics();
я
		this.setStartImage();
		this.repaint();
	}

	public void drawPixelWithoutFullRepaint(long tm, int x, int y, int width, int height, Color color) {
		/** Установка необходимого цвета **/
		g.setColor(color);
		
		/** Закрашиване необходимого пикселя **/
		g.fillRect(x, y, 1, 1);
		
		this.repaint(tm, x, y, width, height);
	}
	
	public void drawPixelWithNoRepaint(int x, int y, Color color) {
		/** Установка необходимого цвета **/
		g.setColor(color);
		
		/** Закрашиване необходимого пикселя **/
		g.fillRect(x, y, 1, 1);
	}

	public void repaintPicture() {
		this.repaint();
	}
	
	public void repaintPicture(int x, int y, int width) {
		this.repaint(0, x, y, width, 1);
	}

	public void drawPixel(int x, int y, Color color) {

		g.setColor(color);

		g.fillRect(x, y, 1, 1);
		
		this.repaint();
	}

	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bImg, 0, 0, null);
    }

	public void clearImage() {
		g.setColor(Color.black);
		g.fillRect(0, 0, bImg.getWidth(), bImg.getHeight());
		this.repaint();
	}
	
	public void setStartImage() {
		this.clearImage();
		
		g.setColor(Color.orange);
		g.fillRect(bImg.getWidth() / 2 - 51, bImg.getHeight() / 2 - 51, 100, 100);	
		g.setColor(Color.white);
		g.fillRect(bImg.getWidth() / 2 - 25, bImg.getHeight() / 2 - 25, 50, 50);
		
		g.setColor(Color.orange);
		g.fillRect(bImg.getWidth() / 2 - 13, bImg.getHeight() / 2 - 13, 25, 25);	
		g.setColor(Color.white);
		g.fillRect(bImg.getWidth() / 2 - 7, bImg.getHeight() / 2 - 7, 13, 13);
		this.repaint();
	}
	
	public BufferedImage getImage() {
		return bImg;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
}
