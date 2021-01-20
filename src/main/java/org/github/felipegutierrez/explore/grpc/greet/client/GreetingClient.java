package org.github.felipegutierrez.explore.grpc.greet.client;

import io.grpc.*;
import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.greet.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingClient {

    // creating a list of request to send to the server
    List<Pair<String, String>> people = Arrays.asList(
            new Pair("Felipe", "Gutierrez"),
            new Pair("Simone", "Farias"),
            new Pair("Fabio", "Gutierrez"),
            new Pair("Daniel", "Gutierrez"),
            new Pair("John", "Oliver"),
            new Pair("Michael", "Jordan"),
            new Pair("Oscar", "Wilde"),
            new Pair("Madonna", "Monalisa")
    );

    private ManagedChannel channel;

    public static void main(String[] args) {
        GreetingClient client = new GreetingClient();

        client.createChannel();
        client.runUnaryGrpc();
        client.runStreamServerGrpc();
        client.runStreamClientGrpc();
        client.runStreamBiDirectionalGrpc();
        client.runUnaryWithDeadlineGrpc();
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

        // create the greeting service client (asynchronous)
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

        people.forEach(person -> {
                    // create the protocol buffer message Greeting
                    Greeting greeting = Greeting.newBuilder()
                            .setFirstName(person.x)
                            .setLastName(person.y)
                            .build();
                    LongGreetRequest request = LongGreetRequest.newBuilder()
                            .setGreeting(greeting)
                            .build();
                    System.out.println("sending message: " + person);
                    requestObserver.onNext(request);
                    try {
                        // simulate some computation to check asynchronous behaviour
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

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
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("received response from server: " + value.getResult());
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

        people.forEach(person -> {
                    // create the protocol buffer message Greeting
                    Greeting greeting = Greeting.newBuilder()
                            .setFirstName(person.x)
                            .setLastName(person.y)
                            .build();
                    GreetEveryoneRequest request = GreetEveryoneRequest.newBuilder()
                            .setGreeting(greeting)
                            .build();
                    System.out.println("sending message: " + person);
                    requestObserver.onNext(request);
                    try {
                        // simulate some computation to check asynchronous behaviour
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runUnaryWithDeadlineGrpc() {
        // create the greeting service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(channel);

        people.forEach(person -> {
            // create the protocol buffer message Greeting
            Greeting greeting = Greeting.newBuilder()
                    .setFirstName(person.x)
                    .setLastName(person.y)
                    .build();
            // create a greeting request with the protocol buffer greeting message
            GreetWithDeadlineRequest request = GreetWithDeadlineRequest.newBuilder()
                    .setGreeting(greeting)
                    .build();
            try {
                System.out.println("Sending message: " + greeting.toString());
                // call the gRPC and get back a protocol buffer GreetingResponse
                GreetWithDeadlineResponse greetResponse = syncClient
                        .withDeadline(Deadline.after(300, TimeUnit.MILLISECONDS))
                        .greetWithDeadline(request);
                System.out.println("Hello from server with deadline: " + greetResponse.getResult());
            } catch (StatusRuntimeException e) {
                if (e.getStatus().getCode() == Status.Code.DEADLINE_EXCEEDED) {
                    System.err.println("Deadline exceeded for " + greeting.toString() + ", we by pass the answer.");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}

class Pair<S, T> {
    public final S x;
    public final T y;

    public Pair(S x, T y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
