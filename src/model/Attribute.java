package model;

import java.io.IOException;
import java.io.Writer;

public class Attribute {
    private final String name;
    private String value;
    private static final String TEMPLATE = "%s=\"%s\" ";

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String newValue) {
        value = newValue;
    }

    public void write(Writer writer) throws IOException {
        writer.write(String.format(TEMPLATE, name, value));
    }
}
