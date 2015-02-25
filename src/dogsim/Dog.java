package dogsim;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.Vector;

/**
 * Class represents a simulated dog which runs around an
 * <code>Arena</code>.  Each dog runs within its own <code>Thread</code>.
 * Each dog is connected to a <code>FitBit</code> which periodically transmits
 * the dog's location and vital signs.  The class includes default simulated
 * dog behaviour such as chasing other dogs and resting when tired.  Dog's
 * normal and max HRs and temps are fixed, but max speed depends on the dog.
 * 
 * @author Karel Bergmann
 * 
 * @see Arena
 * @see Thread
 * @see FitBit
 */
public class Dog extends Thread {
	private static long REFRESH_INTERVAL = 1000;	//milliseconds between updates
	private static long MAX_HR = 200;				//dog's max heart rate
	private static double MAX_TEMP = 45;			//dog's max internal temp in C
	private static double NORMAL_TEMP = 15;			//dog's normal temp.
	private static long NORMAL_HR = 20;				//dog's resting HR.
	private static long CIRCLE = 360;				//number of degrees in a circle
	private static long HALF_CIRCLE = 180;			//half a circle
	private static long NEEDED_REST = 15;			//number of updates in a rest cycle
	private static int ACCEL = 10;					//m/update/update a dog can change speed at
	private static double VISUAL_RANGE = 200;		//farthest a dog can see
	private static int WALL_BUFFER = 30;			//closest a dog comes to a wall
	
	private int fId;				//dog's ID number (unique in arena)
	private volatile double fX;		//x-coordinate
	private volatile double fY;		//y-coordinate
	private double fDirection;		//current heading in degrees
	private double fVelocity;		//current velocity in meters/update
	private long fHR;				//current HR
	private double fTemp;			//current temp in C
	private double fMaxSpeed;		//Dog's maximum velocity
	
	private Arena fArena;			//Dog interaction space
	private FitBit fTransmitter;	//Dog's FitBit transmitter
	private Random fRand;			//for behaviour simulation
	private boolean fActive;		//state variable for active dog
	private boolean fChasing;		//state variable for chasing dog
	private boolean fResting;		//state variable for resting dog
	private long fTimeRested;		//number of updates spent resting
	
	/**
	 * Constructor which specifies starting state of <code>Dog</code>.
	 * 
	 * @param aArena The Arena for dog interactions
	 * @param aHR The dog's current heart rate, must be greater than NORMAL_HR
	 * @param aTemp The dog's current temperature, must be greater than or equal NORMAL_TEMP.
	 * @param aMaxSpeed The dog's maximum speed (different dogs run faster than others)
	 * 					must be greater than 1;
	 * @param aId The dog's ID number
	 * 
	 * @see Arena
	 */
	public Dog (Arena aArena, long aHR, double aTemp, double aMaxSpeed, int aId) {
		if (aId < 0)
			throw new IllegalArgumentException("aID must be non-negative.");
		if (aMaxSpeed <= 1)
			throw new IllegalArgumentException("aMaxSpeed must be greater than 1.");
		if (aTemp < NORMAL_TEMP)
			throw new IllegalArgumentException("aTemp must be NORMAL_TEMP or larger.");
		if (aHR < NORMAL_HR)
			throw new IllegalArgumentException("aBPM must be greater than NORMAL_HR or larger.");
		if (aArena == null)
			throw new IllegalArgumentException("aArena must not be null.");
		
		fRand = new Random();
		fTransmitter = new FitBit(this);
		fArena = aArena;
		
		//initialize dog's state, active.
		fChasing = false;
		fResting = false;
		fActive = true;
		fTimeRested = 0;
		
		fId = aId;
		fHR = aHR;
		fTemp = aTemp;
		fMaxSpeed = aMaxSpeed;
		
		//initialize dog position, to random location.
		fX = fRand.nextInt((int) fArena.MAX_X);
		fY = fRand.nextInt((int) fArena.MAX_Y);
		
		//direction somewhere into the Arena
		fDirection = fRand.nextInt(90);
		
		//speed random, up to max
		fVelocity = fRand.nextInt((int) fMaxSpeed);
		
	}
	
