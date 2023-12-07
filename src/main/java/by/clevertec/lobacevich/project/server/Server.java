package by.clevertec.lobacevich.project.server;

import by.clevertec.lobacevich.project.data.Request;
import by.clevertec.lobacevich.project.data.Response;
import by.clevertec.lobacevich.project.exception.MyInterruptedException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    public static final int LOWER_BOUND = 100;
    public static final int UPPER_BOUND = 901;

    @Getter
    private final List<Integer> responseList = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final Random random = new Random();

    public Response processRequest(Request request) {
        int value = request.getValue();
        lock.lock();
        responseList.add(value);
        try {
            Thread.sleep(random.nextInt(UPPER_BOUND) + LOWER_BOUND);
            return new Response(responseList.size());
        } catch (InterruptedException e) {
            throw new MyInterruptedException("Thread " + Thread.currentThread().getName() +
                    " was interrupted");
        } finally {
            lock.unlock();
        }
    }
}
