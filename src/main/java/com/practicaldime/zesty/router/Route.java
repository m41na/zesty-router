package com.practicaldime.zesty.router;

import java.util.HashMap;
import java.util.Map;

public class Route {

	public String rid;
	public String path;
	public String method;
	public String accepts;
	public String contentType;
	public Map<String, String> headers = new HashMap<>();
}
