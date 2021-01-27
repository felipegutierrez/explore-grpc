package org.github.felipegutierrez.explore.grpc.chat.server;

import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.chat.EchoRequest;
import org.github.felipegutierrez.explore.grpc.chat.EchoResponse;
import org.github.felipegutierrez.explore.grpc.chat.EchoServiceGrpc;

public class ChatServiceImpl extends EchoServiceGrpc.EchoServiceImplBase {

    private final String name;

    ChatServiceImpl(String name) {
        this.name = name;
    }

    @Override
    public void echoUnary(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
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
        StreamObserver<EchoRequest> requestObserver = new StreamObserver<EchoRequest>() {
            @Override
            public void onNext(EchoRequest value) {
                String msg = value.getMessage();
                System.out.println("received message: " + msg);
                EchoResponse response = EchoResponse.newBuilder()
                        .setMessage(msg)
                        .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }
}
