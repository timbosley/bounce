import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JApplet;
import java.util.*;
import javax.sound.sampled.*;
import java.io.*;
import java.net.*;
	
public class Bounce extends Applet implements Runnable, MouseListener, MouseMotionListener, KeyListener {
private final Rectangle gamebox = new Rectangle(400, 400);
private final Rectangle paddle = new Rectangle(0, gamebox.height - 10, 100, 10);
private final Rectangle scorebox = new Rectangle(0, 0, gamebox.width, 20);
private final Rectangle ballbox = new Rectangle(0, scorebox.height, gamebox.width, gamebox.height - scorebox.height - paddle.height);
private final Rectangle paddlebox = new Rectangle(0, paddle.y, gamebox.width, paddle.height);
private int ballx = 50;
private int bally = 50;
private int balld = 10;
private int score = 0;
private int xspeed = 1;
private int yspeed = xspeed;	
private final int pt = 10;
private final int game_over = -1;
private final int game_paused = 0;
private final int game_ready = 1;
private final int game_playing = 2;
private int game_state = game_ready;
private final Font gamefont = new Font(Font.SERIF, Font.PLAIN, 24);
private final Random rany = new Random();
private final Random ranx = new Random();
private final Random rand = new Random();
private Graphics2D g = null;
private final String[] hoorays = {"Hooray!", "Good Job!", "Yes!", "Perfect!", "Your on fire!", "You must be a professional!"};

	public static void main(String[] arg) {
	final Frame f = new Frame("Bounce");
	final Bounce b = new Bounce();
	final Dimension min = b.gamebox.getSize();
	//b.setPreferredSize(min);
	//b.setMinimumSize(min);
	//b.setMaximumSize(min);
	b.setSize(min);
	f.setMinimumSize(min);
	f.addWindowListener(new Closer());
	f.add(b);
	f.setMenuBar(new MenuBar());
	f.pack();
	Insets in = f.getInsets();
	System.out.println(in.top);
	System.out.println(in.bottom);
	System.out.println(in.left);
	System.out.println(in.right);
	f.setSize(min.width, min.height + in.top + in.bottom + 8);
	f.setVisible(true);
	f.setResizable(false);
	b.init();
	}
	
	public void init() {
	addMouseListener(this);
	addMouseMotionListener(this);
	addKeyListener(this);
	setForeground(Color.BLACK);
	setBackground(Color.WHITE);
	g = (Graphics2D)getGraphics();
	System.out.println("Initializing Bounce");
	}	

	public void start() {
	System.out.println("starting");
	setSize(gamebox.getSize());
	setVisible(true);
	}

	public void stop() {
	System.out.println("stopping");
	}

	public void destroy() {
	
	System.out.println("destroying");
	}


	public void mouseClicked(MouseEvent m) {
		if (game_state == game_ready) {
		game_state = game_playing;
		ballx = m.getX();
		bally = m.getY();
			if (bally > scorebox.height || bally < paddlebox.height) {
			bally = gamebox.height/2;
			}
		run();
		}
		else if (game_state == game_over) {
		game_state = game_ready;
		reset();
		}
		else if (game_state == game_paused) {
		game_state = game_playing;
		}
		else if (game_state == game_playing) {
		game_state = game_paused;
		}
	drawGame();
	}

	public void mouseEntered(MouseEvent m) {
	paddle.x = m.getX();	
		if (game_state == game_paused) {
		game_state = game_playing;
		run();
		}
	drawGame(ballbox, paddlebox);
	}

	public void mouseExited(MouseEvent m) {
	int y = m.getY();
		if (game_state == game_playing) {
		game_state = game_paused;
		}
	drawGame();
	}

	public void mousePressed(MouseEvent m) {
	
	}

	public void mouseReleased(MouseEvent m) {

	}
	
	public void mouseDragged(MouseEvent m) {

	}

	public void mouseMoved(MouseEvent m) {
	paddle.x = m.getX();
	int y = m.getY();
	System.out.print("mouse x: ");
	System.out.print(m.getX());
	System.out.print(" mouse y: ");
	System.out.println(m.getY());

		if (y < gamebox.height) {
			if (paddle.x < 0)  {
			paddle.x = 0;
			}
			else if (paddle.x + paddle.width > gamebox.width) {
			paddle.x = gamebox.width - paddle.width;
			}
		}
	drawGame(paddlebox);
	}

