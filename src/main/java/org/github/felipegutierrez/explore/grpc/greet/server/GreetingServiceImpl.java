package org.github.felipegutierrez.explore.grpc.greet.server;

import io.grpc.Context;
import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.greet.*;

import java.util.Random;
import java.util.stream.Stream;

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
        // extract necessary fields
        Greeting greeting = request.getGreeting();
        int times = request.getTimes();

        Stream.iterate(0, n -> n + 1).limit(times).forEach(n -> {
            String result = "Hello " + greeting.getFirstName() + " " + greeting.getLastName() + ". Welcome to gRPC! response number[" + n + "]";
            GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                    .setResult(result)
                    .build();
            // send the response
            responseObserver.onNext(response);
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        // complete the gRPC call
        responseObserver.onCompleted();
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
                responseObserver.onCompleted();
            }
        };
        return requestObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        StreamObserver<GreetEveryoneRequest> requestObserver = new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest value) {
                String result = "Hello " + value.getGreeting().getFirstName() + " " + value.getGreeting().getLastName();
                GreetEveryoneResponse response = GreetEveryoneResponse.newBuilder()
                        .setResult(result)
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

    @Override
    public void greetWithDeadline(GreetWithDeadlineRequest request, StreamObserver<GreetWithDeadlineResponse> responseObserver) {
        try {
            Context context = Context.current();

            Random random = new Random();
            long time = (random.nextInt(5)) * 100;
            System.out.println("taking " + time + " milliseconds to process...");
            Thread.sleep(time);

            // if the time reaches the upper bound dead line we return the gRPC call
            if (context.isCancelled()) {
                System.out.println("context.isCancelled()");
                return;
            }

            String result = "Hello " + request.getGreeting().getFirstName() + " " + request.getGreeting().getLastName() + " after " + time + " milliseconds.";
            GreetWithDeadlineResponse response = GreetWithDeadlineResponse.newBuilder()
                    .setResult(result)
                    .build();
            responseObserver.onNext(response);
            // dont forget to complete the call and send message back to the client!!!
            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
