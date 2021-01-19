package org.github.felipegutierrez.explore.grpc.dummy.client;

import com.example.dummy.DummyServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class DummyClient {
    public static void main(String[] args) {
        DummyClient client = new DummyClient();
        client.run();
    }

    public void run() {
        System.out.println("Dummy client - Hello gRPC");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

        // TODO: do something with the client

        System.out.println("Shutting down Dummy client");
        channel.shutdown();
    }
}
