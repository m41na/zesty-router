package com.practicaldime.zesty.app;

import java.util.Map;

public class AppProvider {

	public static AppServer provide(Map<String, String> props) {
		return new AppServer(props);
	}
}
