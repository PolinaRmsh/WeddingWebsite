package xyz.monhemius.wedding;

import org.glassfish.grizzly.http.CompressionConfig;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    private static final Set<String> compressionMimeTypes = new HashSet<>(Arrays.asList("application/atom+xml",
        "application/javascript",
        "application/json",
        "application/manifest+json",
        "application/x-font-ttf",
        "application/x-web-app-manifest+json",
        "application/xhtml+xml",
        "application/xml",
        "image/bmp",
        "image/svg+xml",
        "image/x-icon",
        "text/cache-manifest",
        "text/css",
        "text/javascript",
        "text/plain",
        "text/x-component",
        "text/x-cross-domain-policy"));

    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://localhost:8082"), false);
        server.getServerConfiguration().setDefaultErrorPageGenerator((
            (Request request, int status, String reasonPhrase, String description, Throwable exception) -> {
                //Todo: create 404 page
                return "404: file not found";
            }));

        server.getListener("grizzly").getFileCache().setEnabled(true);
        CompressionConfig compressionConfig = server.getListener("grizzly").getCompressionConfig();
        compressionConfig.setCompressionMode(CompressionConfig.CompressionMode.ON);
        compressionConfig.setCompressionMinSize(1000);
        compressionConfig.setCompressibleMimeTypes(compressionMimeTypes);
        server.getServerConfiguration().addHttpHandler(
            new CachedHttpHandler(86400, "/var/www/xyz.monhemius.wedding"));

        try {
            // Start services
            // Wait for this thread to finish or crash to properly shut down the thread pool, otherwise netty leaves
            // orphans running.
            executorService.submit(() -> {
                try {
                    server.start();
                    System.in.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).get();
            executorService.shutdownNow();
        } catch (InterruptedException | ExecutionException e) {
            logger.log(Level.SEVERE, "One of the processes had a fatal error", e);
        } finally {
            executorService.shutdownNow();
        }
    }
}
