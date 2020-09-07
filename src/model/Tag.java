package model;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class Tag {
    private final String name;
    private final List<Attribute> attributes;
    private final List<Object> childTags;
    private boolean isSingle = false;

    public Tag(String name) {
        this.name = name;
        this.attributes = new ArrayList<>();
        childTags = new ArrayList<>();
    }

    public void addValue(String value) {
        childTags.add(value);
    }

    public void addValue(Tag value) {
        childTags.add(value);
    }

    public String getName() {
        return name;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public List<Tag> getChildTags() {
        List<Tag> result = new ArrayList<>();
        for (Object tag : childTags) {
            if (tag instanceof Tag) {
                result.add((Tag) tag);
            }
        }
        return result;
    }

    public void setSingle() {
        isSingle = true;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void write(Writer writer) throws IOException {
        writer.write('<');
        writer.write(name);
        writer.write(' ');
        for (Attribute attribute : attributes) {
            attribute.write(writer);
        }
        if (isSingle) {
            writer.write("/>");
        } else {
            writer.write('>');
            for (Object childTag : childTags) {
                if (childTag instanceof String) {
                    writer.write((String) childTag);
                } else if (childTag instanceof Tag) {
                    ((Tag) childTag).write(writer);
                }
            }
            writer.write("</");
            writer.write(name);
            writer.write(">");
        }
    }
}
