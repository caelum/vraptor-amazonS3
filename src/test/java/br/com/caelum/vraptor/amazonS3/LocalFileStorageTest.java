package br.com.caelum.vraptor.amazonS3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.environment.DefaultEnvironment;

public class LocalFileStorageTest {

    private DefaultEnvironment env;
    private LocalFileStorage localFileStorage;
    private File file;
    private File bucketDir;

    @Before
    public void setup() throws IOException {
        env = new DefaultEnvironment("testing");
        localFileStorage = new LocalFileStorage(env);
        URL resource = env.getResource("/sample.txt");
        file = new File(resource.getFile());
        bucketDir = new File("src/main/webapp/files/subdir/");
    }

    @Test
    public void should_store_locally() throws Exception {
        URL url = localFileStorage.store(file, "subdir", "sample.txt");

        File storedFile = new File(bucketDir, "sample.txt");

        assertTrue(storedFile.exists());
        assertTrue(bucketDir.exists());
        assertEquals(new URL("http://localhost:8080/files/subdir/sample.txt"), url);

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
