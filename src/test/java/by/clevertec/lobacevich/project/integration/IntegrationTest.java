package by.clevertec.lobacevich.project.integration;

import by.clevertec.lobacevich.project.client.Client;
import by.clevertec.lobacevich.project.server.Server;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntegrationTest {

    public static final int LIST_SIZE = 100;
    public static final int THREAD_COUNT = 5;
    public static final int TIME_OUT = 5;

    @Test
    void callShouldReturnCorrectAccumulator() throws InterruptedException {
        Server server = new Server();
        Client client = new Client(LIST_SIZE, server);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        Integer expected = 5050;

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(client);
        }
        executorService.shutdown();
        executorService.awaitTermination(TIME_OUT, TimeUnit.MINUTES);
        Integer actual = client.getAccumulator();

        assertEquals(expected, actual);
    }
}
