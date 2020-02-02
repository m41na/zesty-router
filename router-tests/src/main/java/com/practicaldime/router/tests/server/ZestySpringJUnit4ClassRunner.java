package com.practicaldime.router.tests.server;

import com.practicaldime.router.core.server.IServer;
import org.eclipse.jetty.client.HttpClient;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ZestySpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private CountDownLatch serverLatch;

    public ZestySpringJUnit4ClassRunner(Class testClass) throws InitializationError {
        super(testClass);
        //look for method annotated with @ZestyProvider
        List<FrameworkMethod> provider = getTestClass().getAnnotatedMethods(ZestyProvider.class);
        if (provider == null) {
            throw new RuntimeException("Could not find a provider method for the server");
        }
        if (provider.size() > 1) {
            throw new RuntimeException("Expected only one a provider method for the server");
        }
        //start server in its own thread
        CompletableFuture.runAsync(() -> {
            try {
                FrameworkMethod method = provider.get(0);
                IServer server = (IServer) method.invokeExplosively(createTest());
                serverLatch.await();
                server.shutdown();
                executor.shutdown();
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }, executor).handle((res, th) -> {
            if (th != null) {
                System.err.println(th.getMessage());
            }
            return res;
        });
    }

    protected HttpClient startClient() {
        HttpClient httpClient = new HttpClient();
        // Configure basic HttpClient
        httpClient.setFollowRedirects(false);
        try {
            // Start HttpClient
            httpClient.start();
        } catch (Exception e) {
            System.err.println("Could not start client successfully");
            return null;
        }

        return httpClient;
    }

    @Override
    protected List<FrameworkMethod> getChildren() {
        List<FrameworkMethod> children = super.getChildren();
        serverLatch = new CountDownLatch(children.size());
        return children;
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        System.out.println("invoking: " + method.getName());
        serverLatch.countDown();
        if (method.getMethod().getParameterCount() == 1) {
            return new ZestyInvokeMethod(method, test, startClient());
        } else {
            return super.methodInvoker(method, test);
        }
    }

    @Override
    protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation, boolean isStatic, List<Throwable> errors) {
        List<FrameworkMethod> methods = getTestClass().getAnnotatedMethods(annotation);
        for (FrameworkMethod eachTestMethod : methods) {
            System.out.println("skipping args check for valid test method");
            eachTestMethod.validatePublicVoid(isStatic, errors);
        }
    }

    public static class ZestyInvokeMethod extends Statement {

        private final FrameworkMethod testMethod;
        private final Object target;
        private final HttpClient client;

        public ZestyInvokeMethod(FrameworkMethod testMethod, Object target, HttpClient client) {
            this.testMethod = testMethod;
            this.target = target;
            this.client = client;
        }

        @Override
        public void evaluate() throws Throwable {
            if (client != null) {
                testMethod.invokeExplosively(target, client);
            } else {
                throw new RuntimeException("Could not start client successfully");
            }
        }
    }
}
