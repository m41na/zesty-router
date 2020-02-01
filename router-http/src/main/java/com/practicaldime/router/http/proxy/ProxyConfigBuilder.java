package com.practicaldime.router.http.proxy;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ProxyConfigBuilder {

    private String basedir;
    private String appctx;
    private Integer poolSize;
    private Long timeout;
    private Integer port;
    private String host;
    private ProxyConfig.JdbcSession jdbcSession;
    private ProxyConfig.ProxyInfo proxyInfo;
    private ProxyConfig.Balancer balancer;
    private List<ProxyConfig.Static> resources;

    private ProxyConfigBuilder() {
    }

    public static ProxyConfigBuilder newProxyConfig() {
        return new ProxyConfigBuilder();
    }

    public ProxyConfigBuilder basedir(String basedir) {
        if (basedir != null) {
            this.basedir = basedir;
        }
        return this;
    }

    public ProxyConfigBuilder appctx(String appctx) {
        if (appctx != null) {
            this.basedir = appctx;
        }
        return this;
    }

    public ProxyConfigBuilder poolSize(Integer poolSize) {
        if (poolSize != null) {
            this.poolSize = poolSize;
        }
        return this;
    }

    public ProxyConfigBuilder timeout(Long timeout) {
        if (timeout != null) {
            this.timeout = timeout;
        }
        return this;
    }

    public ProxyConfigBuilder port(Integer port) {
        if (port != null) {
            this.port = port;
        }
        return this;
    }

    public ProxyConfigBuilder host(String host) {
        if (host != null) {
            this.host = host;
        }
        return this;
    }

    public ProxyConfigBuilder jdbcSession(Function<JdbcSessionBuilder, ProxyConfig.JdbcSession> builder) {
        this.jdbcSession = builder.apply(JdbcSessionBuilder.newJdbcSession());
        return this;
    }

    public ProxyConfigBuilder proxyInfo(Function<ProxyInfoBuilder, ProxyConfig.ProxyInfo> builder) {
        this.proxyInfo = builder.apply(ProxyInfoBuilder.newProxyInfo());
        return this;
    }

    public ProxyConfigBuilder balancer(Function<BalancerBuilder, ProxyConfig.Balancer> builder) {
        this.balancer = builder.apply(BalancerBuilder.newBalancer());
        return this;
    }

    public ProxyConfigBuilder resources(Function<StaticBuilder, List<ProxyConfig.Static>> builder) {
        this.resources = builder.apply(StaticBuilder.newStatic());
        return this;
    }

    public ProxyConfig build() {
        return new ProxyConfig(basedir, appctx, poolSize, timeout, port, host, jdbcSession, proxyInfo, balancer, resources);
    }

    public static class ProxyInfoBuilder {

        public String pathspec;
        public String prefix;
        public String url;

        private ProxyInfoBuilder() {
        }

        public static ProxyInfoBuilder newProxyInfo() {
            return new ProxyInfoBuilder();
        }

        public ProxyInfoBuilder pathspec(String pathspec) {
            if (pathspec != null) {
                this.pathspec = pathspec;
            }
            return this;
        }

        public ProxyInfoBuilder prefix(String prefix) {
            if (prefix != null) {
                this.prefix = prefix;
            }
            return this;
        }

        public ProxyInfoBuilder url(String url) {
            if (url != null) {
                this.url = url;
            }
            return this;
        }

        public ProxyConfig.ProxyInfo build() {
            return new ProxyConfig.ProxyInfo(pathspec, prefix, url);
        }
    }

    public static class BalancerBuilder {

        public String pathspec;
        public String prefix;
        public List<ProxyConfig.Balancer.Member> members = new LinkedList<>();

        private BalancerBuilder() {
        }

        public static BalancerBuilder newBalancer() {
            return new BalancerBuilder();
        }

        public BalancerBuilder pathspec(String pathspec) {
            if (pathspec != null) {
                this.pathspec = pathspec;
            }
            return this;
        }

        public BalancerBuilder prefix(String prefix) {
            if (prefix != null) {
                this.prefix = prefix;
            }
            return this;
        }

        public BalancerBuilder members(Function<MemberBuilder, List<ProxyConfig.Balancer.Member>> members) {
            if (members != null) {
                this.members = members.apply(new MemberBuilder());
            }
            return this;
        }

        public ProxyConfig.Balancer build() {
            return new ProxyConfig.Balancer(pathspec, prefix, members);
        }
    }

    public static class MemberBuilder {

        public String name;
        public String url;

        private MemberBuilder() {
        }

        public static MemberBuilder newMember() {
            return new MemberBuilder();
        }

        public MemberBuilder url(String url) {
            if (url != null) {
                this.url = url;
            }
            return this;
        }

        public MemberBuilder name(String name) {
            if (name != null) {
                this.name = name;
            }
            return this;
        }

        public ProxyConfig.Balancer.Member build() {
            return new ProxyConfig.Balancer.Member(name, url);
        }
    }

    public static class StaticBuilder {

        public String assets;
        public String context;
        public String pathspec;

        private StaticBuilder() {
        }

        public static StaticBuilder newStatic() {
            return new StaticBuilder();
        }

        public StaticBuilder assets(String assets) {
            if (assets != null) {
                this.assets = assets;
            }
            return this;
        }

        public StaticBuilder context(String context) {
            if (context != null) {
                this.context = context;
            }
            return this;
        }

        public StaticBuilder pathspec(String pathspec) {
            if (pathspec != null) {
                this.pathspec = pathspec;
            }
            return this;
        }

        public ProxyConfig.Static build() {
            return new ProxyConfig.Static(assets, context, pathspec);
        }
    }

    public static class JdbcSessionBuilder {

        public String url;
        public String driver;

        private JdbcSessionBuilder() {
        }

        public static JdbcSessionBuilder newJdbcSession() {
            return new JdbcSessionBuilder();
        }

        public JdbcSessionBuilder url(String url) {
            if (url != null) {
                this.url = url;
            }
            return this;
        }

        public JdbcSessionBuilder driver(String driver) {
            if (driver != null) {
                this.driver = driver;
            }
            return this;
        }

        public ProxyConfig.JdbcSession build() {
            return new ProxyConfig.JdbcSession(url, driver);
        }
    }
}
