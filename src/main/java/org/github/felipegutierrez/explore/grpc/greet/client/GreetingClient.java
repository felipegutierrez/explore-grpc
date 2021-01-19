package org.github.felipegutierrez.explore.grpc.greet.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.greet.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {
    private ManagedChannel channel;

    public static void main(String[] args) {
        GreetingClient client = new GreetingClient();
        client.createChannel();
//        client.runUnaryGrpc();
//        client.runStreamServerGrpc();
        client.runStreamClientGrpc();
        client.closeChannel();
    }

    private void createChannel() {
        System.out.println("GreetingClient client - Hello gRPC");
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
    }

    private void closeChannel() {
        System.out.println("Shutting down GreetingClient");
        channel.shutdown();
    }

    private void runUnaryGrpc() {
        // create the greeting service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(channel);

        // create the protocol buffer message Greeting
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Felipe")
                .setLastName("Gutierrez")
                .build();
        Numbers numbers = Numbers.newBuilder()
                .setFirstTerm(10)
                .setSecondTerm(15)
                .build();

        // create a greeting request with the protocol buffer greeting message
        GreetRequest request = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .setNumbers(numbers)
                .build();

        // call the gRPC and get back a protocol buffer GreetingResponse
        GreetResponse greetResponse = syncClient.greet(request);
        System.out.println(greetResponse.getResult());
        System.out.println(greetResponse.getSum());
    }

    private void runStreamServerGrpc() {
        System.out.println("GreetingClient client with server streaming - Hello gRPC");
        // create the greeting service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(channel);

        // create the protocol buffer message Greeting
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Felipe")
                .setLastName("Gutierrez")
                .build();

        // create a greeting request with the protocol buffer greeting message
        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        // call the gRPC and get back a protocol buffer GreetingResponse
        // GreetManyTimesResponse greetResponse = syncClient.greet(request);
        syncClient.greetManyTimes(request)
                .forEachRemaining(greetResponse -> {
                    System.out.println(greetResponse.getResult());
                });
    }

    private void runStreamClientGrpc() {
        System.out.println("GreetingClient client streaming - Hello gRPC");
        List<Pair<String, String>> names = new ArrayList<Pair<String, String>>();
        names.add(new Pair("Felipe", "Gutierrez"));
        names.add(new Pair("Simone", "Farias"));
        names.add(new Pair("Fabio", "Gutierrez"));
        names.add(new Pair("Daniel", "Gutierrez"));

        // create the greeting service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
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

        int counter = 1;
        for (Pair<String, String> name : names) {
            // create the protocol buffer message Greeting
            Greeting greeting = Greeting.newBuilder()
                    .setFirstName(name.x)
                    .setLastName(name.y)
                    .build();
            LongGreetRequest request = LongGreetRequest.newBuilder()
                    .setGreeting(greeting)
                    .build();
            System.out.println("sending message " + counter);
            requestObserver.onNext(request);
            counter++;
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

class Pair<S, T> {
    public final S x;
    public final T y;

    public Pair(S x, T y) {
        this.x = x;
        this.y = y;
    }
}