	/**
	 * Allows external entity to kill this dog's thread, and
	 * thus remove it from the simulation.
	 */
	public void kill() {
		fActive = false;
	}
	
	/**
	 * Simulates dog behaviour.  Every <code>REFRESH_INTERVAL</code>
	 * milliseconds, the dog's temp, HR, direction, position and velocity
	 * are updated.  The dog's vital signs are transmitted to a RESTful web
	 * server by it's <code>FitBit</code>.  The loop can be interrupted by a
	 * call to <code>Dog.kill()</code>.
	 * 
	 * @see FitBit
	 * @see Dog.kill()
	 */
	@Override
	public void run() {
		while (fActive) {
			updateTemp();
			updateHR();
			updateDirection();
			updatePosition();
			updateVelocity();
			
			fTransmitter.transmit();
			try {
				sleep(REFRESH_INTERVAL);
			} catch (InterruptedException e) {
				//keep going
			}
		}
	}
	
	public int getID () {
		return fId;
	}
	
	public double getX () {
		return fX;
	}
	
	public double getY () {
		return fY;
	}
	
	public long getHR () {
		return fHR;
	}
	
	public double getTemp () {
		return fTemp;
	}
	
	public static long getNORMAL_HR() {
		return NORMAL_HR;
	}
	
	public static long getMAX_HR() {
		return MAX_HR;
	}
	
	/**
	 * Updates a dog's position based on current velocity.
	 * The dog's heading is altered by 180 degrees if it hits
	 * the edge of the <code>Arena</code>.
	 * 
	 * @see Arena
	 */
	private void updatePosition() {
		//update position
		fX += fVelocity * Math.cos(fDirection);
		fY += fVelocity * Math.sin(fDirection);
		
		//check to make sure they stay away from walls
		boolean changed = false;
		if (fX < WALL_BUFFER) {
			fX = WALL_BUFFER;
			changed = true;
		}
		if (fX >= fArena.MAX_X - WALL_BUFFER) {
			fX = fArena.MAX_X - WALL_BUFFER;
			changed = true;
		}
		if (fY < WALL_BUFFER) {
			fY = WALL_BUFFER;
			changed = true;
		}
		if (fY >= fArena.MAX_Y - WALL_BUFFER) {
			fY = fArena.MAX_Y - WALL_BUFFER;
			changed = true;
		}
		//turn around if they hit a wall
		if (changed == true) {
			fDirection = (((int)(fDirection)) + Dog.HALF_CIRCLE) % Dog.CIRCLE;
		}
			
	}
	
	/**
	 * Update the dog's direction.  The dog will head towards (chase)
	 * the nearest dog which is up to 45 degree off of its current heading
	 * and in <code>VISUAL_RANGE</code>.
	 * If there is no such dog, the dog alters heading by up to 90 degrees.
	 */
	private void updateDirection() {
		Vector<Dog> dogs = fArena.getDogs();
		Dog nearestDog = null;
		double nearest = VISUAL_RANGE;

		//for every dog
		synchronized (DogManager.elock) {
			for (Dog d : dogs) {
				/* if the for is close, and not this dog, and in roughly the
			   		same direction. */
				if ((Point2D.distance(fX, fY, d.getX(), d.getY()) < nearest) &&
						(Point2D.distance(fX, fY, d.getX(), d.getY()) > 1) &&
						(Math.abs(fDirection - heading(d)) < CIRCLE/4)) {
					//set that dog as the best chasing candidate
					nearestDog = d;
					nearest = Point2D.distance(fX, fY, d.getX(), d.getY());
				}
			}
		
			//if a chasable dog has been found, alter heading to chase it
			if (nearestDog != null) {
				fDirection = heading(nearestDog) - 5 + fRand.nextInt(10);
				fChasing = true;
			}
			//otherwise keep wandering
			else {
				fDirection = fDirection - 90 + fRand.nextInt(180);
				fChasing = false;
			}
		}
	}
	
