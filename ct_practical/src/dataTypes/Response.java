package dataTypes;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import core.JsonObject;

public class Response {

	public int code;
	public String body = null;
	
	public Response(int responseCode, String body) {
		this.code = responseCode;
		this.body = body;
	}
}
