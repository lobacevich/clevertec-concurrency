package by.clevertec.lobacevich.project.server;

import by.clevertec.lobacevich.project.data.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class ServerTest {

    public static final int LIST_SIZE = 100;
    public static final int THREAD_COUNT = 5;
    public static final int TIME_OUT = 5;
    private final Server server = new Server();

    @Test
    void processRequestShouldReturnCorrectResponses() throws InterruptedException {
        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= LIST_SIZE; i++) {
            expected.add(i);
        }
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<List<Response>>> futureList = new ArrayList<>();
        int size_part = LIST_SIZE / THREAD_COUNT;
        for (int i = 0; i < THREAD_COUNT; i++) {
            int from = size_part * i;
            int to = size_part * (i + 1);
            futureList.add(executorService.submit(new ServerTestDataCallable(from, to, server)));
        }
        executorService.shutdown();
        executorService.awaitTermination(TIME_OUT, TimeUnit.MINUTES);

        List<Integer> actual = futureList.stream()
                .map(listFuture -> {
                    try {
                        return listFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException("Can't get future value");
                    }
                })
                .flatMap(List::stream)
                .map(Response::getResult)
                .sorted()
                .collect(Collectors.toList());

        assertIterableEquals(expected, actual);
    }

    @Test
    void processRequestShouldGetCorrectResponseList() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        int size_part = LIST_SIZE / THREAD_COUNT;
        for (int i = 0; i < THREAD_COUNT; i++) {
            int from = size_part * i;
            int to = size_part * (i + 1);
            executorService.submit(new ServerTestDataCallable(from, to, server));
        }
        executorService.shutdown();
        executorService.awaitTermination(TIME_OUT, TimeUnit.MINUTES);
        Integer expected = 5050;

        Integer actual = server.getResponseList().stream().reduce(0, Integer::sum);

        assertEquals(expected, actual);
    }
}
