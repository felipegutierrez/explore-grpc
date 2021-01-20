package org.github.felipegutierrez.explore.grpc.calculator.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.calculator.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CalculatorClient {
    List<Integer> numbers = Arrays.asList(1, 20, 2, 30, 3, 40, 4, 50, 5);
    private ManagedChannel channel;

    public static void main(String[] args) {
        CalculatorClient client = new CalculatorClient();
        client.createChannel();

        client.runStreamServerGrpc();
        client.runStreamClientGrpc();
        client.runStreamBiDirectionalGrpc();

        client.closeChannel();
    }

    private void createChannel() {
        System.out.println("Calculator client - Hello gRPC");
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
    }

    private void closeChannel() {
        System.out.println("Shutting down Calculator client");
        channel.shutdown();
    }

    private void runStreamServerGrpc() {
        // create the greeting PrimeNumber client (blocking - synchronous)
        CalculatorServiceGrpc.CalculatorServiceBlockingStub syncClient = CalculatorServiceGrpc.newBlockingStub(channel);

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

        // create the greeting service client (asynchronous)
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
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

        numbers.forEach(n -> {
            // create the protocol buffer message Greeting
            AverageRequest request = AverageRequest.newBuilder()
                    .setValue(n)
                    .build();
            System.out.println("sending number " + n);
            requestObserver.onNext(request);
        });

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runStreamBiDirectionalGrpc() {
        // create the greeting service client (asynchronous)
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<MaximumRequest> requestObserver = asyncClient.findMaximum(new StreamObserver<MaximumResponse>() {

            @Override
            public void onNext(MaximumResponse value) {
                System.out.println("received maximum value response from server: " + value.getResult());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        numbers.forEach(n -> {
            // create the protocol buffer message Greeting
            MaximumRequest request = MaximumRequest.newBuilder()
                    .setValue(n)
                    .build();
            System.out.println("sending number " + n);
            requestObserver.onNext(request);
        });

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
