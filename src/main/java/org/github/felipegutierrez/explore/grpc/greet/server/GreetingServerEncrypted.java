package org.github.felipegutierrez.explore.grpc.greet.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServerEncrypted {
    public static void main(String[] args) {
        GreetingServerEncrypted server = new GreetingServerEncrypted();
        server.run();
    }

    public void run() {
        try {
            System.out.println("Greeting server encrypted - Hello gRPC");

            File certChain = new File("ssl/server.crt");
            File privateKey = new File("ssl/server.pem");

            Server server = ServerBuilder.forPort(50051)
                    .addService(new GreetingServiceImpl())
                    .useTransportSecurity(certChain, privateKey)
                    .build();

            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("received shutdown request");
                server.shutdown();
                System.out.println("successfully server encrypted shutdown");
            }));

            server.awaitTermination();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
