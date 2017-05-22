package foo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.saxonica.config.ProfessionalConfiguration;
import net.sf.saxon.Configuration;
import net.sf.saxon.lib.FeatureKeys;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.Xslt30Transformer;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import foo.ctx.Handler;

public class SaxonMultithreadTest {

    public static final String JAVA_PROTOCOL_HANDLER_PKGS = "java.protocol.handler.pkgs";
    
    private String origProp;

    @Before
    public void setSystemProp() {
        origProp = System.getProperty(JAVA_PROTOCOL_HANDLER_PKGS);
        
        System.setProperty(JAVA_PROTOCOL_HANDLER_PKGS, getClass().getPackage().getName());

        //SET THE LOCAL THREAD CONTEXT, WHICH LATER WILL BE REFERENCED BY when "ctx://" URL is used in
        // stylesheet

        Handler.ctx.set(getResource("data.xml"));
    }
    
    @After
    public void restoreSystemProp() {
        Handler.ctx.remove();

        if (origProp != null)
            System.setProperty(JAVA_PROTOCOL_HANDLER_PKGS, origProp);
        else
            System.clearProperty(JAVA_PROTOCOL_HANDLER_PKGS);
    }
    
    @Test
    public void testSourceDocumentInMap() throws Exception {
        Configuration conf  = Configuration.newConfiguration();
        conf.setBooleanProperty(FeatureKeys.ALLOW_MULTITHREADING, false);

        assertTrue("Enterprise Xslt feature expected", conf.isLicensedFeature(Configuration.LicenseFeature.ENTERPRISE_XSLT));

        assertNotNull(Handler.ctx.get());
        
        Processor proc = new Processor(conf);

        XsltCompiler comp = proc.newXsltCompiler();

        Source xslt = getSource("threading-map.xsl");
        
        XsltExecutable exec = comp.compile(xslt);

        Xslt30Transformer transformer = exec.load30();

        Serializer destination = transformer.newSerializer();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        destination.setOutputStream(baos);
        
        
        transformer.applyTemplates(getSource("input.xml"), destination);

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<foo>\n" +
                "   <data>bar3</data>\n" +
                "   <data>bar2</data>\n" +
                "</foo>\n", baos.toString());
    }

    private Source getSource(String resourceName) {
        return new StreamSource(getResource(resourceName).toExternalForm());
    }

    private URL getResource(String resourceName) {
        return getClass().getResource(resourceName);
    }
}
