package org.github.felipegutierrez.explore.grpc.greet.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetingServer {
    public static void main(String[] args) {
        GreetingServer server = new GreetingServer();
        server.run();
    }

    public void run() {
        try {
            System.out.println("Greeting server - Hello gRPC");
            Server server = ServerBuilder.forPort(50051)
                    .addService(new GreetingServiceImpl())
                    .build();

            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("received shutdown request");
                server.shutdown();
                System.out.println("successfully server shutdown");
            }));

            server.awaitTermination();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
