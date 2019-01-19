package xyz.monhemius.wedding;

import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.http.util.Header;
import org.glassfish.grizzly.http.util.HttpStatus;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Static HTTP handler which simply sets the cache-control header for static resources.
 * The scope of the cache is set to private.
 */
public class CachedHttpHandler extends StaticHttpHandler {

    private static final Logger logger = Logger.getLogger(CachedHttpHandler.class.getName());

    private final int cacheMaxAge;
    private static final String CODE = "lWg1K1bM5n";

    /**
     * Create a new instance of the Cached static http handlers.
     * @param cacheMaxAge The max age to set for cache control. E.g. max-age=${cacheMaxAge}
     * @param docRoots The path of the document root to serve
     */
    public CachedHttpHandler(int cacheMaxAge, String... docRoots) {
        super(docRoots);
        this.cacheMaxAge = cacheMaxAge;
    }

    public CachedHttpHandler(int cacheMaxAge, Set<String> docRoots) {
        super(docRoots);
        this.cacheMaxAge = cacheMaxAge;
    }

    @Override
    protected boolean handle(String uri, Request request, Response response) throws Exception {
        logger.log(Level.INFO, uri);
        if (uri.contains("rsvp") && !request.getParameter("code").equals(CODE)) {
            response.setStatus(HttpStatus.FORBIDDEN_403);
            return false;
        }
        return super.handle(uri, request, response);
    }
}
