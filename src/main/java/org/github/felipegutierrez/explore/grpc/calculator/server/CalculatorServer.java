package org.github.felipegutierrez.explore.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;

import java.io.IOException;

public class CalculatorServer {
    public static void main(String[] args) {
        CalculatorServer server = new CalculatorServer();
        server.run();
    }

    private void reflectionInstructions() {
        System.out.println("enabling reflection (use https://github.com/ktr0731/evans on the terminal:");
        System.out.println("$ ./evans -r -p 50051)");
        System.out.println("> show package");
        System.out.println("> package grpc.reflection.v1alpha");
        System.out.println("> show service");
        System.out.println("> service CalculatorService");
        System.out.println("> show message");
        System.out.println("> call SquareRoot");
        System.out.println("> ");
    }

    private void run() {
        try {
            System.out.println("Calculator server - Hello gRPC");
            Server server = ServerBuilder.forPort(50051)
                    .addService(new CalculatorServiceImpl())
                    .addService(ProtoReflectionService.newInstance()) // enabling reflection
                    .build();
            reflectionInstructions();

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
