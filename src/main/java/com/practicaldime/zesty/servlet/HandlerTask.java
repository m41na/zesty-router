package com.practicaldime.zesty.servlet;

import java.io.IOException;

import javax.servlet.ServletException;

@FunctionalInterface
public interface HandlerTask {

	void handle() throws ServletException, IOException;

	default void resolve(HandlerTask task) throws ServletException, IOException {
		task.handle();
	}
}
