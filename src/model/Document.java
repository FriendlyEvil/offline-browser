package model;

import java.util.ArrayList;
import java.util.List;

public class Document {
    private final String pageUrl;
    private final List<String> pages = new ArrayList<>();
    private final List<String> files = new ArrayList<>();

    public Document(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public List<String> getPages() {
        return pages;
    }

    public void addPage(String url) {
        pages.add(url);
    }

    public List<String> getFiles() {
        return files;
    }

    public void addFile(String url) {
        files.add(url);
    }

    public String getPageUrl() {
        return pageUrl;
    }
}
