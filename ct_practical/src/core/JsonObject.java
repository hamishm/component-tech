package core;

import org.json.simple.JSONObject;

public interface JsonObject {
	void load(JSONObject obj);
	JSONObject getJsonObj();
}
