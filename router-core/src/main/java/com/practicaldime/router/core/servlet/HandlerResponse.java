package com.practicaldime.router.core.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practicaldime.router.core.handler.BodyWriter;
import com.practicaldime.router.core.handler.HandlerStatus;
import com.practicaldime.router.core.handler.RouteResponse;
import com.practicaldime.router.core.view.ViewEngine;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class HandlerResponse extends HttpServletResponseWrapper implements RouteResponse {

    private final Logger LOG = LoggerFactory.getLogger(HandlerResponse.class);
    protected byte[] content = new byte[0];
    protected boolean redirect = false;
    protected boolean forward = false;
    protected String routeUri;
    protected String contextPath;
    protected String templateDir;
    protected ObjectMapper mapper;
    protected ViewEngine engine;

    public HandlerResponse(HttpServletResponse response) {
        super(response);
        init();
    }

    private void init() {
        setContentType("text/html;charset=utf-8");
        this.mapper = ObjectMapperSupplier.version1.get();
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
    public void engine(ViewEngine engine) {
        this.engine = engine;
    }

    @Override
    public void context(String ctx) {
        this.contextPath = ctx;
    }

    @Override
    public void status(int status) {
        setStatus(status);
    }

    public void ok(Object payload) {
        status(200);
        json(payload);
    }

    public void accepted() {
        sendStatus(201);
    }

    @Override
    public void sendStatus(int status) {
        status(status);
        send(HttpStatus.getMessage(status));
    }

    @Override
    public void send(String payload) {
        this.content = payload.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void bytes(byte[] payload) {
        this.content = payload;
    }

    @Override
    public void json(Object payload) {
        try {
            setContentType("application/json");
            this.content = mapper.writeValueAsBytes(payload);
        } catch (Exception e) {
            throw new RuntimeException("Could not write json value from java entity", e);
        }
    }

    @Override
    public void jsonp(Object payload) {
        try {
            setContentType("application/json");
            this.content = mapper.writeValueAsBytes(payload);
        } catch (Exception e) {
            throw new RuntimeException("Could not write json value from java entity", e);
        }
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
            LOG.error("Could not transform content dest response body");
        }
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
        try {
            this.content = this.engine.merge(template, model).getBytes(StandardCharsets.UTF_8);
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
        setStatus(SC_SEE_OTHER);
    }

    @Override
    public void redirect(int status, String path) {
        this.redirect = true;
        this.routeUri = path;
        setStatus(status);
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
            // set dest binary type if MIME context not provided
            mimeType = "application/octet-stream";
        }

        // modifies response
        setContentType(mimeType);

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

        if (status != null) {
            status.send();
        }
    }

    @Override
    public byte[] getContent() {
        return this.content;
    }

    @Override
    public String readContent(String folder, String file) {
        Path path = Paths.get(folder, file);
        try {
            StringWriter sw = new StringWriter();
            Files.lines(path).forEach(sw::write);
            return sw.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Reader getReader(String folder, String file) {
        Path path = Paths.get(folder, file);
        try {
            return new FileReader(path.toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
