package com.practicaldime.router.http.proxy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class ProxyConfig {

    public String basedir;
    public String appctx;
    public Integer poolSize;
    public Long timeout;
    public Integer port;
    public String host;
    public JdbcSession session;
    public ProxyInfo proxy;
    public Balancer balancer;
    public List<Static> resources;

    @JsonCreator
    public ProxyConfig(@JsonProperty("basedir") String basedir,
                       @JsonProperty("appctx") String appctx,
                       @JsonProperty("poolSize") Integer poolSize,
                       @JsonProperty("timeout") Long timeout,
                       @JsonProperty("port") Integer port,
                       @JsonProperty("host") String host,
                       @JsonProperty("session") JdbcSession session,
                       @JsonProperty("proxy") ProxyInfo proxy,
                       @JsonProperty("balancer") Balancer balancer,
                       @JsonProperty("resources") List<Static> resources) {
        super();
        this.basedir = basedir;
        this.appctx = appctx;
        this.poolSize = poolSize;
        this.timeout = timeout;
        this.port = port;
        this.host = host;
        this.session = session;
        this.proxy = proxy;
        this.balancer = balancer;
        this.resources = resources;
    }

    public static ProxyConfig load(String baseDir, String configPath, ObjectMapper mapper) throws IOException {
        //load default values
        InputStream defaultInput = ProxyApp.class.getClassLoader().getResourceAsStream(configPath);
        ProxyConfig defaultConfig = mapper.readValue(defaultInput, ProxyConfig.class);

        //check for user-defined config location in system properties
        String configFile = System.getProperty("config.file");
        if (configFile != null && configFile.trim().length() > 0) {
            //check if the file is in the classpath with relative path
            InputStream configInput = ProxyApp.class.getClassLoader().getResourceAsStream(configFile);
            if (configInput == null) {
                //check if the file is in the classpath with absolute path
                configInput = ProxyApp.class.getResourceAsStream(configFile);
                if (configInput == null) {
                    //check if file is in location relative to the appdir
                    File theFile = new File(baseDir, configFile);
                    if (theFile.exists()) {
                        configInput = new FileInputStream(theFile);
                    } else {
                        //check if file location is on an absolute path
                        theFile = new File(configFile);
                        if (theFile.exists()) {
                            configInput = new FileInputStream(new File(configFile));
                        }
                    }
                    if (configInput == null) {
                        //check if file can be loaded via url
                        URL urlObject = new URL(configFile);
                        URLConnection urlConnection = urlObject.openConnection();
                        configInput = urlConnection.getInputStream();
                        if (configInput == null) {
                            throw new RuntimeException("could not locate a configuration file to load");
                        }
                    }
                }
            }
            ProxyConfig userConfig = mapper.readValue(configInput, ProxyConfig.class);
            //merge configurations
            defaultConfig.thisToThatOnNull(userConfig);
            return userConfig;
        }
        return defaultConfig;
    }

    public void thisToThatOnNull(ProxyConfig that) {
        if (that.basedir == null) {
            that.basedir = this.basedir;
        }
        if (that.appctx == null) {
            that.appctx = this.appctx;
        }
        if (that.poolSize == null) {
            that.poolSize = this.poolSize;
        }
        if (that.timeout == null) {
            that.timeout = this.timeout;
        }
        if (that.port == null) {
            that.port = this.port;
        }
        if (that.host == null) {
            that.host = this.host;
        }
        if (that.session == null) {
            that.session = this.session;
        }
        if (that.proxy == null) {
            that.proxy = this.proxy;
        }
        if (that.balancer == null) {
            that.balancer = this.balancer;
        }
        if (that.resources == null) {
            that.resources = this.resources;
        }
    }

    public void thatToThisOnNull(ProxyConfig that) {
        if (this.basedir == null) {
            this.basedir = that.basedir;
        }
        if (this.appctx == null) {
            this.appctx = that.appctx;
        }
        if (this.poolSize == null) {
            this.poolSize = that.poolSize;
        }
        if (this.timeout == null) {
            this.timeout = that.timeout;
        }
        if (this.port == null) {
            this.port = that.port;
        }
        if (this.host == null) {
            this.host = that.host;
        }
        if (this.session == null) {
            this.session = that.session;
        }
        if (this.proxy == null) {
            this.proxy = that.proxy;
        }
        if (this.balancer == null) {
            this.balancer = that.balancer;
        }
        if (this.resources == null) {
            this.resources = that.resources;
        }
    }

    public void thisOnNotNullToThat(ProxyConfig that) {
        if (this.basedir != null) {
            that.basedir = this.basedir;
        }
        if (this.appctx != null) {
            that.appctx = this.appctx;
        }
        if (this.poolSize != null) {
            that.poolSize = this.poolSize;
        }
        if (this.timeout != null) {
            that.timeout = this.timeout;
        }
        if (this.port != null) {
            that.port = this.port;
        }
        if (this.host != null) {
            that.host = this.host;
        }
        if (this.session != null) {
            that.session = this.session;
        }
        if (this.proxy != null) {
            that.proxy = this.proxy;
        }
        if (this.balancer != null) {
            that.balancer = this.balancer;
        }
        if (this.resources != null) {
            that.resources = this.resources;
        }
    }

    public void thatOnNotNullToThis(ProxyConfig that) {
        if (that.basedir != null) {
            this.basedir = that.basedir;
        }
        if (that.appctx != null) {
            this.appctx = that.appctx;
        }
        if (that.poolSize != null) {
            this.poolSize = that.poolSize;
        }
        if (that.timeout != null) {
            this.timeout = that.timeout;
        }
        if (that.port != null) {
            this.port = that.port;
        }
        if (that.host != null) {
            this.host = that.host;
        }
        if (that.session != null) {
            this.session = that.session;
        }
        if (that.proxy != null) {
            this.proxy = that.proxy;
        }
        if (that.balancer != null) {
            this.balancer = that.balancer;
        }
        if (that.resources != null) {
            this.resources = that.resources;
        }
    }

    public static class ProxyInfo {
        public final String pathspec;
        public final String prefix;
        public final String url;

        @JsonCreator
        public ProxyInfo(@JsonProperty("pathspec") String pathspec, @JsonProperty("prefix") String prefix, @JsonProperty("url") String url) {
            this.pathspec = pathspec;
            this.prefix = prefix;
            this.url = url;
        }
    }

    public static class Balancer {
        public final String pathspec;
        public final String prefix;
        public final List<Member> members;

        @JsonCreator
        public Balancer(@JsonProperty("pathspec") String pathspec, @JsonProperty("prefix") String prefix, @JsonProperty("members") List<Member> members) {
            this.pathspec = pathspec;
            this.prefix = prefix;
            this.members = members;
        }

        public static class Member {
            public final String name;
            public final String url;

            @JsonCreator
            public Member(@JsonProperty("name") String name, @JsonProperty("url") String url) {
                this.name = name;
                this.url = url;
            }
        }
    }

    public static class Static {
        public final String assets;
        public final String context;
        public final String pathspec;

        @JsonCreator
        public Static(@JsonProperty("assets") String assets, @JsonProperty("context") String context, @JsonProperty("pathspec") String pathspec) {
            this.assets = assets;
            this.context = context;
            this.pathspec = pathspec;
        }
    }

    public static class JdbcSession {
        public final String url;
        public final String driver;

        @JsonCreator
        public JdbcSession(@JsonProperty("url") String url, @JsonProperty("driver") String driver) {
            this.url = url;
            this.driver = driver;
        }
    }
}
