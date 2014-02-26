package br.com.caelum.vraptor.amazonS3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.caelum.vraptor.environment.DefaultEnvironment;
import br.com.caelum.vraptor.environment.EnvironmentType;

public class LocalFileStorageTest {

    private DefaultEnvironment env;
    private LocalFileStorage localFileStorage;
    private File file;
    private File bucketDir;

    @Before
    public void setup() throws IOException {
        env = new DefaultEnvironment(EnvironmentType.TEST);
        ServletContext ctx = mock(ServletContext.class);
        when(ctx.getRealPath(Mockito.anyString())).thenReturn("src/main/webapp/files/");
        when(ctx.getContextPath()).thenReturn("");
		localFileStorage = new LocalFileStorage(env, ctx);
        URL resource = env.getResource("/sample.txt");
        file = new File(resource.getFile());
        bucketDir = new File("src/main/webapp/files/subdir/");
    }

    @Test
    public void should_store_locally() throws Exception {
        URL url = localFileStorage.store(file, "subdir", "dir/sample.txt");

        File storedFile = new File(bucketDir, "dir/sample.txt");

        assertTrue(storedFile.exists());
        assertTrue(bucketDir.exists());
        assertEquals(new URL("http://localhost:8080/files/subdir/dir/sample.txt"), url);

        storedFile.delete();
        bucketDir.delete();
    }
    
    @Test
    public void should_store_as_input_stream() throws Exception {
        URL url = localFileStorage.store(new FileInputStream(file), "subdir", "sample.txt", "text/plain");
        
        File storedFile = new File(bucketDir, "sample.txt");
        
        assertTrue(storedFile.exists());
        assertTrue(bucketDir.exists());
        assertEquals(new URL("http://localhost:8080/files/subdir/sample.txt"), url);
        
        storedFile.delete();
        bucketDir.delete();
    }

}
