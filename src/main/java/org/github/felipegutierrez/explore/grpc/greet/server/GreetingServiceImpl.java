package org.github.felipegutierrez.explore.grpc.greet.server;

import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.greet.GreetRequest;
import org.github.felipegutierrez.explore.grpc.greet.GreetResponse;
import org.github.felipegutierrez.explore.grpc.greet.GreetServiceGrpc;
import org.github.felipegutierrez.explore.grpc.greet.Greeting;

public class GreetingServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        // extract necessary fields
        Greeting greeting = request.getGreeting();

        // create the response
        String result = "Hello " + greeting.getFirstName() + " " + greeting.getLastName() + ". Welcome to gRPC!";
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build();

        // send the response
        responseObserver.onNext(response);

        // complete the gRPC call
        responseObserver.onCompleted();
    }
}
