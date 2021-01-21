package ru.job4j.pooh.http;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpResponse.
 *
 * @author Ivan Belyaev
 * @version 1.0
 * @since 11.01.2021
 */
public class HttpResponse {
    /** Http protocol version. */
    private String protocol = "HTTP/1.1";
    /** Response code. */
    private int code;
    /** Status. */
    private String status = "";
    /** Response headers. */
    private Map<String, String> headers = new HashMap<>();
    /** Response body. */
    private String body = "";

    /**
     * Constructor.
     */
    public HttpResponse() {
        code = 200;
        status = "OK";
    }

    /**
     * Returns response protocol.
     * @return response protocol.
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets response protocol.
     * @param protocol new response protocol.
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Returns response code.
     * @return response code.
     */
    public int getCode() {
        return code;
    }

    /**
     * Sets response code.
     * @param code new response code.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Gets status.
     * @return status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets response status.
     * @param status new response status.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns response headers.
     * @return response headers.
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Adds new header into response.
     * @param name header name.
     * @param value header value.
     */
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    /**
     * Adds new headers into response.
     * @param headers new headers.
     */
    public void addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    /**
     * Returns response body.
     * @return response body.
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets new response body.
     * @param body new response body.
     */
    public void setBody(String body) {
        this.body = body;
        headers.put("Content-Length", Integer.toString(body.length()));
    }

    /**
     * Adds cookie into response.
     * @param name cookie name.
     * @param value cookie value.
     */
    public void addCookie(String name, String value) {
        String cookies = headers.computeIfAbsent("Set-Cookie", (key) -> "");
        addHeader("Set-Cookie", String.format("%s%s=%s;", cookies, name, value));
    }
}
