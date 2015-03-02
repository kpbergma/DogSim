package dogsim;

import us.monoid.json.JSONException;
import us.monoid.json.JSONStringer;

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
	
	public String toJSON () {
		String retval = "";
		try {
			retval = new JSONStringer()
				.object()
					.key("id")
					.value(fId)
					.key("active")
					.value(true)
					.key("xcoord")
					.value(fX)
					.key("ycoord")
					.value(fY)
					.key("hr")
					.value(fHR)
					.key("temp")
					.value(fTemp)
				.endObject()
				.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retval;
	}
}
