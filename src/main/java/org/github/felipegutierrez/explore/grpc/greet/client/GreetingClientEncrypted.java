package org.github.felipegutierrez.explore.grpc.greet.client;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.github.felipegutierrez.explore.grpc.greet.*;
import org.github.felipegutierrez.explore.grpc.util.Pair;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class GreetingClientEncrypted {

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

    private ManagedChannel secureChannel;

    public static void main(String[] args) {
        GreetingClientEncrypted client = new GreetingClientEncrypted();

        try {
            client.createSecureChannel();
            client.runUnarySecureGrpc();
        } catch (SSLException e) {
            e.printStackTrace();
        } finally {
            client.closeSecureChannel();
        }
    }

    private void createSecureChannel() throws SSLException {
        System.out.println("GreetingClient secure client - Hello gRPC");
        File trustCertificate = new File("ssl/ca.crt");
        secureChannel = NettyChannelBuilder.forAddress("localhost", 50051)
                .sslContext(GrpcSslContexts.forClient().trustManager(trustCertificate).build())
                .build();
    }

    private void closeSecureChannel() {
        System.out.println("Shutting down secure GreetingClient");
        secureChannel.shutdown();
    }

    private void runUnarySecureGrpc() {
        // create the greeting service client (blocking - synchronous)
        GreetServiceGrpc.GreetServiceBlockingStub syncClient = GreetServiceGrpc.newBlockingStub(secureChannel);

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
}
