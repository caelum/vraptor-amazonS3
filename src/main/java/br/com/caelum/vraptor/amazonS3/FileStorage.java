package br.com.caelum.vraptor.amazonS3;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

public interface FileStorage {

    public URL store(File file, String dir, String key);

    public URL store(InputStream is, String dir, String key, String contentType);

    public void newBucket(String dir);

}
