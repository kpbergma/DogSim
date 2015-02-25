package dogsim;

import java.io.IOException;
import us.monoid.web.Resty;

/**
 * FitBit class represents wireless transmitters attached
 * to <code>Dog</code> collars while present in the <code>Arena</code>.
 * The <code>FitBit</code> maintains a reference to the attached <code>Dog</code>
 * in order that it may retrieve information it is responsible for transmitting,
 * such as x and y-coordinates, dog ID, heart rate and temperature.
 * 
 * @author Karel Bergmann
 * @see us.monoid.web.Resty
 * @see Arena
 * @see Dog
 */
public class FitBit {
	private Dog fDog;	//Dog FitBit is attached to.
	private static final String SERVER_URL = "http://localhost:8080/locationserver/locationupdate/";
	
	/**
	 * Constructor specifying attached Dog.
	 * @param aDog
	 */
	public FitBit (Dog aDog) {
		if (aDog == null)
			throw new IllegalArgumentException("aDog must not be null.");
		fDog = aDog;
	}
	
	/**
	 * Transmits <code>Dog</code> information to a RESTful web service using
	 * the Resty library.  This is accomplished by using the <code>DogState</code>
	 * helper class to extract pertinent information from the attached <code>Dog</code>.
	 * 
	 * @see us.monoid.web.Resty
	 * @see DogState
	 * @see Dog
	 */
	public void transmit () {		
		/*Resty r = new Resty();
		try {
			r.text(SERVER_URL + fDog.getId(), 
					Resty.put(Resty.content(fDog.getDogState().toString())));
		} catch (IOException e) {
			// do nothing, a lost update isn't critical.
		}*/
		System.out.println(fDog.getDogState().toString());
	}
}