	/**
	 * Updates the dog's velocity.  If the dog is resting, the dog stops
	 * moving.  If the dog is chasing, it increases speed by a random
	 * factor to a maximum of <code>fMaxSpeed</code> m/update.  If a dog
	 * is neither chasing, nor resting (active), it's speed is adjusted
	 * by a random factor.
	 */
	private void updateVelocity() {
		if (fResting == true) {
			fVelocity = 0;
		}
		else if (fChasing == true) {
			fVelocity = fVelocity + fRand.nextInt(ACCEL);
			if (fVelocity > fMaxSpeed) {
				fVelocity = fMaxSpeed;
			}
		}
		fVelocity = fVelocity - 5 + fRand.nextInt(ACCEL);
	}
	
	/**
	 * Maintains the dog's internal temperature in C.  If the dog's
	 * velocity is greater than fMaxSpeed, internal temperature is incremented.
	 * If the dog isn't moving, temp is decremented.  If the temperature exceeds
	 * <code>MAX_TEMP</code> the dog stops chasing and starts resting.  Internal
	 * temp is not allowed to drop below <code>NORMAL_TEMP</code>
	 */
	private void updateTemp() {
		if (fVelocity > fMaxSpeed / 2) {
			fTemp += 1;
		}
		if (fVelocity == 0) {
			fTemp -= 1;
		}
		if (fTemp > Dog.MAX_TEMP) {
			fChasing = false;
			fResting = true;
		}
		if (fTemp < Dog.NORMAL_TEMP) {
			fTemp = Dog.NORMAL_TEMP;
		}
	}
	
	/**
	 * Updates the dog's heart rate.  HR increases by 1BPM
	 * if dog is above 0.5*max speed.  If the dog is resting, HR
	 * decreases by 1 BPM.  If the dog has rested for <code>NEEDED_REST</code>
	 * updates, the dog stops resting.  If the dog's HR exceeds <code>MAX_BPM</code>
	 * the dog stops chasing, stops moving and starts resting.  The dog's HR is not
	 * permitted to drop below <code>NORMAL_HR</code>.
	 */
	private void updateHR() {
		if (fVelocity > fMaxSpeed / 2) {
			fHR += 1;
		}
		if (fResting == true) {
			fHR -= 1;
			fTimeRested++;
			if (fTimeRested > Dog.NEEDED_REST) {
				fResting = false;
				fTimeRested = 0;
			}
		}
		if (fHR > Dog.MAX_HR) {
			fVelocity = 0;
			fChasing = false;
			fResting = true;
		}	
		if (fHR < Dog.NORMAL_HR) {
			fHR = Dog.NORMAL_HR;
		}
	}
	
	/**
	 * Helper method to calculate the heading required
	 * by this <code>Dog</code> to reach <code>aDog</code>
	 * in a straight line.
	 * 
	 * @param aDog The target dog.
	 * @return a heading in degrees.
	 */
	private double heading (Dog aDog) {
		if (aDog == null)
			throw new IllegalArgumentException("aDog must not be null.");
		double dx = aDog.getX() - fX;
		double dy = aDog.getY() - fY;
		double ret = Math.atan2(dy, dx) * Dog.HALF_CIRCLE / Math.PI;
		
		//Ensure the result is positive
		while (ret < 0)
			ret += CIRCLE;
		return ret;
	}
	
	/**
	 * Produces a <code>DogState</code> object with the dog's
	 * current vital signs and location.
	 * 
	 * @return DogState with current vital signs
	 * @see DogState
	 */
	public DogState getDogState () {
		DogState ds = new DogState();
		ds.fId = fId;
		ds.fX = fX;
		ds.fY = fY;
		ds.fHR = fHR;
		ds.fTemp = fTemp;
		return ds;
	}
}
