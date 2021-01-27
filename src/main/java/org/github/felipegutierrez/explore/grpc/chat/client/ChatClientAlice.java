package org.github.felipegutierrez.explore.grpc.chat.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver;
import org.github.felipegutierrez.explore.grpc.chat.EchoRequest;
import org.github.felipegutierrez.explore.grpc.chat.EchoResponse;
import org.github.felipegutierrez.explore.grpc.chat.EchoServiceGrpc;
import org.github.felipegutierrez.explore.grpc.chat.util.MultiAddressNameResolverFactory;

import java.net.InetSocketAddress;
import java.util.stream.Stream;

public class ChatClientAlice {

    public static void main(String[] args) {
        NameResolver.Factory nameResolverFactory = new MultiAddressNameResolverFactory(
                new InetSocketAddress("localhost", 50000),
                new InetSocketAddress("localhost", 50001),
                new InetSocketAddress("localhost", 50002)
        );

        ManagedChannel channel = ManagedChannelBuilder.forTarget("service")
                .nameResolverFactory(nameResolverFactory)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();

        EchoServiceGrpc.EchoServiceBlockingStub stub = EchoServiceGrpc.newBlockingStub(channel);

        Stream.iterate(0, i -> i + 1)
                .limit(100)
                .forEach(integer -> {
                    EchoResponse response = stub.echo(EchoRequest.newBuilder()
                            .setMessage("Hello, I am " + ChatClientAlice.class.getSimpleName() + "! I am sending message number " + integer + ".")
                            .build());
                    System.out.println(response.getMessage());

                    // throttle the stream
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
    }
}
