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
    
    private final Environment env;
    private final File localStorageDir;
    private final String serverRoot;
	private final ServletContext context;

    public LocalFileStorage(Environment env, ServletContext context) {
        this.env = env;
		this.context = context;
        
        serverRoot = getOrElse(SERVER_URL, "http://localhost:8080");
        localStorageDir = new File(context.getRealPath("/"));
    }

    @Override
    public URL store(File file, String path) {
        File dest = new File(localStorageDir, path);
        dest.getParentFile().mkdirs();
        copy(file, dest);
        
        return urlFor(null, path);
    }

    @Override
    public URL store(InputStream is, String path, String contentType) {
    	File dest = new File(localStorageDir, path);
    	dest.getParentFile().mkdirs();
        copy(is, dest);
        
        return urlFor(null, path);
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
    public URL urlFor(String bucket, String path) {
        return url(putSlash(serverRoot) + putSlash(context.getContextPath()) + path);
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
