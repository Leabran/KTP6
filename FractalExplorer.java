import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FractalExplorer {
	
	private int width;
	private int height;
	
	/** Главное окно **/
	private JFrame frame;
	
	/** Элементы на North **/
	private JPanel northP;
	private JComboBox chooseF;
	private JLabel textF;
	
	/** Элементы на Center **/
	private JImageDisplay display;
	private Rectangle2D.Double range;
	
	/** Элементы на South **/
	private JPanel southP;
	private JButton resetB;
	private JButton saveB;

	private ArrayList<FractalGenerator> fractals;

	private File nowPath = null;
	
	/** Количество потоков на выполнении **/
	private int rowsRemaining = 0;
	
	/** Классы-слушатели событий **/
	private class resetButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("Reset button clicked!");
			
			/** Сброс границ фрактала и вызов функции отрисовки **/
			int index = chooseF.getSelectedIndex();
			if (index >= fractals.size()) {
				FractalExplorer.this.setStartImage();
				return;
			}
			
			fractals.get(index).getInitialRange(range);
			FractalExplorer.this.drawFractal(index);
		}
	}
	
	private class saveButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			System.out.println("Save button clicked!");
			
			/** Диалоговое окно **/
			JFileChooser fchooser;
			
			/** Создание диалогового окна для получения пути сохранения файла **/
			if (nowPath == null) {
				fchooser = new JFileChooser();		
			} else {
				fchooser = new JFileChooser(nowPath);
			}
			
			/** Настройка имени **/
			fchooser.setDialogTitle("Choose path");
			
			/** Настройка фильтров **/
			fchooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG Images", "*.png"));
			fchooser.addChoosableFileFilter(new FileNameExtensionFilter("JPEG Images", "*.jpeg"));
			fchooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP Images", "*.bmp"));

			fchooser.setAcceptAllFileFilterUsed(false);

			int result = fchooser.showSaveDialog(frame);	
			if (result == JFileChooser.APPROVE_OPTION) {
				System.out.println("Directory get");
			} else {
				System.out.println("Directory get ERROR");
				return;
			}

			String ext = "";
			String extension = fchooser.getFileFilter().getDescription();
			System.out.println("Desctiption = " + extension);
			
			if (extension.equals("PNG Images")) ext = "png";
			if (extension.equals("JPEG Images")) ext = "jpeg";
			if (extension.equals("BMP Images")) ext = "bmp";

			nowPath = new File(fchooser.getSelectedFile().getPath() + "." + ext);
			System.out.println("Full name = " + nowPath);
			
			/** Запись файла на диск **/
			try 
			{                               
				ImageIO.write(display.getImage(), ext, nowPath);
				System.out.println("Write image success!");
				JOptionPane.showMessageDialog(FractalExplorer.this.frame, "Save is success!", "File save", JOptionPane.INFORMATION_MESSAGE);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(FractalExplorer.this.frame, "Save is failed!", "File save", JOptionPane.WARNING_MESSAGE);
			}
			
		}
	}
	
	private class mouseClickListener implements MouseListener {
		
		/** Событие нажатия на кнопку **/
		public void mouseClicked(MouseEvent e) {
			System.out.println("Mouse button clicked!");

			if (rowsRemaining != 0) return;
			
			int index = chooseF.getSelectedIndex();
			if (index >= fractals.size()) return;
			
			/** Координаты клика **/
			int x = e.getX();
			int y = e.getY();
			
			/** Перевод координат в комплексную плоскость **/
			double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, display.getWidth(), x);
			double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, display.getHeight(), y);

			if (e.getButton() == MouseEvent.BUTTON1) {

				fractals.get(index).recenterAndZoomRange(range, xCoord, yCoord, 0.5);
			}

			if (e.getButton() == MouseEvent.BUTTON3) {

				fractals.get(index).recenterAndZoomRange(range, xCoord, yCoord, 1.5);
			}

			FractalExplorer.this.drawFractal(index);	
		}

		public void mouseEntered(MouseEvent e) {}
 
        public void mouseExited(MouseEvent e) {}
 
        public void mousePressed(MouseEvent e) {}
 
        public void mouseReleased(MouseEvent e) {}
	}
	
	private class comboBoxClickListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			
			System.out.println("Click in ComboBox on " + chooseF.getSelectedItem());

			int index = chooseF.getSelectedIndex();
			
			if (index >= fractals.size()) {
				FractalExplorer.this.setStartImage();
				return;
			}
			
			/** Настройка начального диапазона фрактала **/
			fractals.get(index).getInitialRange(range);
			
			/** Вызов функции рисования **/
			FractalExplorer.this.drawFractal(index);
		}
	}

	public FractalExplorer() {
		this(500);
	}
	
	public FractalExplorer(int size) {
		this(size, size);
	}
	
	public FractalExplorer(int width, int height) {
		this.width = width;
		this.height = height;

		this.range = new Rectangle2D.Double();
		
		/** Создание объектов Фракталов **/
		fractals = new ArrayList<FractalGenerator>();
		fractals.add(new Mandelbrot());

		fractals.add(new Tricorn());
		fractals.add(new BurningShip());
	}

	public void createAndShowGUI() {

		this.frame = new JFrame("Fraktalz");
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.frame.setSize(this.width, this.height);
		this.frame.setResizable(false); 

		northP = new JPanel();
		southP = new JPanel();

		this.resetB = new JButton("Reset display");
		this.resetB.setPreferredSize(new Dimension(frame.getWidth() / 3, 30));
		southP.add(this.resetB);
		
		this.saveB = new JButton("Save image");
		this.saveB.setPreferredSize(new Dimension(frame.getWidth() / 3, 30));
		southP.add(this.saveB);
		
		/** Добавление текста **/
		this.textF = new JLabel("Fraktals: ");
		Font font = saveB.getFont();
		textF.setFont(font);
		northP.add(this.textF);
		
		/** Создание и заполнение списка элементами **/
		this.chooseF = new JComboBox();
		for (int i = 0; i < fractals.size(); i++) {
			chooseF.addItem(fractals.get(i).toString());
		}

		chooseF.addItem("-Empty-");

		chooseF.setSelectedIndex(fractals.size());

		this.chooseF.setPreferredSize(new Dimension(frame.getWidth() / 4, 30));
		northP.add(this.chooseF);

		frame.getContentPane().add(BorderLayout.NORTH, this.northP);
		frame.getContentPane().add(BorderLayout.SOUTH, this.southP);

		int height = frame.getHeight() - 60;
		int width = height;
		frame.setSize(width, frame.getHeight());

		this.display = new JImageDisplay(width, height);
		frame.getContentPane().add(BorderLayout.CENTER, this.display);

		display.addMouseListener(new mouseClickListener());

		resetB.addActionListener(new resetButtonListener());
		saveB.addActionListener(new saveButtonListener());
		chooseF.addActionListener(new comboBoxClickListener());
		
		frame.setVisible(true);
	}
	
	/** Отрисовка фрактала **/

	public void drawFractal(int index) {
		
		/** Очисткапредыдущего рисунка **/
		this.clearImage();

		this.enableUI(false);

		int pictureHeight = display.getHeight();
		int pictureWidth = display.getWidth();
		
		rowsRemaining = pictureHeight;
		
		for (int i = 0; i < pictureHeight; i++) {

			FractalWorker tempThread = new FractalWorker(i, pictureWidth, index);
			tempThread.execute();
		}
		
		
		/*
		for (int x = 0; x < this.width; x++) {
			for (int y = 0; y < this.height; y++) {
				
				// Преобразование координат плоскости пикселей в координаты мнимой плоскости
				double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, display.getWidth(), x);
				double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, display.getHeight(), y);
				
				// Определение входа точки в множество Мандельброта
				int numOfIter = fractals.get(index).numIterations(xCoord, yCoord);
				
				int rgbColor;
				if (numOfIter != -1) {
					float hue = 0.7f + (float) numOfIter / 200f; 
					rgbColor = Color.HSBtoRGB(hue, 1f, 1f); 
				} 
				else {
					rgbColor = Color.HSBtoRGB(0, 0, 0); 
				}
				
				display.drawPixel(x, y, new Color(rgbColor));
				
			}
		}
		*/
	}

	public void setStartImage() {
		this.display.setStartImage();
	}
	
	public void clearImage() {
		this.display.clearImage();
	}

	private class FractalWorker extends SwingWorker<Object, Object> {

		private int numOfStr;
		private int picWidth;

		private int[] strValues;

		private int index;

		public FractalWorker(int y, int picWidth, int index) {
			this.numOfStr = y;
			this.picWidth = picWidth;
			this.index = index;
		}

		@Override
        protected String doInBackground() throws Exception {
			
			strValues = new int[picWidth];
			
			for (int x = 0; x < picWidth; x++) {

				double xCoord = FractalGenerator.getCoord(range.x, range.x + range.width, display.getWidth(), x);
				double yCoord = FractalGenerator.getCoord(range.y, range.y + range.height, display.getHeight(), numOfStr);

				strValues[x] = fractals.get(index).numIterations(xCoord, yCoord);
			}
			
			return null;
		}
		
		/** Вызов после работы фонового потока **/
		@Override
        protected void done() {
			try {
				int x = 0;
				for (int numOfIter: strValues) {
					int rgbColor;
					if (numOfIter != -1) {
						float hue = 0.7f + (float) numOfIter / 200f; 
						rgbColor = Color.HSBtoRGB(hue, 1f, 1f); 
					} 
					else {
						rgbColor = Color.HSBtoRGB(0, 0, 0); 
					}
					
					// 1!После каждого вызова идёт полная перерисовка
					//display.drawPixel(x, numOfStr, new Color(rgbColor));
					
					// 2!Вроде как это неполная перерисовка
					//display.drawPixelWithoutFullRepaint(0, x, numOfStr, 1, 1, new Color(rgbColor));
					
					// 3!Перерисовка без обновления элемента - рисуется на buffered image без присвоение результата jpanel
					display.drawPixelWithNoRepaint(x, numOfStr, new Color(rgbColor));
					
					x++;
				}
				// 3!Обновление рисунка при перерисовке без обновления элемента
				//display.repaintPicture(0, numOfStr, picWidth);
			} 
			catch (Exception e) { 
				e.printStackTrace(); 
            }
			
			rowsRemaining--;
			if (rowsRemaining == 0) {
				enableUI(true);
				
				// 3.1!Обновленеи рисунка при завершении всех потоков (картинка отрисуется на элемент когда вся отрисовка будет готова)
				display.repaintPicture();
			}
		} 
	}

	private void enableUI(boolean enable) {
		chooseF.setEnabled(enable);
		saveB.setEnabled(enable);
		resetB.setEnabled(enable);
	}
	
	/** Точка входа **/

	public static void main(String[] args) {
		FractalExplorer explorer = new FractalExplorer(600);
		explorer.createAndShowGUI();
	}
}
