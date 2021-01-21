package ru.job4j.pooh;

import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.pooh.http.HttpRequest;
import ru.job4j.pooh.json.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * ServerTest.
 *
 * @author Ivan Belyaev
 * @version 1.0
 * @since 16.01.2021
 */
public class ServerTest {
    /**
     * Starts the server before starting tests.
     */
    @BeforeClass
    public static void startServer() {
        Thread serverThread = new Thread(() -> new Server().start());
        serverThread.start();
    }

    /**
     * Simple test for queue.
     * @throws IOException possible exception.
     * @throws ParseException possible exception.
     */
    @Test
    public void whenPostsTwoQueuesThenGetsTheSameQueueInTheSameOrder() throws IOException, ParseException {
        doPost("queue", "weather", "temp-1");
        doPost("queue", "weather", "temp-2");
        HttpRequest firstGet = doGet("queue", "weather", "");
        HttpRequest secondGet = doGet("queue", "weather", "");

        assertThat(new JsonParser(firstGet.getBody()).getText(), is("temp-1"));
        assertThat(new JsonParser(secondGet.getBody()).getText(), is("temp-2"));
    }

    /**
     * Simple test for topic.
     * @throws IOException possible exception.
     * @throws ParseException possible exception.
     */
    @Test
    public void whenPostsTwoTopicsThenGetsTheSameTopicsManyTimes() throws IOException, ParseException {
        doPost("topic", "weather", "temp-1");
        doPost("topic", "weather", "temp-2");
        HttpRequest firstGet = doGet("topic", "weather", ""); // index = 0;
        HttpRequest secondGet = doGet("topic", "weather", ""); // index = 0;
        HttpRequest thirdGet = doGet("topic", "weather", "weather=1"); // index = 1;


        assertThat(new JsonParser(firstGet.getBody()).getText(), is("temp-1"));
        assertThat(new JsonParser(secondGet.getBody()).getText(), is("temp-1"));
        assertThat(new JsonParser(thirdGet.getBody()).getText(), is("temp-2"));
    }

    /**
     * Concurrent test for topic.
     * @throws InterruptedException possible exception.
     */
    @Test
    public void whenPostsIntoTopicManyClientsThenGetsFromTopicManyClients() throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int index = i;
            Runnable post = () -> {
                try {
                    doPost("topic", "weather2", "temp-" + index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            threads[i] = new Thread(post);
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }

        List<String> answers = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            Runnable get = () -> {
                try {
                    HttpRequest httpRequest = doGet("topic", "weather2", "weather2=" + index);
                    String text = new JsonParser(httpRequest.getBody()).getText();
                    answers.add(text);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            };
            threads[i] = new Thread(get);
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }

        for (int i = 0; i < 10; i++) {
            assertTrue(answers.contains("temp-" + i));
        }
    }

    /**
     * Concurrent test for queue.
     * @throws InterruptedException possible exception.
     */
    @Test
    public void whenPostsIntoQueueManyClientsThenGetsFromQueueManyClients() throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int index = i;
            Runnable post = () -> {
                try {
                    doPost("queue", "weather", "temp-" + index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            threads[i] = new Thread(post);
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }

        List<String> answers = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 10; i++) {
            Runnable get = () -> {
                try {
                    HttpRequest httpRequest = doGet("queue", "weather", "");
                    String text = new JsonParser(httpRequest.getBody()).getText();
                    answers.add(text);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            };
            threads[i] = new Thread(get);
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }

        for (int i = 0; i < 10; i++) {
            assertTrue(answers.contains("temp-" + i));
        }
    }

    /**
     * Sends post request to server.
     * @param type message type. Topic or queue.
     * @param name queue or topic name.
     * @param text message text.
     * @return response from server.
     * @throws IOException possible exception.
     */
    private HttpRequest doPost(String type, String name, String text) throws IOException {
        HttpRequest httpRequest;
        try (Socket socket = new Socket("localhost", 8888);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String data = String.format("{\"%s\" : \"%s\", \"text\" : \"%s\"}", type, name, text);
            writer.write(String.format("POST /%s HTTP/1.1\n", type));
            writer.write("Content-Length: " + Integer.toString(data.length()) + "\n\n");
            writer.write(data);
            writer.flush();
            httpRequest = new HttpRequest(reader);
        }
        return httpRequest;
    }

    /**
     * Sends get request to server.
     * @param type message type. Topic or queue.
     * @param name topic or queue name.
     * @param cookie cookie.
     * @return response from server.
     * @throws IOException possible exception.
     */
    private HttpRequest doGet(String type, String name, String cookie) throws IOException {
        HttpRequest httpRequest;
        try (Socket socket = new Socket("localhost", 8888);
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.write(String.format("GET /%s/%s HTTP/1.1\n", type, name));
            if (!cookie.equals("")) {
                writer.write(String.format("Cookie: %s;\n", cookie));
            }
            writer.write("\n");
            writer.flush();
            httpRequest = new HttpRequest(reader);
        }
        return httpRequest;
    }
}
