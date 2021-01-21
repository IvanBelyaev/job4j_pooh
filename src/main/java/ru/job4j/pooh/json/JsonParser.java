package ru.job4j.pooh.json;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * JsonParser.
 * Reads. JSON-string.
 * Format: { type : name, "text" : text }.
 * @author Ivan Belyaev
 * @version 1.0
 * @since 12.01.2021
 */
public class JsonParser {
    /** Name. */
    private String name;
    /** Text. */
    private String text;

    /**
     * Constructor.
     * @param jsonString JSON-string.
     * @throws ParseException possible exception.
     */
    public JsonParser(String jsonString) throws ParseException {
        parse(jsonString);
    }

    /**
     * Processes JSON-string.
     * @param jsonString JSON-string.
     * @throws ParseException possible exception.
     */
    private void parse(String jsonString) throws ParseException {
        JSONObject jo = (JSONObject) new JSONParser().parse(jsonString);
        name = (String) jo.get("queue");
        if (name == null) {
            name = (String) jo.get("topic");
            if (name == null) {
                throw new ParseException(1);
            }
        }
        text = (String) jo.get("text");
    }

    /**
     * Returns name from JSON-string.
     * @return name from JSON-string.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns text from JSON-string.
     * @return text from JSOn-string.
     */
    public String getText() {
        return text;
    }
}
