package model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Result {
    private final List<String> downloaded;
    private final Map<String, IOException> errors;

    public Result(final List<String> downloaded, final Map<String, IOException> errors) {
        this.downloaded = List.copyOf(downloaded);
        this.errors = Map.copyOf(errors);
    }

    public List<String> getDownloaded() {
        return downloaded;
    }

    public Map<String, IOException> getErrors() {
        return errors;
    }
}
