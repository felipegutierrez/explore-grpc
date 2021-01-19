package org.github.felipegutierrez.explore.grpc.prime.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class PrimeNumberDecompositeServer {
    public static void main(String[] args) {
        PrimeNumberDecompositeServer server = new PrimeNumberDecompositeServer();
        server.run();
    }

    public void run() {
        try {
            System.out.println("PrimeNumberDecomposite server - Hello gRPC");
            Server server = ServerBuilder.forPort(50051)
                    .addService(new PrimeNumberDecompositeServiceImpl())
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
