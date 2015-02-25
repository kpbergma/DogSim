package dogsim;

import java.util.Vector;

/**
 * Class defines a 2-dimensional interaction space for
 * Dog objects.  Includes a <code>Vector</code> for
 * storing dogs present in the <code>Arena</code> which
 * is maintained externally, in this case by the
 * <code>DogManager</code> class.
 * @author Karel Bergmann
 * @see DogManager
 * @see Vector
 */
public class Arena {
	public final double MAX_X;	//horizontal extent of arena
	public final double MAX_Y;	//vertical extent of arena
	
	private Vector<Dog> fDogs;	//dogs present in the area
	
	/**
	 * Constructor which allows specification of Arena size.
	 * <code>fDogs</code> initialized to hold maximally 100
	 * <code>Dog</code>s efficiently.
	 * @param aX horizontal extent
	 * @param aY vertical extent
	 */
	public Arena(double aX, double aY) {
		if (aX < 0)
			throw new IllegalArgumentException(" aX must be greater than 0.");
		if (aY < 0)
			throw new IllegalArgumentException(" aY must be greater than 0.");
		
		fDogs = new Vector<Dog> (100, 1);
		MAX_X = aX;
		MAX_Y = aY;
	}
	
	/**
	 * Returns a <code>Vector</code> of Dogs in the arena.  This <code>Vector</code>
	 * is maintained externally.
	 * @return <code>Vector</code> of Dogs in Arena (maintained externally).
	 */
	public Vector<Dog> getDogs() {
		return fDogs;
	}

}
