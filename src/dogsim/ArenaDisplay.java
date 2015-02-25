package dogsim;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.*;        
 
/**
 * Class for visualizing an arena with dogs in it.
 * GUI consists of a main interaction area and a dog monitor.
 * 
 * The interaction area shows the arena and positions of dogs
 * which are labeled with their IDs.  When the user hovers over
 * a dog, its vital signs are shown by the mouse pointer and in the
 * dog monitor at the bottom.  When the user ceases hovering, the
 * dog's vitals remain in the dog monitor until a different dog
 * is hovered over.  
 * 
 * The interaction space shows the most dense cluster of dogs
 * by highlighting it in red.  When dogs get close to one another
 * and are highlighted, their ID numbers are shown on a watch list.
 * 
 * @author Karel Bergmann
 *
 * @see Arena
 * @see Dog
 */
public class ArenaDisplay {
	private Arena fArena;		//Arena to display
	private DrawPanel dpnl;		//main interaction space
	private JTextArea fText;	//dog monitor
	
	/**
	 * Constructor with <code>Arena</code> to display.
	 * 
	 * @param aArena The <code>Arena</code> to show.
	 * 
	 * @see Arena
	 */
	public ArenaDisplay(Arena aArena) {
		super();
		fArena = aArena;
	}
	
    /**
     * Create and show the window
     */
    public void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Arena Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setLayout(new BorderLayout());
 
        //Add the interaction space and dog monitor
        fText = new JTextArea(1, 15);
        dpnl = new DrawPanel(fArena, fText); 
        frame.getContentPane().add(dpnl, BorderLayout.CENTER);
        frame.getContentPane().add(fText, BorderLayout.SOUTH);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}

/**
 * Displays the interaction area and controls the dog monitor.
 * 
 * @author Karel Bergmann
 */
class DrawPanel extends JPanel {
	private static final long serialVersionUID = 2402045072396021046L;
	private static int DISPLAY_WIDTH = 800;		//width of displayed arena
	private static int DISPLAY_HEIGHT = 600;	//height
	private static int DOG_W = 30;				//width of dog icon
	private static int DOG_H = 30;				//height of dog icon
	private static int REPAINT_INTERVAL = 10;	//frequency with which to repaint
	private static int POPUP_WIDTH = 200;
	private static int POPUP_HEIGHT = 30;
	private static int POPUP_OFFSET = 20;
	private static int CLUSTER_RANGE = 200;
	
	private Arena fArena;						//the arena to paint
	private JTextArea fText;					//the dog monitor
	private int activeDogID = -1;				//the dog to display in dog monitor
	
	/**
	 * Constructor, sets up timer to animate display.
	 * 
	 * @param aArena The arena to display
	 * @param aText The dog monitor at the bottom of the page
	 */
	public DrawPanel(Arena aArena, JTextArea aText) {
		super();
		fText = aText;
		fArena = aArena;
		
		//Set up a repaint every 10 milliseconds
		this.setPreferredSize(new Dimension(DISPLAY_WIDTH, DISPLAY_HEIGHT));
		ActionListener taskPerformer = new ActionListener() {
			  public void actionPerformed(ActionEvent evt) {
			    repaint();
			  }
		};
		new Timer(REPAINT_INTERVAL, taskPerformer).start();
	}
	
	/**
	 * Loads images for GUI
	 * @param fileName name of image file.
	 * @return Image created from the image file.
	 */
	public BufferedImage loadImage(String fileName){

		BufferedImage buff = null;
		try {
		    buff = ImageIO.read(getClass().getResourceAsStream(fileName));
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    return null;
		}
		return buff;
	}
	
	/**
	 * Draws everything on the canvas and updates the
	 * dog monitor.
	 * 
	 * @param g Graphics context
	 */
    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        
        //set the background image to match the canvas dimensions.
        Image back = loadImage("back.jpg");
        g2d.drawImage(back, 0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT, null);
        
        //draw the dogs
        drawDogs (g2d);
        
        //see if the mouse is hovering over a dog icon.
        Point mouse = this.getMousePosition();
        //for each dog, see if the mouse is pointing at its icon.
        for (Dog d : fArena.getDogs()) {
        	if ((mouse != null) &&
        		(mouse.x > calculateLocation(d).x - (DOG_W/2)) &&
        		(mouse.x < calculateLocation(d).x + (DOG_W/2)) &&
        		(mouse.y > calculateLocation(d).y - (DOG_H/2)) &&
        		(mouse.y < calculateLocation(d).y + (DOG_H/2))) {
        		
        		//if so, show a white rectangle with dog info in it.
        		g2d.setColor(Color.white);
        		g2d.fillRect(mouse.x, mouse.y, POPUP_WIDTH, POPUP_HEIGHT);
        		g2d.setColor(Color.blue);
        		g2d.drawString("ID" + d.getID() + " " + d.getHR() + " BPM  " + d.getTemp() + " C", 
        				mouse.x+POPUP_OFFSET, mouse.y+POPUP_OFFSET);
        		
        		//update the dog monitor to show the same info.
        		//set the selected dog to be displayed in the dog monitor
        		fText.setText("ID" + d.getID() + " " + d.getHR() + " BPM  " + d.getTemp() + " C");
        		activeDogID = d.getID();
        		break;
        	}
        }
        
