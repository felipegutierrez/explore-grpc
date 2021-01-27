package org.github.felipegutierrez.explore.grpc.chat.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolver;
import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.chat.EchoRequest;
import org.github.felipegutierrez.explore.grpc.chat.EchoResponse;
import org.github.felipegutierrez.explore.grpc.chat.EchoServiceGrpc;
import org.github.felipegutierrez.explore.grpc.chat.util.MultiAddressNameResolverFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ChatClientBob {
    private NameResolver.Factory nameResolverFactory;
    private ManagedChannel channel;

    public static void main(String[] args) {
        ChatClientBob chatClientBob = new ChatClientBob();
        chatClientBob.createChannel();
        chatClientBob.runUnaryChat();
        chatClientBob.runBiDiStreamChat();
        chatClientBob.closeChannel();
    }

    private void createChannel() {
        System.out.println("creating gRPC channel for Bob");
        nameResolverFactory = new MultiAddressNameResolverFactory(
                new InetSocketAddress("localhost", 50000),
                new InetSocketAddress("localhost", 50001),
                new InetSocketAddress("localhost", 50002)
        );
        channel = ManagedChannelBuilder.forTarget("service")
                .nameResolverFactory(nameResolverFactory)
                .defaultLoadBalancingPolicy("round_robin")
                .usePlaintext()
                .build();
    }

    private void closeChannel() {
        System.out.println("Shutting gRPC channel for Bob");
        channel.shutdown();
    }

    private void runUnaryChat() {
        System.out.println("creating unary stub for bob");
        EchoServiceGrpc.EchoServiceBlockingStub stub = EchoServiceGrpc.newBlockingStub(channel);

        Stream.iterate(0, i -> i + 1)
                .limit(10)
                .forEach(integer -> {
                    String msg = "Hello, I am " + ChatClientBob.class.getSimpleName() + "! I am sending message number " + integer + ".";
                    System.out.println("bob sending unary message: " + msg);
                    EchoResponse response = stub.echoUnary(EchoRequest.newBuilder()
                            .setMessage(msg)
                            .build());
                    System.out.println("Bob receiving unary message: " + response.getMessage());

                    // throttle the stream
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
        System.out.println("Bob unary is done.");
    }

    private void runBiDiStreamChat() {
        System.out.println("creating Bidirectional stream stub for bob");
        EchoServiceGrpc.EchoServiceStub asyncClient = EchoServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<EchoRequest> requestObserver = asyncClient.echoBiDi(new StreamObserver<EchoResponse>() {
            @Override
            public void onNext(EchoResponse value) {
                System.out.println("Bob received response from server: " + value.getMessage());
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

        Stream.iterate(0, i -> i + 1)
                .limit(50)
                .forEach(integer -> {
                    String msg = "Hello, I am " + ChatClientBob.class.getSimpleName() + "! I am sending stream message number " + integer + ".";
                    System.out.println("Bob sending message: " + msg);
                    EchoRequest request = EchoRequest.newBuilder()
                            .setMessage(msg)
                            .build();
                    requestObserver.onNext(request);
                    // throttle the stream
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
        requestObserver.onCompleted();
        System.out.println("Bob BiDi stream is done.");
        try {
            // wait for the time set on the stream + the throttle
            latch.await((1000 * 60), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
