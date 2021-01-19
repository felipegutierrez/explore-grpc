package org.github.felipegutierrez.explore.grpc.prime.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.github.felipegutierrez.explore.grpc.prime.PrimeNumberRequest;
import org.github.felipegutierrez.explore.grpc.prime.PrimeNumberServiceGrpc;
import org.github.felipegutierrez.explore.grpc.prime.SourceNumber;

public class PrimeNumberDecompositeClient {
    public static void main(String[] args) {
        PrimeNumberDecompositeClient client = new PrimeNumberDecompositeClient();
        client.runStreamServerGrpc();
    }

    public void runStreamServerGrpc() {
        System.out.println("PrimeNumberDecomposite client - Hello gRPC");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // create the greeting PrimeNumber client (blocking - synchronous)
        PrimeNumberServiceGrpc.PrimeNumberServiceBlockingStub syncClient = PrimeNumberServiceGrpc.newBlockingStub(channel);

        // create the protocol buffer message SourceNumber
        SourceNumber sourceNumber = SourceNumber.newBuilder()
                .setValue(225054876)
                .build();

        // create a PrimeNumber request with the protocol buffer greeting message
        PrimeNumberRequest request = PrimeNumberRequest.newBuilder()
                .setSourceNumber(sourceNumber)
                .build();

        // call the gRPC and get back a protocol buffer GreetingResponse
        syncClient.decomposite(request)
                .forEachRemaining(primeNumberResponse -> {
                    System.out.println(primeNumberResponse.getResult());
                });

        System.out.println("Shutting down GreetingClient");
        channel.shutdown();
    }
}
