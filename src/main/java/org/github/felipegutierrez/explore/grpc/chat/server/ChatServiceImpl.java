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
    public void echo(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
        String message = request.getMessage();
        String reply = this.name + " echo: " + message;
        EchoResponse response = EchoResponse.newBuilder()
                .setMessage(reply)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