        //If the selected dog is still in the simulation
        //show its vitals in the dog monitor
        boolean activeDogFound = false;
        for (Dog d : fArena.getDogs()) {
        	if (d.getID() == activeDogID) {
        		fText.setForeground(Color.blue);
        		fText.setText("ID" + d.getID() + " " + d.getHR() + " BPM  " + d.getTemp() + " C  " +
        					  "X-COORD " + (int)d.getX() + "  Y-COORD " + (int)d.getY());
        		
        		//draw a blue rectangle around the dog being monitored
        		g2d.setColor(Color.blue);
        		Point pos = calculateLocation(d);
        		g2d.drawRoundRect((int)pos.getX() - DOG_W/2,
        				(int)pos.getY() - DOG_H/2,
        				DOG_W,
        				DOG_H,
        				5, 5);
        		activeDogFound = true;
        	}
        }
        
        //if the monitored dog is no longer present, clear the monitor.
        if (activeDogFound == false) {
        	activeDogID = -1;
        	fText.setText("");
        }
        
        //Find and highlight the most dense dog cluster
        Dog root = null;		//centroid candidate
        Dog northest = null;	//highest most cluster member
        Dog westest = null;		//left most cluster member
        Dog southest = null;	//lowest most cluster member
        Dog eastest = null;		//right most cluster member
        Vector<Dog> rootVector = new Vector<Dog>(0, 1);		//dogs in the cluster
        
        //Examine each dog as a centroid candidate
        for (Dog d1 : fArena.getDogs()) {
        	Vector<Dog> testVector = new Vector<Dog>(0, 1);
        	Dog south = d1;
        	Dog east = d1;
        	Dog north = d1;
        	Dog west = d1;
        	
        	//For every other dog in the arena
        	for (Dog d2 : fArena.getDogs()) {
        		//if it is in clustering range
        		if ((!d2.equals(d1)) &&
        			(Point2D.distance(d1.getX(), d1.getY(), d2.getX(), d2.getY()) < CLUSTER_RANGE)) {
        			//add the dog to the cluster
        			//expand the bounding box to the new dog, if necessary.
        			testVector.add(d2);
        			if (d2.getX() > east.getX())
        				east = d2;
        			if (d2.getY() > south.getY())
        				south = d2;
        			if (d2.getX() < west.getX())
        				west = d2;
        			if (d2.getY() < north.getY())
        				north = d2;
        		}
        	}
        	
        	//check if this is the biggest cluster
        	if (testVector.size() > rootVector.size()) {
        		root = d1;
        		rootVector = testVector;
        		southest = south;
        		eastest = east;
        		northest = north;
        		westest = west;
        	}	
        }
        
        //If a largest cluster has been detected...
        if (root != null) {
        	//find cluster boundaries on canvas.
        	Point pSouth = calculateLocation(southest);
        	Point pEast = calculateLocation(eastest);
        	Point pNorth = calculateLocation(northest);
        	Point pWest = calculateLocation(westest);
        	
        	//draw a box over the cluster
        	g2d.setColor(Color.red);
        	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        	g2d.fillRoundRect((int)pWest.getX() - DOG_W/2,
    				(int)pNorth.getY() - DOG_H/2,
    				((int)pEast.getX() - (int)pWest.getX()) + DOG_W,
    				((int)pSouth.getY() - (int)pNorth.getY()) + DOG_H,
    				15, 15);	
        	
        	//draw a border around the box
        	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        	g2d.drawRoundRect((int)pWest.getX() - DOG_W/2,
    				(int)pNorth.getY() - DOG_H/2,
    				((int)pEast.getX() - (int)pWest.getX()) + DOG_W,
    				((int)pSouth.getY() - (int)pNorth.getY()) + DOG_H,
    				15, 15);	
        	
        	//display a watch list of dogs in the cluster in case they fight
        	String watchList = "Watch List: " + root.getID();
        	for (Dog d : rootVector) {
        		watchList = watchList + ", " + d.getID();
        	}
        	g2d.setColor(Color.white);
        	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        	g2d.fillRect(0, 0, 200, 20);
        	g2d.setColor(Color.red);
        	g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        	g2d.drawString(watchList, 0, 15);
        }
    }

    /**
     * Draw the dogs on the interaction space based on positions in the
     * arena.
     * 
     * @param g2d Graphics context
     */
    private void drawDogs (Graphics2D g2d) {
    	Vector<Dog> dogs = fArena.getDogs();
    	//build dog image.
        Image dog = loadImage("dog.png");
        
    	//for each dog, calculate the position on the canvas
    	//and show the icon.
    	for (Dog d : dogs) {
    		Point location = calculateLocation(d);
    		
    		//center the icon around the location
        	g2d.drawImage(dog, location.x - DOG_W/2, location.y - DOG_H/2, DOG_W, DOG_H, null);
        	
        	//draw the id number in the icon
        	g2d.setColor(Color.white);
        	g2d.drawString("" + d.getID(), location.x-8, location.y+5);
        }
    }
    
    /**
     * Calculates the location of a dog on the canvas,
     * given the location of the dog in the arena.
     * 
     * @param aDog The dog whose canvas position to find.
     * @return Point indicating the dog's relative position on
     * 				 the canvas, given location in the arena.
     * 
     * @see Arena
     * @see Dog
     */
    private Point calculateLocation(Dog aDog) {
    	int xLoc = (int) (aDog.getX() * DISPLAY_WIDTH / fArena.MAX_X);
    	int yLoc = (int) (aDog.getY() * DISPLAY_HEIGHT / fArena.MAX_Y);
    	return new Point(xLoc, yLoc);
    }
    
    /**
     * Paint the component.  Lock while doing this
     * to prevent <code>ConcurrentModificationException</code>s
     * pertaining to the Dogs Vector in Arena.
     */
    @Override
    public void paintComponent(Graphics g) {
        synchronized(DogManager.elock) {
        	super.paintComponent(g);
        	doDrawing(g);
        }
    }
}
