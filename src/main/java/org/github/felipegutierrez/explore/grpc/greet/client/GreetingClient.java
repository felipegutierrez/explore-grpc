package org.github.felipegutierrez.explore.grpc.greet.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.github.felipegutierrez.explore.grpc.greet.GreetRequest;
import org.github.felipegutierrez.explore.grpc.greet.GreetResponse;
import org.github.felipegutierrez.explore.grpc.greet.GreetServiceGrpc;
import org.github.felipegutierrez.explore.grpc.greet.Greeting;

public class GreetingClient {
    public static void main(String[] args) {
        GreetingClient client = new GreetingClient();
        client.run();
    }

    public void run() {
        System.out.println("GreetingClient client - Hello gRPC");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        // create the greeting service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(channel);

        // create the protocol buffer message Greeting
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Felipe")
                .setLastName("Gutierrez")
                .build();

        // create a greeting request with the protocol buffer greeting message
        GreetRequest request = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        // call the gRPC and get back a protocol buffer GreetingResponse
        GreetResponse greetResponse = syncClient.greet(request);
        System.out.println(greetResponse.getResult());

        System.out.println("Shutting down GreetingClient");
        channel.shutdown();
    }
}
