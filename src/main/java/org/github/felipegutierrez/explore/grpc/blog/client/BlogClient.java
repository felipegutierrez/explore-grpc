package org.github.felipegutierrez.explore.grpc.blog.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {

    private ManagedChannel channel;

    public static void main(String[] args) {
        BlogClient client = new BlogClient();

        client.createChannel();
        // client.runUnaryGrpc();
        client.closeChannel();
    }

    private void createChannel() {
        System.out.println("BlogClient client - Hello gRPC");
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
    }

    private void closeChannel() {
        System.out.println("Shutting down BlogClient");
        channel.shutdown();
    }
}
