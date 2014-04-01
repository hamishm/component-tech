package tests.middleware;

import static org.junit.Assert.*;

import org.junit.Test;

import core.Network;

public class TestNetwork {

	@Test
	public void testGet(){
		String url = "http://ms267.host.cs.st-andrews.ac.uk/ct/test.html";
		String response = Network.callGet(url);
		assertTrue(response, response.equals("This page is for testing a method for ct."));
	}
	
	@Test
	public void testPost(){
		String url = "http://ms267.host.cs.st-andrews.ac.uk/ct/testpost.html";
		String response = Network.callPost(url, "someParams");
		assertTrue(response, 
				response.contains("<form name=\"input\" action=\"testpost.html\" method=\"post\">"));
		assertTrue("I don't have time to set up a server to handle this" instanceof String);
	}
	
}
