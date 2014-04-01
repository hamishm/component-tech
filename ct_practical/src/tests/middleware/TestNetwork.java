package tests.middleware;

import static org.junit.Assert.*;

import org.junit.Test;

import core.Network;
import dataTypes.Response;

public class TestNetwork {

	@Test
	public void testGet(){
		String url = "http://ms267.host.cs.st-andrews.ac.uk/ct/test.html";
		Response response = Network.callGet(url);
		assertTrue(response.body, response.body.equals("This page is for testing a method for ct."));
		assertTrue(response.code == 200);
	}
	
	@Test
	public void testPost(){
		String url = "http://ms267.host.cs.st-andrews.ac.uk/ct/testpost.html";
		Response response = Network.callPost(url, "someParams");
		assertTrue(response.body, 
				response.body.contains("<form name=\"input\" action=\"testpost.html\" method=\"post\">"));
		assertTrue(response.code == 200);
	}
	
}
