AppServer Object
=====================

The is one of the core components in the zesty framework. It holds references to objects that play a central role in how everthing else is held together.::

    public AppServer() {
        this(new HashMap<>());
    }

    public AppServer(Map<String, String> props) {
        this.assets(Optional.ofNullable(props.get("assets")).orElse("www"));
        this.appctx(Optional.ofNullable(props.get("appctx")).orElse("/"));
        this.engine(Optional.ofNullable(props.get("engine")).orElse("jtwig"));
        this.threadPoolExecutor = createThreadPoolExecutor();
    }

locals: Properties
^^^^^^^^^^^^^^^^^^^

This holds configuration attributes that can be passed to the application and that are used by other components internally to configure their own behavior.

threadPoolExecutor: ThreadPoolExecutor
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This is a thread-pool that is separate from the server's own thread pool. This is only used to handle requests for handlers configured as '*supports async*'.
This thread pool is configurable using 3 parameters which are passed through the **locals** objects. These properties are:

    * *poolSize* - the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
    * *maxPoolSize* - the maximum number of threads to allow in the pool
    * *keepAliveTime* - when the number of threads is greater than the core, this is the maximum time (in MILLISECONDS) that excess idle threads will wait for new tasks before terminating.

wpcontext: Map
^^^^^^^^^^^^^^^

If configuring the application for :code:`wordpress`, this will hold the configuration parameters required.::

    // ************* WORDPRESS *****************//
    public AppServer wordpress(String home, String fcgi_proxy) {
        this.wpcontext.put("activate", "true");
        this.wpcontext.put("resource_base", home);
        this.wpcontext.put("welcome_file", "index.php");
        this.wpcontext.put("fcgi_proxy", fcgi_proxy);
        this.wpcontext.put("script_root", home);
        return this;
    }

corsconetxt: Map
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This map holds the request headers' attributes that the application will use to configure the :code:`CorsFilter`.

servlets: ServletContextHandler
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

This is the servlet handlers' container for the application. All configured routes are added to this handler under one context path.

engine: ViewEngine
^^^^^^^^^^^^^^^^^^^

This is an interface for exposing the active **view** component to the application. The value it holds specifies the concrete view 
implementation to be used by the application. This could be either be *freemarker*, *jtwig* at the moment.

AppProvider Object
===================

This is a provider for the **AppServer** object which is comes in handy when working with Javascript.::

    public class AppProvider {

        public static AppServer provide(Map<String, String> props) {
            return new AppServer(props);
        }
    }

You can alternatively just simply instantiate the **AppServer** manually.

HandlerConfig Object
=====================

This interface allows you to dynamically customize the request handler (the underlying object is a :code:`HttpServletRequest`) with the help of 
:code:`ServletRegistration.Dynamic` to suit specific requirements.::

    public interface HandlerConfig {

        public void configure(ServletHolder holder);
    }

Through the :code:`holder.getRegistration()` object, you will have access to servlet-specific methods like:

    * setLoadOnStartup(int loadOnStartup)
    * setMultipartConfig(MultipartConfigElement multipartConfig)
    * Set<String> addMapping(String... urlPatterns)
    * setAsyncSupported(boolean supported)

among others.

BodyWriter Object
==================

This interface allows you to take over the response generation responsibility and the output is sent back to the client as the response body. ::

    public interface BodyWriter<T> {
        
        byte[] transform(T object);
    }

BodyReader Object
==================

This interface allows you to take over the request body processing responsibility and the output is used by the application to process the request. ::
    
    public interface BodyReader<T> {
        
        T transform(String type, byte[] bytes);
    }

Configure Logging
==================

To configure logging, let's revisit how logback initialize itself.

    * Logback tries to find a file called logback-test.xml in the classpath.
    * If no such file is found, logback tries to find a file called logback.groovy in the classpath.
    * If no such file is found, it checks for the file logback.xml in the classpath..
    * If no such file is found, service-provider loading facility (introduced in JDK 1.6) is used to resolve the implementation of com.qos.logback.classic.spi.Configurator interface by looking up the file META-INF\services\ch.qos.logback.classic.spi.Configurator in the class path. Its contents should specify the fully qualified class name of the desired Configurator implementation.
    * If none of the above succeeds, logback configures itself automatically using the BasicConfigurator which will cause logging output to be directed to the console.

For the javascript application, we need another way to override this process in order to control logging. Fortunately, logback will allow us to configure a system property path which will preempt
the initialization process above. :code:`java -Dlogback.configurationFile=/path/to/config.xml`. To configure this for javascript, use the syntax below::

    jjs --language=es6 -ot -scripting -J-Dlogback.configurationFile=../lib/app-logback.xml -J-Djava.class.path=../lib/zesty-router-0.1.0-shaded.jar index.js

The corresponding :code:`app-logback.xml` file would look something like this.::

    <?xml version="1.0" encoding="UTF-8"?>
    <configuration debug="true">

        <appender name="STDOUT"
            class="ch.qos.logback.core.ConsoleAppender">
            <encoder
                class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
                <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
                </Pattern>
            </encoder>
        </appender>

        <logger name="com.practicaldime.zesty" level="DEBUG" />
        <logger name="org.eclipse.jetty" level="ERROR" />

        <root level="debug">
            <appender-ref ref="STDOUT" />
        </root>
    </configuration>

With this setup, you are back in control over the logging process through the configuration file.