package com.practicaldime.zesty.examples;

import java.util.HashMap;
import java.util.Map;

import com.practicaldime.zesty.app.AppProvider;
import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.websock.AppWsPolicy;
import com.practicaldime.zesty.websock.AppWsHandler;

public class SimpleSock {

	public static void main(String... args) {

		Map<String, String> props = new HashMap<>();
		props.put("appctx", "/");
		props.put("assets", "src/test/resources/webapp/chat");
		props.put("cors", "true");
		
		AppServer app = AppProvider.provide(props);
		app.router()
		.websocket("/events/*", () -> new AppWsHandler("events"), () -> AppWsPolicy.defaultConfig())
		.listen(8080, "localhost", (result) -> {
			System.out.println(result);
		});
	}
}
