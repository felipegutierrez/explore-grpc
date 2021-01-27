package org.github.felipegutierrez.explore.grpc.greet.server;

import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;
import org.github.felipegutierrez.explore.grpc.greet.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class GreetingServerTest {
    @Rule
    public final GrpcServerRule grpcServerRule = new GrpcServerRule().directExecutor();

    @Test
    public void greeterUnaryCall() {
        // Add the service to the in-process server.
        grpcServerRule.getServiceRegistry().addService(new GreetingServiceImpl());

        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(grpcServerRule.getChannel());

        String firstName = "Felipe";
        String lastName = "Gutierrez";
        GreetRequest greetRequest = GreetRequest
                .newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName(firstName).setLastName(lastName).build())
                .setNumbers(Numbers.newBuilder().setFirstTerm(10).setSecondTerm(20).build())
                .build();
        GreetResponse greetResponse = blockingStub.greet(greetRequest);

        String result = greetResponse.getResult();
        String sum = greetResponse.getSum();
        assertEquals("Hello " + firstName + " " + lastName + ". Welcome to gRPC!", result);
        assertEquals("The sum is: 30 =)", sum);
    }

    @Test
    public void greeterStreamBiDirectionalCall() {
        List<Pair<String, String>> people = Arrays.asList(
                new Pair("Felipe", "Gutierrez"),
                new Pair("Simone", "Farias")
        );
        List<String> response = new ArrayList<String>();

        // Add the service to the in-process server.
        grpcServerRule.getServiceRegistry().addService(new GreetingServiceImpl());

        // GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(grpcServerRule.getChannel());
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(grpcServerRule.getChannel());
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("received response from server: " + value.getResult());
                response.add(value.getResult());
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

        people.forEach(person -> {
                    // create the protocol buffer message Greeting
                    Greeting greeting = Greeting.newBuilder()
                            .setFirstName(person.x)
                            .setLastName(person.y)
                            .build();
                    GreetEveryoneRequest request = GreetEveryoneRequest.newBuilder()
                            .setGreeting(greeting)
                            .build();
                    System.out.println("sending message: " + person);
                    requestObserver.onNext(request);
                    try {
                        // simulate some computation to check asynchronous behaviour
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        people.forEach(p -> {
            assertTrue(response.contains("Hello " + p.x + " " + p.y));
        });
    }

    class Pair<S, T> {
        public final S x;
        public final T y;

        public Pair(S x, T y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}


