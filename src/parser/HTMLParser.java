package parser;

import model.Attribute;
import model.Tag;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

public class HTMLParser {
    public static final String ROOT_TAG_NAME = "root";
    private static final Set<String> singeTags = Set.of(
            "area", "base", "basefont", "bgsound", "br", "col", "command",
            "embed", "hr", "img", "input", "isindex", "keygen", "link",
            "meta", "param", "source", "track", "wbr", "use", "time");

    private int ch;
    private final Reader reader;
    private final Deque<Tag> tagStack;
    private Tag currentTag;


    public HTMLParser(Reader reader) {
        this.reader = reader;
        this.currentTag = new Tag(ROOT_TAG_NAME);
        this.tagStack = new ArrayDeque<>();
    }

    private void getNextChar() {
        try {
            ch = reader.read();
        } catch (IOException e) {
            System.out.println("Error while working with site " + e.getMessage());
        }
    }

    private void nextChar() {
        getNextChar();
    }

    private void skipWhitespace() {
        while (Character.isWhitespace(ch)) {
            nextChar();
        }
    }

    private boolean check(char c) {
        if (ch == c) {
            nextChar();
            return true;
        }
        return false;
    }


    private void skipComment() {
        while (!(check('-') && check('-') && check('>'))) {
            nextChar();
        }
    }

    public Tag parse() {
        nextChar();
        skipWhitespace();
        while (ch != -1) {
            if (check('<')) {
                if (check('!')) {
                    if (check('D')) {
                        parseToCharacter('>');
                        nextChar();
                    } else {
                        skipComment();
                    }
                } else if (check('/')) {
                    parseCloseTag();
                } else if (Character.isLetter(ch)) {
                    parseOpenTag();
                } else {
                    nextChar();
                }
            } else {
                String value = parseToCharacter('<');
                currentTag.addValue(value);
            }
        }

        if (tagStack.size() != 0) {
            System.err.println("stack size != 0");
            return tagStack.peekLast();
        }
        return currentTag;
    }

    private String parseToCharacter(char c) {
        StringBuilder str = new StringBuilder();
        while (ch != c) {
            str.append((char) ch);
            nextChar();
        }
        return str.toString();
    }

    private String parseName() {
        StringBuilder sb = new StringBuilder();
        while (Character.isLetter(ch) || ch == '-' || ch == '_' || Character.isDigit(ch) || ch == ':' || ch == '.') {
            sb.append((char) ch);
            nextChar();
        }
        return sb.toString();
    }

    private String parseTo(String str) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            for (int i = 0; i < str.length(); i++) {
                if (!check(str.charAt(i))) {
                    sb.append(str, 0, i);
                    sb.append((char) ch);
                    nextChar();
                    break;
                }
                if (i == str.length() - 1) {
                    return sb.toString();
                }
            }
        }
    }

    private String parseScriptSection() {
        return parseTo("</script>");
    }

    private void parseOpenTag() {
        String tag = parseName().toLowerCase();
        skipWhitespace();
        Tag tmpElement = new Tag(tag);

        while (true) {
            if (check('>')) {
                currentTag.addValue(tmpElement);
                if (tag.equals("script")) {
                    String script = parseScriptSection();
                    tmpElement.addValue(script);
                } else if (!singeTags.contains(tag)) {
                    tagStack.push(currentTag);
                    currentTag = tmpElement;
                } else {
                    tmpElement.setSingle();
                }
                break;
            }
            if (check('/') && check('>')) {
                tmpElement.setSingle();
                currentTag.addValue(tmpElement);
                break;
            }
            String attributeName = parseName();
            parseToCharacter('=');
            nextChar();
            skipWhitespace();

            if (check('"')) {
                String attributeValue = parseToCharacter('"');
                nextChar();

                tmpElement.addAttribute(new Attribute(attributeName, attributeValue));
            } else {
                parseToCharacter('>');
            }

            skipWhitespace();
        }
    }

    private void parseCloseTag() {
        skipWhitespace();
        String name = parseName();
        if (currentTag.getName().equals(name)) {
            currentTag = tagStack.pop();
        }
        parseToCharacter('>');
        nextChar();
        skipWhitespace();
    }
}
