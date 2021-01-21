package ru.job4j.pooh.storage;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Storage.
 *
 * @author Ivan Belyaev
 * @version 1.0
 * @since 18.01.2021
 */
public class Storage {
    /**
     * Storage for queues.
     */
    private Map<String, BlockingQueue<String>> queues = new ConcurrentHashMap<>();
    /**
     * Strorage for topics.
     */
    private Map<String, CopyOnWriteArrayList<String>> topics = new ConcurrentHashMap<>();

    /**
     * Adds message into the queue.
     * @param name queue name.
     * @param text message text.
     */
    public void addQueueMessage(String name, String text) {
        BlockingQueue<String> bq = queues.computeIfAbsent(name, (key) -> new LinkedBlockingQueue<>());
        bq.offer(text);
    }

    /**
     * Adds message into the topic.
     * @param name topic name.
     * @param text message text.
     */
    public void addTopicMessage(String name, String text) {
        CopyOnWriteArrayList<String> list = topics.computeIfAbsent(name, (key) -> new CopyOnWriteArrayList<>());
        list.add(text);
    }

    /**
     * Gets message from the queue.
     * @param name queue name.
     * @return message from the queue. If queue doesn't exist returns null.
     */
    public String getQueueMessage(String name) {
        String result = null;
        BlockingQueue<String> queue = queues.get(name);
        if (queue != null) {
            result = queue.poll();
            if (queue.isEmpty()) {
                    queues.remove(name);
            }
        }
        return result;
    }

    /**
     * Gets message from the topic.
     * @param name topic name.
     * @param index topic index.
     * @return message from the topic.
     * If topic doesn't exist or index greater than the topic size returns null.
     */
    public String getTopicMessage(String name, int index) {
        String result = null;
        CopyOnWriteArrayList<String> topic = topics.get(name);
        if (topic != null) {
            if (index < topic.size()) {
                result = topic.get(index);
            }
        }
        return result;
    }
}
