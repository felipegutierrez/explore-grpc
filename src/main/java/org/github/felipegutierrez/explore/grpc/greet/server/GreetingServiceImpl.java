package org.github.felipegutierrez.explore.grpc.greet.server;

import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.greet.*;

public class GreetingServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        // extract necessary fields
        Greeting greeting = request.getGreeting();
        Numbers numbers = request.getNumbers();

        // create the response
        String result = "Hello " + greeting.getFirstName() + " " + greeting.getLastName() + ". Welcome to gRPC!";
        String sum = "The sum is: " + (numbers.getFirstTerm() + numbers.getSecondTerm()) + " =)";
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .setSum(sum)
                .build();

        // send the response
        responseObserver.onNext(response);

        // complete the gRPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        try {
            // extract necessary fields
            Greeting greeting = request.getGreeting();

            for (int i = 0; i < 100; i++) {
                // create the response
                String result = "Hello " + greeting.getFirstName() + " " + greeting.getLastName() + ". Welcome to gRPC! response number[" + i + "]";
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                        .setResult(result)
                        .build();

                // send the response
                responseObserver.onNext(response);
                Thread.sleep(1000L);
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            // complete the gRPC call
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {
            String state = "";

            @Override
            public void onNext(LongGreetRequest value) {
                // client sends a message
                state += "Hello " + value.getGreeting().getFirstName() + " " + value.getGreeting().getLastName() + "! ";
            }

            @Override
            public void onError(Throwable t) {
                // client sends an error
            }

            @Override
            public void onCompleted() {
                // client processed all messages
                LongGreetResponse response = LongGreetResponse.newBuilder()
                        .setResult(state)
                        .build();
                responseObserver.onNext(response);
            }
        };
        return requestObserver;
    }
}
