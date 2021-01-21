package ru.job4j.pooh.json;

/**
 * JsonMaker.
 *
 * @author Ivan Belyaev
 * @version 1.0
 * @since 15.01.2021
 */
public class JsonMaker {
    /**
     * Creates JSON-string.
     * format: { type : name, "text" : text }
     * @param type type.
     * @param name name.
     * @param text text.
     * @return JSON-string.
     */
    public static String getJsonString(String type, String name, String text) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n\t\"");
        sb.append(type);
        sb.append("\" : \"");
        sb.append(name);
        sb.append("\",\n\t\"text\" : \"");
        sb.append(text);
        sb.append("\"\n}");
        return sb.toString();
    }
}
