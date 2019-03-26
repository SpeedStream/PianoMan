import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.io.IOException;

import java.lang.System.*;
import com.fazecast.jSerialComm.*;  //Connnection to ports

public class Simon implements ActionListener
{
	static SerialPort thePort;
	SerialPortTest piano = new SerialPortTest();
	Audio generalAudio = new Audio();

	public static Simon simon;
	public Renderer renderer;
	public static int WIDTH, HEIGHT;
	public int flashed, dark, ticks, indexPattern;
	public boolean playGame = false;
	public boolean creatingPattern = true;
	public ArrayList<Integer> pattern;
	public Random random;
	private boolean gameOver = false;
	Timer timer = new Timer(20, this);

	public static void main(String[] args) {
		simon = new Simon();
	}

	public Simon (){
		JFrame frame = new JFrame("Simon");
		renderer = new Renderer();

		makeFrameFullSize(frame);
		frame.setVisible(true);
		frame.setResizable(true);
		frame.add(renderer);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setConnection();
		startGame();
		//MainMenu();
	}

	private void MainMenu(Graphics2D g){
		//g.drawString("SIMON SAYS", 100, 100);
		//g.drawString("Presiona cualquier tecla para iniciar.");
		if(playGame){
			startGame();
		}
	}

	private void setConnection(){
		System.out.println("setConnection try...");
		if(piano.AutoEstablishPort() == true){
			thePort = piano.getPort();
			readValue();
			System.out.println("Port connected! " + piano.getPortName());
		}
	}

	private void makeFrameFullSize(JFrame aFrame) {
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    	aFrame.setSize(screenSize.width, screenSize.height);
    	WIDTH = screenSize.width/4;
    	HEIGHT = screenSize.height;
	}

	public void startGame()	{
		timer.start();
		random = new Random();
		pattern = new ArrayList<Integer>();
		indexPattern = 0;
		dark = 1;
		flashed = 99;
		ticks = 0;
	}

	private void flashTile(Graphics2D g, Color color, int coordX, boolean flashed){
		if(flashed){
			g.setColor(color);
			g.fillRect(coordX, 0, WIDTH, HEIGHT);
		}
		else{
			g.setColor(color);
			g.fillRect(coordX, 0, WIDTH, HEIGHT);
		}
	}

	private void normalTiles(Graphics2D g){
		flashTile(g, Color.PINK.darker().darker(), 0, false);
		flashTile(g, Color.CYAN.darker().darker(), WIDTH, false);
		flashTile(g, Color.ORANGE.darker().darker(), WIDTH*2, false);
		flashTile(g, Color.GREEN.darker().darker(), WIDTH*3, false);
	}

	private void playGame(Graphics2D g){
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		normalTiles(g);

		switch (flashed){
			case 0:
				flashTile(g, Color.PINK, 0, true);
				generalAudio.setAudio("fa.wav");
				break;
			case 1:
				flashTile(g, Color.CYAN, WIDTH, true);
				generalAudio.setAudio("mi.wav");
				break;
			case 2:
				flashTile(g, Color.ORANGE, WIDTH*2, true);
				generalAudio.setAudio("sol.wav");
				break;
			case 3:
				flashTile(g, Color.GREEN, WIDTH*3, true);
				generalAudio.setAudio("si.wav");
				break;
		}
		generalAudio.play();
		flashed = 99;
	}

	public void paint(Graphics2D g) {
		playGame(g);
	}

	private void readValue(){
		thePort.addDataListener(new SerialPortDataListener(){
			@Override
			public int getListeningEvents(){ return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }

			@Override
			public void serialEvent(SerialPortEvent event){
				SerialPort comPort = event.getSerialPort();
				byte[] newData = new byte[comPort.bytesAvailable()];
				int numRead = comPort.readBytes(newData, newData.length);
				//String value = new String(newData);
				//tileValue.callback(mValue);
				System.out.println("Read! -> " + String.valueOf(new String(newData).charAt(0)));
				switch(String.valueOf(new String(newData).charAt(0))){
					case "O":
						flashed = 0;
						break;
					case "B":
						flashed = 1;
						break;
					case "Y":
						flashed = 2;
						break;
					case "G":
						flashed = 3;
						break;
				}
				ticks = 1;
				if (flashed != 99) {
					if (pattern.get(indexPattern) == flashed) {
						indexPattern++;
						System.out.println("Add indexPattern -> " + indexPattern);
					}
					else {
						System.out.println("GAME OVER");
						startGame();
					}
				}
			}
		});
	}

	private void GameOver(){

	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ticks++;
		if (ticks % 20 == 0) {
			flashed = 99;

			if (dark >= 0) {
				dark--;
			}
		}
		if (creatingPattern) {
			if (dark <= 0) {
				if (indexPattern >= pattern.size()) {
					flashed = random.nextInt(4) % 4;
					System.out.println("Flashed tile -> " + flashed);
					pattern.add(flashed);
					indexPattern = 0;
					creatingPattern = false;
				}
				else {
					flashed = pattern.get(indexPattern);
					indexPattern++;
				}
				dark = 2;
			}
		}
		else if (indexPattern == pattern.size()) {
			creatingPattern = true;
			indexPattern = 0;
			dark = 2;
		}
		renderer.repaint();
	}

/*
	
	@Override
	public void mousePressed(MouseEvent e) {
		
		int x = e.getX(), y = e.getY();
		if (x > 0 && x < (WIDTH) && y > 0 && y < HEIGHT ) {
			flashed = 0;
			ticks = 1;
		}
		else if (x > (WIDTH) && x < (WIDTH)*2 && y > 0 && y < HEIGHT) {
			flashed = 1;
			ticks = 1;
		}
		else if (x > (WIDTH)*2 && x < (WIDTH)*3 && y > 0 && y < HEIGHT) {
			flashed = 2;
			ticks = 1;
		}
		else if (x > (WIDTH)*3 && x < WIDTH && y > 0 && y < HEIGHT) {
			flashed = 3;
			ticks = 1;
		}

		if (flashed != 99) {
			if (pattern.get(indexPattern) == flashed) {
				indexPattern++;
			}
			else {
				gameOver = true;
			}
		}
		else if (gameOver) {
			start();
			gameOver = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}
	*/

}