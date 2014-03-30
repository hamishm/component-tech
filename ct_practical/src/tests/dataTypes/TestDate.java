package tests.dataTypes;

import static org.junit.Assert.assertTrue;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.junit.Test;

import dataTypes.Date;

public class TestDate {

	@Test
	public void testFromJson() {
		String dateS = "2003-01-1712:12:12.342-0800";
		JSONObject obj = new JSONObject();
		obj.put("date", dateS);
		Date d = new Date(obj);
		String ds = d.toString();
		assertTrue("Datetimes not equivilent: "+dateS+","+ds, ds.equals(dateS));
	}

	@Test
	public void testToJson() {
		// YYYY-MM-DDThh:mm:ss.fffZ
		String dateS = "2003-01-1712:12:12.342-0800";
		Date d = new Date(dateS);
		String ds = d.toString();
		assertTrue("Datetimes not equivilent: "+dateS+","+ds, ds.equals(dateS));
	}

}
