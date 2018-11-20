package com.rest.test;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Hello world!
 */
public class App {
    private static final int httpPort = 8080;
    private static final int httpsPort = 8083;
    private static final String keyStorePath = "/Users/mukthara/Data/git/personal/ssl-https/muks-ssl-site.jks";
    private static final String keyStorePassword = "Mahmed!234";

    public static void main(String[] args) {
        //httpServer();
//        httpOverSSLServer();
        http();

    }

    private static void http() {
        final Server server = new Server();
        final HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme("https");
        httpConfiguration.setSecurePort(httpsPort);

        final ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
        http.setPort(httpPort);
        server.addConnector(http);


        final SslContextFactory sslContextFactory = new SslContextFactory(keyStorePath);
        sslContextFactory.setKeyStorePassword(keyStorePassword);

        final HttpConfiguration httpsConfiguration = new HttpConfiguration(httpConfiguration);
        httpsConfiguration.addCustomizer(new SecureRequestCustomizer());
        final ServerConnector httpsConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(httpsConfiguration));
        httpsConnector.setPort(httpsPort);
        server.addConnector(httpsConnector);



        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/*");
        server.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.classnames", EntryPoint.class.getCanonicalName());


        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.destroy();
        }
    }

    private static void httpServer() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(
                org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);

        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter(
                "jersey.config.server.provider.classnames",
                EntryPoint.class.getCanonicalName());


        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jettyServer.destroy();
        }
    }

//    private static void httpOverSSLServer() {
//        Server server = new Server();
//        ServerConnector connector = new ServerConnector(server);
//        connector.setPort(9999);
//        HttpConfiguration https = new HttpConfiguration();
//        https.addCustomizer(new SecureRequestCustomizer());
//        SslContextFactory sslContextFactory = new SslContextFactory();
//
//        sslContextFactory.setKeyStorePath(EmbeddedServer.class.getResource("/keystore.jks").toExternalForm());
//
//        sslContextFactory.setKeyStorePassword("123456");
//        sslContextFactory.setKeyManagerPassword("123456");
//
//        ServerConnector sslConnector = new ServerConnector(server,
//                new SslConnectionFactory(sslContextFactory, "http/1.1"),
//                new HttpConnectionFactory(https));
//
//        sslConnector.setPort(9998);
//        server.setConnectors(new Connector[] { connector, sslConnector });
//    }



}