	public void keyPressed(KeyEvent k) {
		
		System.out.print("We are pressing key " );
		System.out.println(k.getKeyCode());
		if (k.getKeyCode() == KeyEvent.VK_ESCAPE) {
		game_state = game_over;	
		}		
	}

	public void keyReleased(KeyEvent k) {

	}

	public void keyTyped(KeyEvent k) {

	}

	public void drawGame() {
	drawGame(gamebox);
	}
	
	public void drawGame(Rectangle...area) {
	Rectangle allrect = new Rectangle(); 
		for (Rectangle arect : area) {
		allrect.add(arect);
		}
	drawGame(allrect);	
	}

	public void drawGame(Rectangle area) {
	drawGame(area.x, area.y, area.width, area.height);
	}
	
	public void drawGame(int x, int y, int w, int h) {
	g.setClip(x, y, w, h);
	g.clearRect(x, y, w, h);
	g.setFont(gamefont);
	String scr = "Score: " + String.valueOf(score);
	g.drawString(scr, 0, 19);
	g.draw(scorebox);
	//g.draw(ballbox);
	//g.draw(paddlebox);
		if (game_state != game_playing) {
		String message = "";
			if (game_state == game_ready) { 
			message = "Click to Begin!";
			}
			else if (game_state == game_paused) {
			message = "Game Paused";
			}
			else if (game_state == game_over) {
			message = "Game Over";
			}
		g.drawString(message, getCenterWidth(message), getCenterHeight(message));
		}
		else if (game_state == game_playing) {
		//System.out.print("moving paddle: ");
		//this will draw the ball
		g.fillOval(ballx, bally, balld, balld);
		//this is to draw the paddle
		//System.out.println(paddle.x);
		}
	g.fill(paddle);	
	}

	private int getCenterWidth(String str) {
		if (str.length() > 0) {
		FontMetrics fms = g.getFontMetrics();
		int width = fms.charWidth(str.charAt(0)) * str.length();
		int c = (gamebox.width - width) / 2;
		return c;
		}
		else {
		return 0;
		}
	}

	private int getCenterHeight(String str) {
		if (str.length() > 0) {
		FontMetrics fms = g.getFontMetrics();
		int h = fms.getHeight();
		int c = (gamebox.height - h) / 2;
		return c;
		}
		else {
		return 0;
		}
	}
		
	private void sleep(long time) {
		try {
		Thread.sleep(time);
		}
		catch (InterruptedException I) {
		System.out.println(I.toString());
		}
	}
		
	//this method insures a standard reset for all game variables
	private void reset() {
	game_state = game_ready;
		if (rand.nextBoolean()) {
		xspeed = 1;
		}
		else {
		xspeed = -1;
		}
	yspeed = 1;
	bally = rany.nextInt(gamebox.height/2) + scorebox.height;
	ballx = ranx.nextInt(gamebox.width - balld);
	score = 0;
	}

	public void run() {
		while (game_state == game_playing) {
		sleep(10);
		Point mpoint = getMousePosition();
			if (mpoint != null) {
			paddle.x = mpoint.x;
				if (ballbox.contains(ballx, bally, balld, balld) == false) {
					if (ballx + balld > gamebox.width || ballx < 0) {
					xspeed = (-1 * xspeed);	
					}
					if (scorebox.contains(ballx, bally)) {
					yspeed = (-1 * yspeed);	
					}
					if (bally > gamebox.height) {
					game_state = game_over;
					}
					if (paddle.contains(ballx, bally, balld, balld)) {
					score += pt;
						if (score > 0) {
						int h = rand.nextInt(hoorays.length);
						//showStatus(hoorays[h]);
						}
					drawGame(scorebox);
						/*try {
						URL beep = new URL(getCodeBase(), "beep.wav");
						play(beep);
						}
						catch (MalformedURLException M) {
						System.out.println(M.toString());
						}*/
			
					yspeed = (-1 * yspeed) - 1;
						if (xspeed < 0) {
						xspeed += -1;
						}
						else {
						xspeed += 1;
						}
					}
				}
			ballx = ballx + xspeed;
			bally = bally + yspeed;
			/*System.out.print(paddle.x);
			System.out.print(", ");
			System.out.print(ballx);
			System.out.print(", ");
			System.out.println(bally);
			System.out.print("Enabled: ");
			System.out.println(isEnabled());*/
			drawGame(ballbox, paddlebox);
			}
		}
	}
}

class Closer extends WindowAdapter {

	public void windowClosing(WindowEvent w) {
	System.exit(0);

	}


}
