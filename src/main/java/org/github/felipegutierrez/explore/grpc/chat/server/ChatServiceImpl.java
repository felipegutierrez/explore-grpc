package org.github.felipegutierrez.explore.grpc.chat.server;

import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.chat.EchoRequest;
import org.github.felipegutierrez.explore.grpc.chat.EchoResponse;
import org.github.felipegutierrez.explore.grpc.chat.EchoServiceGrpc;
import org.github.felipegutierrez.explore.grpc.chat.util.SingletlonChatStreamObserver;

public class ChatServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {

    private final String name;
    private final SingletlonChatStreamObserver singletonObservers;

    ChatServiceImpl(String name) {
        this.name = name;
        this.singletonObservers = SingletlonChatStreamObserver.getInstance();
    }

    @Override
    public void echoUnary(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        System.out.println("receive unary call");

        String message = request.getMessage();
        String reply = this.name + " echo: " + message;
        EchoResponse response = EchoResponse.newBuilder()
                .setMessage(reply)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<EchoRequest> echoBiDi(StreamObserver<EchoResponse> responseObserver) {
        System.out.println("received bidirectional call");

        singletonObservers.addObserver(responseObserver);
        System.out.println("added responseObserver to the pool of observers: " + singletonObservers.getObservers().size());

        StreamObserver<EchoRequest> requestObserver = new StreamObserver<EchoRequest>() {
            @Override
            public void onNext(EchoRequest value) {
                String msg = value.getMessage();
                System.out.println("received message: " + msg);
                EchoResponse response = EchoResponse.newBuilder()
                        .setMessage(msg)
                        .build();
                // do not send messages to a single observer but to all observers on the pool
                // responseObserver.onNext(response);
                // observers.foreach...
                singletonObservers.getObservers().forEach(observer -> {
                    observer.onNext(response);
                });
            }

            @Override
            public void onError(Throwable t) {
                // observers.remove(responseObserver);
                singletonObservers.getObservers().remove(responseObserver);
                System.out.println("removed responseObserver to the pool of observers");
            }

            @Override
            public void onCompleted() {
                // do not complete messages to a single observer but to all observers on the pool
                // responseObserver.onCompleted();
                // observers.foreach
                singletonObservers.getObservers().forEach(observer -> {
                    observer.onCompleted();
                });

                // observers.remove(responseObserver);
                System.out.println("removed responseObserver to the pool of observers");
            }
        };
        return requestObserver;
    }
}
