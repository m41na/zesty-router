package com.practicaldime.zesty.servlet;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.IOException;

public abstract class HomeSsrHandler extends HandlerServlet {

	private static final long serialVersionUID = 1L;
	
	public abstract String getFolder();
	
	public abstract String getSource();
	
	@Override
	public void handle(HandlerRequest request, HandlerResponse response, HandlerPromise promise) {
		try {
			Source source = Source.newBuilder("js", response.getReader(getFolder(), getSource()), "ssr-home").build();
			Context context = Context.create();
			Value value = context.eval(source);
			response.status(200);
			response.send(value.asString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}		
}
