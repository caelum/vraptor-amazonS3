package br.com.caelum.vraptor.amazonS3;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import br.com.caelum.vraptor.environment.Environment;

public class LocalFileStorage implements FileStorage {

    private static final String SERVER_URL = "br.com.caelum.vraptor.amazonS3.server.url";
    private static final String LOCAL_DIR = "br.com.caelum.vraptor.amazonS3.localstorage.dir";
    
    private final Environment env;
    private final File localStorageDir;
    private String localDir;
    private String serverRoot;
	private ServletContext context;

    public LocalFileStorage(Environment env, ServletContext context) {
        this.env = env;
		this.context = context;
        
        serverRoot = getOrElse(SERVER_URL, "http://localhost:8080");
        localDir = getOrElse(LOCAL_DIR, "files/");
        String filesPath = context.getRealPath(localDir);
        localStorageDir = new File(filesPath);
        if (!localStorageDir.exists()) {
            throw new IllegalStateException("could not find " + localStorageDir
                    + " dir, please set " + LOCAL_DIR + " properties properly.");
        }
    }

    @Override
    public URL store(File file, String bucket, String key) {
        File bucketDir = new File(localStorageDir, bucket);
        bucketDir.mkdirs();
        File dest = new File(bucketDir, key);
        copy(file, dest);
        
        return urlFor(bucket, key);
    }

    @Override
    public URL store(InputStream is, String bucket, String key, String contentType) {
        File bucketDir = new File(localStorageDir, bucket);
        bucketDir.mkdirs();
        File dest = new File(bucketDir, key);
        copy(is, dest);
        
        return urlFor(bucket, key);
    }

    private void copy(InputStream is, File dest) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(dest);
            IOUtils.copy(is, fileOutputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
    }

    @Override
    public void newBucket(String name) {
        File bucketDir = new File(localStorageDir, name);
        bucketDir.mkdirs();
    }

    @Override
    public URL urlFor(String bucket, String key) {
        
        localDir = putSlash(localDir);
        return url(serverRoot + context.getContextPath() + "/" + localDir + bucket + "/" + key);
    }

    private String putSlash(String dir) {
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        return dir;
    }

    private void copy(File file, File dest) {
        try {
            FileUtils.copyFile(file, dest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL url(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getOrElse(String key, String defaultValue) {
        try {
            return env.get(key);
        } catch (NoSuchElementException e) {
            return defaultValue;
        }
    }
}
