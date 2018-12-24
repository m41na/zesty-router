package com.practicaldime.zesty.router;

import java.util.HashMap;
import java.util.Map;

public class Request {

	public String url;
	public String method;
	public Map<String, String[]> headers = new HashMap<>();
	
	public String getHeader(String name) {
		return headers.keySet().contains(name)? headers.get(name)[0] : null;
	}
}
