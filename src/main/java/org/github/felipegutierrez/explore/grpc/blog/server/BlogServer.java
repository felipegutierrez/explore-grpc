package org.github.felipegutierrez.explore.grpc.blog.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class BlogServer {
    public static void main(String[] args) {
        BlogServer server = new BlogServer();
        server.run();
    }

    public void run() {
        try {
            System.out.println("make sure that you started mongodb server");
            System.out.println("docker-compose up");
            System.out.println("Blog server - Hello gRPC");
            Server server = ServerBuilder.forPort(50051)
                    .addService(new BlogServiceImpl())
                    .build();

            server.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("received shutdown request");
                server.shutdown();
                System.out.println("successfully Blog server shutdown");
            }));

            server.awaitTermination();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
