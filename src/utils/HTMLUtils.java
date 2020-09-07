package utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

public final class HTMLUtils {
    private static final Set<String> expansion = Set.of(
            ".css", ".js", ".png", ".jpeg", ".ico", ".html", ".vector",
            ".php", ",jpg", ".json", ".svg", ".xml");


    public static String escapeSymbols(String str) {
        return str.replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("&mdash;", "\u2014")
                .replaceAll("&nbsp;", "\u00A0")
                .replaceAll("&reg;", "\u00AE");
    }

    public static String mergeUrl(String url, String link) {
        try {
            return (new URL(new URL(url), removeFragment(link))).toString();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static String getHost(final String url) throws MalformedURLException {
        return getURI(url).getHost();
    }

    public static URI getURI(final String url) throws MalformedURLException {
        final String fragment = removeFragment(url);
        try {
            final URI uri = new URL(fragment).toURI();
            return uri.getPath() == null || uri.getPath().isEmpty() ? new URL(fragment + "/").toURI() : uri;
        } catch (final URISyntaxException e) {
            throw new MalformedURLException(e.getMessage());
        }
    }

    public static String removeFragment(final String url) {
        final int index = url.indexOf('#');
        return index >= 0 ? url.substring(0, index) : url;
    }

    public static String getExpansion(String urlNew) {
        int i = urlNew.lastIndexOf('.') + 1;
        StringBuilder str = new StringBuilder();
        str.append('.');
        while (i < urlNew.length() && Character.isLetter(urlNew.charAt(i))) {
            str.append(urlNew.charAt(i));
            i++;
        }
        if (expansion.contains(str.toString())) {
            return str.toString();
        }
        return "";
    }

}
