package com.practicaldime.zesty.demo;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.servlet.HandlerRequest;
import com.practicaldime.zesty.servlet.HandlerResponse;
import com.practicaldime.zesty.servlet.HandlerServlet;

public class DemoApp {

	private static final Logger LOG = LoggerFactory.getLogger(DemoApp.class);

	public static void main(String... args) {
		
		int port = 8080;
		String host = "localhost";

		Map<String, String> props = Maps.newHashMap();
		props.put("appctx", "/app");
		props.put("assets", ""); 
		props.put("engine", "freemarker");

		AppServer app = new AppServer(props);

		app.router().get("/", new HandlerServlet() {
			private static final long serialVersionUID = 1L;

			@Override
			public void handle(HandlerRequest request, HandlerResponse response) {
				response.send(String.format("incoming request: '%s'", request.getRequestURI()));
			}
		}).listen(port, host, (msg)-> LOG.info(msg));
	}
}
