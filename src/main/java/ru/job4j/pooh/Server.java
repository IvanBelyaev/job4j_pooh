package ru.job4j.pooh;

import ru.job4j.pooh.http.HttpHandler;
import ru.job4j.pooh.storage.Storage;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server.
 *
 * @author Ivan Belyaev
 * @version 1.0
 * @since 11.01.2021
 */
public class Server {
    /** Storage. */
    private Storage storage = new Storage();
    /** Thread pool. */
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * Start server.
     */
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            while (!Thread.currentThread().isInterrupted()) {
                executorService.execute(new HttpHandler(storage, serverSocket.accept()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Entry point.
     * @param args command line arguments. Not used.
     */
    public static void main(String[] args) {
        new Server().start();
    }
}
