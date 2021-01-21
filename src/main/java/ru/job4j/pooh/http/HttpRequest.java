package ru.job4j.pooh.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpRequest.
 *
 * @author Ivan Belyaev
 * @version 1.0
 * @since 11.01.2021
 */
public class HttpRequest {
    /** Request method. */
    private String method = "";
    /** Universal Resource Identifier. */
    private String uri = "";
    /** Http protocol version. */
    private String protocol = "";
    /** Http headers. */
    private Map<String, String> headers = new HashMap<>();
    /** Cookies. */
    private Map<String, String> cookies = new HashMap<>();
    /** Request body. */
    private String body = "";

    /**
     * Constructor.
     * @param reader wrapper for client input stream.
     * @throws IOException possible exception.
     */
    public HttpRequest(BufferedReader reader) throws IOException {
        readRequest(reader);
    }

    /**
     * Reads request.
     * @param reader wrapper for client input stream.
     * @throws IOException possible exception.
     */
    private void readRequest(BufferedReader reader) throws IOException {
        String[] startString = reader.readLine().split(" ");
        method = startString[0];
        uri = startString[1];
        protocol = startString[2];
        readHeaders(reader);
        readBody(reader);
        readCookies();
    }

    /**
     * Reads cookies.
     */
    private void readCookies() {
        String strCookies = headers.get("Cookie");
        if (strCookies != null) {
            String[] cookies = strCookies.split(";");
            for (String cookie : cookies) {
                String[] curCookie = cookie.trim().split("=");
                this.cookies.put(curCookie[0], curCookie[1]);
            }
        }
    }

    /**
     * Reads http headers.
     * @param reader wrapper for client input stream.
     * @throws IOException possible exception.
     */
    private void readHeaders(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (!line.isEmpty()) {
            String[] header = line.split(":", 2);
            headers.put(header[0], header[1]);
            line = reader.readLine();
        }
    }

    /**
     * Reads request body.
     * @param reader wrapper for client input stream.
     * @throws IOException possible exception.
     */
    private void readBody(BufferedReader reader) throws IOException {
        String strBodyLength = headers.get("Content-Length");
        if (strBodyLength != null) {
            int bodyLength = Integer.parseInt(strBodyLength.trim());
            StringBuilder stringBuilder = new StringBuilder();
            char[] buf = new char[256];
            int now = 0;
            while (now < bodyLength) {
                int len = reader.read(buf);
                now += len;
                stringBuilder.append(buf, 0, len);
            }
            body = stringBuilder.toString();
        }
    }

    /**
     * Returns request method.
     * @return request method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Returns uri.
     * @return uri.
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns http protocol version.
     * @return http protocol version.
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Returns request headers.
     * @return request headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Returns request body.
     * @return request body.
     */
    public String getBody() {
        return body;
    }

    /**
     * Returns cookies.
     * @return cookies.
     */
    public Map<String, String> getCookies() {
        return cookies;
    }
}
