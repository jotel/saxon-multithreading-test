package foo.ctx;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
    public static ThreadLocal<URL> ctx = new ThreadLocal<>();
    
    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        URL url = ctx.get();
        
        assert url != null;
                
        return url.openConnection();
        // to verify that the stylesheet is working uncomment the line below
//        return new File("./src/test/resources/foo/data.xml").toURI().toURL().openConnection();
    }
}
