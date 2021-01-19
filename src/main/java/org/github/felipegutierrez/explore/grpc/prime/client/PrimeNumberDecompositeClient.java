package org.github.felipegutierrez.explore.grpc.prime.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.prime.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PrimeNumberDecompositeClient {
    private ManagedChannel channel;

    public static void main(String[] args) {
        PrimeNumberDecompositeClient client = new PrimeNumberDecompositeClient();
        client.createChannel();
        client.runStreamServerGrpc();
        client.runStreamClientGrpc();
        client.closeChannel();
    }

    private void createChannel() {
        System.out.println("PrimeNumberDecomposite client - Hello gRPC");
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
    }

    private void closeChannel() {
        System.out.println("Shutting down PrimeNumberDecomposite client");
        channel.shutdown();
    }

    private void runStreamServerGrpc() {
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
    }

    private void runStreamClientGrpc() {
        System.out.println("Average client streaming - Hello gRPC");
        List<Integer> numbers = new ArrayList<Integer>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);

        // create the greeting service client (asynchronous)
        PrimeNumberServiceGrpc.PrimeNumberServiceStub asyncClient = PrimeNumberServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<AverageRequest> requestObserver = asyncClient.longAverage(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse value) {
                System.out.println("received response from server");
                System.out.println(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                // received error from server
            }

            @Override
            public void onCompleted() {
                System.out.println("server is done sending data");
                latch.countDown();
            }
        });

        for (Integer n : numbers) {
            // create the protocol buffer message Greeting
            AverageRequest request = AverageRequest.newBuilder()
                    .setValue(n)
                    .build();
            System.out.println("sending number " + n);
            requestObserver.onNext(request);
        }

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
