package org.github.felipegutierrez.explore.grpc.dummy.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class DummyServer {
    public static void main(String[] args) {
        DummyServer server = new DummyServer();
        server.run();
    }

    public void run() {
        try {
            System.out.println("Dummy server - Hello gRPC");
            Server server = ServerBuilder.forPort(50051).build();

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
