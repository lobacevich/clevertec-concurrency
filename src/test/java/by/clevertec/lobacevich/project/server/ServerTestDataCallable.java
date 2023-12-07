package by.clevertec.lobacevich.project.server;

import by.clevertec.lobacevich.project.data.Request;
import by.clevertec.lobacevich.project.data.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class ServerTestDataCallable implements Callable<List<Response>> {

    private final int from;
    private final int to;
    private final List<Response> data;
    private final Server server;

    public ServerTestDataCallable(int from, int to, Server server) {
        this.from = from;
        this.to = to;
        this.data = new ArrayList<>();
        this.server = server;
    }

    @Override
    public List<Response> call() {
        for (int i = from + 1; i <= to; i++) {
            data.add(server.processRequest(new Request(i)));
        }
        return data;
    }
}
