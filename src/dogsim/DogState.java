package dogsim;

/**
 * Helper class for sending essential <code>Dog</code> info
 * to a RESTful web service.
 * @author Karel Bergmann
 * @see Dog
 */
public class DogState {
	public int fId;		//dog ID
	public double fX;	//x-coordinate
	public double fY;	//y-coordinate
	public long fHR;	//heart rate
	public double fTemp;//body temperature
	
	@Override
	public String toString () {
		return(fId + " " + fX + " " + fY + " " + fHR + " " + fTemp);
	}
}
