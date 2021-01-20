package org.github.felipegutierrez.explore.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) {
        CalculatorServer server = new CalculatorServer();
        server.run();
    }

    public void run() {
        try {
            System.out.println("Calculator server - Hello gRPC");
            Server server = ServerBuilder.forPort(50051)
                    .addService(new CalculatorServiceImpl())
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
