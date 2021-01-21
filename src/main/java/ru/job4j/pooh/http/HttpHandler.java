package ru.job4j.pooh.http;

import org.json.simple.parser.ParseException;
import ru.job4j.pooh.json.JsonMaker;
import ru.job4j.pooh.json.JsonParser;
import ru.job4j.pooh.storage.Storage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.util.Map;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * HttpHandler.
 *
 * @author Ivan Belyaev
 * @version 1.0
 * @since 18.01.2021
 */
public class HttpHandler implements Runnable {
    /** Strorage. */
    private final Storage storage;
    /** Socket. */
    private final Socket socket;

    /**
     * Constructor.
     * @param storage data storage.
     * @param socket client socket.
     */
    public HttpHandler(Storage storage, Socket socket) {
        this.storage = storage;
        this.socket = socket;
    }

    /**
     * Http handler.
     */
    @Override
    public void run() {
        try (Socket socket = this.socket;
             PrintWriter writer = new PrintWriter(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            try {
                HttpRequest httpRequest = new HttpRequest(reader);
                HttpResponse httpResponse = new HttpResponse();
                switch (httpRequest.getMethod()) {
                    case "GET":
                        doGet(httpRequest, httpResponse);
                        break;
                    case "POST":
                        doPost(httpRequest, httpResponse);
                        break;
                    default:
                        httpResponse.setCode(400);
                        httpResponse.setStatus("Bad Request");
                }

                sendResponse(httpResponse, writer);
            } catch (ParseException e) {
                String response = "Wrong JSON";
                writer.write("HTTP/1.1 422 Unprocessable Entity\n\n");
                writer.write("Content-Length: " + Integer.toString(response.length()));
                writer.write(response);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends response into client output stream.
     * @param httpResponse http response.
     * @param writer wrapper for client output stream.
     */
    private void sendResponse(HttpResponse httpResponse, PrintWriter writer) {
        writer.write(
                String.format("%s %s %s\n",
                        httpResponse.getProtocol(),
                        httpResponse.getCode(),
                        httpResponse.getStatus()
                )
        );
        httpResponse.getHeaders().entrySet().stream()
                .forEach((entry) -> writer.write(String.format("%s: %s\n", entry.getKey(), entry.getValue())));
        writer.write("\n");
        writer.write(httpResponse.getBody());
        writer.flush();
    }

    /**
     * Processes the post request.
     * @param httpRequest http request.
     * @param httpResponse http response.
     * @throws ParseException possible exception.
     */
    private void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws ParseException {
        JsonParser json = new JsonParser(httpRequest.getBody());
        String uri = httpRequest.getUri();
        if (uri.equals("/queue")) {
            storage.addQueueMessage(json.getName(), json.getText());
        } else if (uri.equals("/topic")) {
            storage.addTopicMessage(json.getName(), json.getText());
        }
    }

    /**
     * Processes the get request.
     * @param httpRequest http request.
     * @param httpResponse http response.
     * @throws ParseException possible exception.
     */
    private void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        String[] uri = httpRequest.getUri().split("/");
        boolean isFound = false;
        if (uri[1].equals("queue")) {
            String text = storage.getQueueMessage(uri[2]);
            if (text != null) {
                httpResponse.setBody(JsonMaker.getJsonString("queue", uri[1], text));
                isFound = true;
            }
        } else if (uri[1].equals("topic")) {
            Map<String, String> cookies = httpRequest.getCookies();
            int index = 0;
            if (cookies.containsKey(uri[2])) {
                index = Integer.parseInt(cookies.get(uri[2]));
            }
            String text = storage.getTopicMessage(uri[2], index);
            if (text != null) {
                httpResponse.setBody(JsonMaker.getJsonString("topic", uri[2], text));
                httpResponse.addCookie(uri[2], Integer.toString(index + 1));
                httpResponse.addCookie("Expires", ZonedDateTime.now().plusDays(100).format(RFC_1123_DATE_TIME));
                isFound = true;
            }

            if (!isFound) {
                httpResponse.setCode(404);
                httpResponse.setStatus("Not Found");
            }
        }
    }
}
