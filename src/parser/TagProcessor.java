package parser;

import manager.FileManager;
import model.Attribute;
import model.Document;
import model.Tag;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Function;

import static utils.HTMLUtils.escapeSymbols;
import static utils.HTMLUtils.mergeUrl;

public class TagProcessor {
    private final FileManager fileManager;
    private final Document document;
    private final Tag page;

    public TagProcessor(String url, Tag page, FileManager fileManager) {
        this.document = new Document(url);
        this.page = page;
        this.fileManager = fileManager;
    }

    public Document processAndSave(Writer writer) {
        processTag(page);
        saveDocument(page, writer);
        return document;
    }

    private void saveDocument(Tag tag, Writer writer) {
        if (tag.getName().equals(HTMLParser.ROOT_TAG_NAME)) {
            tag.getChildTags().forEach(tmp -> saveDocument(tmp, writer));
        } else {
            try {
                tag.write(writer);
            } catch (IOException e) {
                System.err.println("Error while saving document");
            }
        }
    }

    private void processTag(Tag tag) {
        switch (tag.getName()) {
            case HTMLParser.ROOT_TAG_NAME:
                break;
            case "a":
                processAttribute("href", tag, this::addPage);
                break;
            case "link":
                processAttribute("href", tag, this::addFile);
                break;
            case "img":
            case "script":
                processAttribute("src", tag, this::addFile);
                break;
        }
        for (Tag childTag : tag.getChildTags()) {
            processTag(childTag);
        }
    }

    private void processAttribute(String attributeName, Tag tag, Function<String, String> addFunction) {
        for (Attribute attribute: tag.getAttributes()) {
            if (attributeName.equals(attribute.getName())) {
                String filename = processFile(attribute.getValue(), addFunction);
                attribute.setValue(filename);
            }
        }
    }

    private String processFile(String value, Function<String, String> addFunction) {
        String url = mergeUrl(document.getPageUrl(), value);
        if (url != null) {
            return addFunction.apply(url);
        }
        return value;
    }

    private String addPage(String url) {
        document.addPage(escapeSymbols(url));
        return fileManager.addHTML(url);
    }

    private String addFile(String url) {
        document.addFile(url);
        return fileManager.addFile(url);
    }
}
