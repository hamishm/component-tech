package dataTypes;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONObject;

import core.JsonObject;

public class Date implements JsonObject {

	DateTime dt;
	DateTimeZone z;
	DateTimeFormatter fmt;
	
	public Date(String dateS) {
		fromString(dateS);
	}

	public Date(JSONObject obj) {
		load(obj);
	}

	/**
	 * Use toString method to get proper representation of date
	 * 
	 * <b>Always returns null</b>
	 */
	@Override
	public JSONObject getJsonObj() {
		return null;
	}

	/**
	 * Pass the Data object
	 */
	@Override
	public void load(JSONObject obj) {
		String s = (String) obj.get("date");
		fromString(s);
	}
	
	private void fromString(String dts){
		getFormatter();
		dt = fmt.parseDateTime(dts);
		z = dt.getZone();
		
	}
	
	private void getFormatter(){
		fmt = DateTimeFormat.forPattern("yyyy-MM-ddHH:mm:ss.SSSZ").withOffsetParsed();
		if(z != null){
			fmt.withZone(z);
		}
	}

	public DateTime getDateTime() {
		return dt.withZone(z);
	}

	public String toString() {
		getFormatter();
		String dts = dt.toString(fmt);
		return dts;
	}
}
