package dogsim;

import java.util.Random;
import java.util.Vector;

/**
 * Main class responsible for setting up an Arena, and adding dogs.
 * dogs are introduced and removed from the interaction space in a
 * pseudorandomn fashion while the program is running.  A maximum of
 * MAX_DOGS can be present at any given time.
 * 
 * @author Karel Bergmann
 *
 * @see Arena
 * @see Dog
 */
public class DogManager {
	private static int MAX_DOGS = 100;
	private static int ARENA_WIDTH = 1500;
	private static int ARENA_HEIGHT = 1000;
	private static int MAX_SPEED = 250;
	private static int MIN_SPEED = 20;
	private static int SLEEP_INTERVAL = 2;
	private static double DOG_INTRO = 0.0001;	//probability of adding a dog
	private static double DOG_REMOVE = 0.0000;	//probability of removing a dog
	private static boolean DISPLAY_GUI = false;	//show the interface
	public static Object elock = new byte[0];   //for synchronizing access to dog Vector.
	
	private Arena fSaddleDome;		//interaction space
	private Vector<Integer> fIds;	//available ids
	private Random fRand;			//for simulation	
	private ArenaDisplay ad;		//the GUI
	
	/**
	 * Constructor initializes a <code>Vector</code> for available
	 * dog IDs and the arena.
	 * 
	 * @param aNumIds maximum number of dogs to support.
	 * 
	 * @see Arena
	 */
	public DogManager (int aNumIds) {
		fIds = new Vector<Integer>(aNumIds, 1);
		for (int i = 0 ; i < aNumIds; i++) {
			fIds.add(new Integer(i));
		}
		fSaddleDome = new Arena(ARENA_WIDTH, ARENA_HEIGHT);
		fRand = new Random();
		
		
		//display the interface if requested.
		if (DISPLAY_GUI) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	ad = new ArenaDisplay(fSaddleDome);
	                ad.createAndShowGUI();
	            }
	        });
		}
	}
	
	public Arena getArena() {
		return(fSaddleDome);
	}
	
	/**
	 * Add a dog to the arena if there aren't too many already.
	 * 
	 * @see Arena
	 * @see Dog
	 */
	private  void insertDog() {
		//only proceed if there are free IDs.
		if (!fIds.isEmpty()) {
			Integer id = fIds.firstElement();
			fIds.removeElementAt(0);

			//construct random max speed and starting HR.
			double speed = fRand.nextInt(MAX_SPEED - MIN_SPEED) + MIN_SPEED;
			long hr = fRand.nextInt((int) (Dog.getMAX_HR() - Dog.getNORMAL_HR())) + Dog.getNORMAL_HR();
			
			//add the dog to the arena and start the thread.
			Dog newDog = new Dog(fSaddleDome, hr, 25, speed, id);
			synchronized (elock) {
				fSaddleDome.getDogs().add(newDog);
			}
			newDog.start();
		}
	}
	
	/**
	 * Removes a dog from the arena
	 * 
	 * @see Arena
	 * @see Dog
	 */
	private void removeDog() {
		Vector<Dog> dogs = fSaddleDome.getDogs();
		
		//synchronize access to dog vector, proceed only if there are dogs to remove.
		synchronized (elock) {
			if (!dogs.isEmpty()) {
				//select a random dog, and remove it.
				int select = fRand.nextInt(dogs.size());
				Dog removed = dogs.elementAt(select);
				
				//release the id back into the available pool.
				fIds.add(new Integer(removed.getID()));
				dogs.removeElementAt(select);
				
				//shut down the dog.
				removed.kill();
			}
		}
	}
	
	/**
	 * Main method.  Loops infinitely.  On each iteration sleeps
	 * SLEEP_INTERVAL milliseconds, introduces a dog with probability
	 * DOG_INTRO and removes a dog with probability DOG_REMOVE.  Supports
	 * upto MAX_DOGS at any one time.
	 * 
	 * @param args not used
	 */
	public static void main (String [] args) {
		Random rand = new Random();
		DogManager dm = new DogManager(MAX_DOGS);
		
		//keep looping
		while (true) {
			//sleep unless the thread is interrupted.
			try {
			    Thread.sleep(rand.nextInt(SLEEP_INTERVAL));
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			
			double prob = rand.nextDouble();
			//possibly remove a dog.
			if (prob < DOG_REMOVE) {
				dm.removeDog();
			}
			//possible add a dog.
			else if (prob < DOG_REMOVE + DOG_INTRO) {
				dm.insertDog();
			}
		}
	}
}
