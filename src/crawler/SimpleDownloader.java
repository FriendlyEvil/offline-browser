package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class SimpleDownloader implements Downloader {

    @Override
    public InputStream download(String url) throws IOException {
        URL obj = new URL(url);
        URLConnection conn = obj.openConnection();
        return conn.getInputStream();
    }

    @Override
    public Reader downloadUTF8(String url) throws IOException {
        URL obj = new URL(url);
        URLConnection conn = obj.openConnection();
        return new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8);
    }

}
