package com.practicaldime.zesty.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.practicaldime.zesty.app.AppServer;
import com.practicaldime.zesty.route.BodyWriter;
import com.practicaldime.zesty.route.RouteResponse;
import com.practicaldime.zesty.view.ViewEngine;

public class HandlerResponse extends HttpServletResponseWrapper implements RouteResponse {

	private final Logger LOG = LoggerFactory.getLogger(HandlerResponse.class);
	protected byte[] content = new byte[0];
	protected boolean redirect = false;
	protected boolean forward = false;
	protected String routeUri;
	protected String contextPath;
	protected String templateDir;

	public HandlerResponse(HttpServletResponse response) {
		super(response);
		init();
	}

	private void init() {
		setContentType("text/html;charset=utf-8");
	}

	@Override
	public void header(String header, String value) {
		setHeader(header, value);
	}
	
	@Override
	public void templates(String folder) {
		this.templateDir = folder;
	}

	@Override
	public void context(String ctx) {
		this.contextPath = ctx;
	}

	@Override
	public void status(int status) {
		setStatus(status);
	}

	@Override
	public void sendStatus(int status) {
		status(status);
		send(HttpStatus.getMessage(status));
	}

	@Override
	public void send(String payload) {
		this.content = payload.getBytes(StandardCharsets.UTF_8);
		setContentLength(this.content.length);
	}

	@Override
	public void json(Object payload) {
		setContentType("application/json");
		this.content = new Gson().toJson(payload).getBytes(StandardCharsets.UTF_8);
		setContentLength(this.content.length);
	}

	@Override
	public void jsonp(Object payload) {
		setContentType("application/json");
		this.content = new Gson().toJson(payload).getBytes(StandardCharsets.UTF_8);
		setContentLength(this.content.length);
	}

	@Override
	public <T> void xml(Object payload, Class<T> template) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			JAXBContext context = JAXBContext.newInstance(template);
			Marshaller m = context.createMarshaller();
			// for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(template, bytes);
		} catch (JAXBException e) {
			LOG.error("Could not transform content to response body");
		}
		setContentLength(bytes.size());
		this.content = bytes.toByteArray();
	}

	@Override
	public <T> void content(T payload, BodyWriter<T> writer) {
		byte[] bytes = writer.transform(payload);
		setContentLength(bytes.length);
		this.content = bytes;
	}

	@Override
	public void render(String template, Map<String, Object> model) {
		ViewEngine engine = AppServer.engine();
		try {
			this.content = engine.merge(template, model).getBytes(StandardCharsets.UTF_8);
			setContentLength(this.content.length);
		} catch (Exception e) {
			this.content = e.getMessage().getBytes(StandardCharsets.UTF_8);
			setStatus(SC_NOT_ACCEPTABLE);
		}
	}

	@Override
	public void next(String path) {
		this.forward = true;
		this.routeUri = path;
	}

	@Override
	public void redirect(String path) {
		this.redirect = true;
		this.routeUri = path;
		setStatus(SC_MOVED_PERMANENTLY);
	}

	@Override
	public void type(String mimetype) {
		setContentType(mimetype);
	}

	@Override
	public void cookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		addCookie(cookie);
	}

	@Override
	public void attachment(String filename) {
		download(filename, filename, getContentType(), null);
	}

	@Override
	public void download(String path, String filename, String mimeType, HandlerStatus status) {
		// reads input file from an absolute path
		Path filePath = Paths.get(System.getProperty("user.dir"), path);
		File downloadFile = filePath.resolve(filename).toFile();

		// gets MIME type of the file
		if (mimeType == null) {
			// set to binary type if MIME mapping not provided
			mimeType = "application/octet-stream";
		}

		// modifies response
		setContentType(mimeType);
		setContentLength((int) downloadFile.length());

		// forces download
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
		setHeader(headerKey, headerValue);

		ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
		byte[] buffer = new byte[4096];
		int bytesRead;

		try (FileInputStream inStream = new FileInputStream(downloadFile)) {
			while ((bytesRead = inStream.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}
		} catch (IOException ex) {
			setStatus(SC_NOT_ACCEPTABLE);
			send(ex.getMessage());
			return;
		}
		this.content = baos.toByteArray();
		setContentLength(this.content.length);

		if (status != null) {
			status.send();
		}
	}

	@Override
	public byte[] getContent() {
		return this.content;
	}
}
