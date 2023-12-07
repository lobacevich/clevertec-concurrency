package by.clevertec.lobacevich.project.client;

import by.clevertec.lobacevich.project.data.Request;
import by.clevertec.lobacevich.project.data.Response;
import by.clevertec.lobacevich.project.exception.MyInterruptedException;
import by.clevertec.lobacevich.project.server.Server;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client implements Runnable {

    public static final int LOWER_BOUND = 100;
    public static final int UPPER_BOUND = 401;
    private final Lock lock = new ReentrantLock();
    @Getter
    private final List<Integer> requestList;
    private final Server server;
    private final Random random = new Random();
    private final AtomicInteger accumulator = new AtomicInteger();


    public Client(int n, Server server) {
        this.server = server;
        this.requestList = new ArrayList<>();
        fillRequestList(n);
    }

    private void fillRequestList(int n) {
        for (int i = 1; i <= n; i++) {
            requestList.add(i);
        }
    }

    @Override
    public void run() {
        while (!requestList.isEmpty()) {
            lock.lock();
            int value = requestList.remove(random.nextInt(requestList.size()));
            lock.unlock();
            Request request = new Request(value);
            Response response = server.processRequest(request);
            accumulator.addAndGet(response.getResult());
            try {
                Thread.sleep(random.nextInt(UPPER_BOUND) + LOWER_BOUND);
            } catch (InterruptedException e) {
                throw new MyInterruptedException("Thread " + Thread.currentThread().getName() +
                        " was interrupted");
            }
        }
    }

    public Integer getAccumulator() {
        return accumulator.get();
    }
}
