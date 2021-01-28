package org.github.felipegutierrez.explore.grpc.greet.server;

import io.grpc.Deadline;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;
import org.github.felipegutierrez.explore.grpc.greet.*;
import org.github.felipegutierrez.explore.grpc.util.Pair;
import org.junit.Before;
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
public class GreetingServiceImplTest {
    @Rule
    public final GrpcServerRule grpcServerRule = new GrpcServerRule().directExecutor();

    @Before
    public void addService() {
        // Add the service to the in-process server.
        grpcServerRule.getServiceRegistry().addService(new GreetingServiceImpl());
    }

    @Test
    public void greeterUnaryCall() {
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
    public void greeterUnaryCallManyTimes() {
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(grpcServerRule.getChannel());

        String firstName = "Felipe";
        String lastName = "Gutierrez";
        // create the protocol buffer message Greeting
        Greeting greeting = Greeting.newBuilder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .build();

        // create a greeting request with the protocol buffer greeting message
        GreetManyTimesRequest request = GreetManyTimesRequest.newBuilder()
                .setGreeting(greeting)
                .setTimes(10)
                .build();

        // call the gRPC and get back a protocol buffer GreetingResponse
        List<String> result = new ArrayList<String>();
        blockingStub.greetManyTimes(request)
                .forEachRemaining(greetResponse -> {
                    result.add(greetResponse.getResult());
                });
        assertTrue(result.size() == 10);
        result.forEach(r -> {
            assertTrue(r.contains("Hello " + firstName + " " + lastName));
        });
    }

    @Test
    public void greeterStreamClientCall() {
        List<Pair<String, String>> people = Arrays.asList(
                new Pair("Michael", "Jordan"),
                new Pair("Oscar", "Wilde"),
                new Pair("Madonna", "Monalisa")
        );
        StringBuilder expected = new StringBuilder();
        people.forEach(p -> expected.append("Hello " + p.x + " " + p.y + "! "));
        StringBuilder result = new StringBuilder();

        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(grpcServerRule.getChannel());
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                System.out.println(value.getResult());
                result.append(value.getResult());
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
                    LongGreetRequest request = LongGreetRequest.newBuilder()
                            .setGreeting(greeting)
                            .build();
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
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void greeterStreamBiDirectionalCall() {
        List<Pair<String, String>> people = Arrays.asList(
                new Pair("Felipe", "Gutierrez"),
                new Pair("Simone", "Farias")
        );
        List<String> response = new ArrayList<String>();

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

    @Test
    public void greeterUnaryCallWithLargeDeadline() {
        List<Pair<String, String>> people = Arrays.asList(
                new Pair("Felipe", "Gutierrez"),
                new Pair("Simone", "Farias"),
                new Pair("Michael", "Jordan"),
                new Pair("Oscar", "Wilde"),
                new Pair("Madonna", "Monalisa")
        );
        List<Status.Code> listOfCodes = new ArrayList<Status.Code>();
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(grpcServerRule.getChannel());

        people.forEach(person -> {
            try {
                // create the protocol buffer message Greeting
                Greeting greeting = Greeting.newBuilder()
                        .setFirstName(person.x)
                        .setLastName(person.y)
                        .build();
                // create a greeting request with the protocol buffer greeting message
                GreetWithDeadlineRequest request = GreetWithDeadlineRequest.newBuilder()
                        .setGreeting(greeting)
                        .build();
                System.out.println("Sending message: " + greeting.toString());
                // call the gRPC and get back a protocol buffer GreetingResponse
                GreetWithDeadlineResponse greetResponse = blockingStub
                        .withDeadline(Deadline.after(5, TimeUnit.SECONDS))
                        .greetWithDeadline(request);
                System.out.println("Hello from server with deadline: " + greetResponse.getResult());
            } catch (StatusRuntimeException e) {
                listOfCodes.add(e.getStatus().getCode());
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        assertEquals(0, listOfCodes.size());
    }

    // @Test // not working. it is hanging forever
    public void greeterUnaryCallWithSmallDeadline() {
        List<Pair<String, String>> people = Arrays.asList(
                new Pair("Felipe", "Gutierrez")
        );
        List<Status.Code> listOfCodes = new ArrayList<Status.Code>();
        GreetServiceGrpc.GreetServiceBlockingStub blockingStub = GreetServiceGrpc.newBlockingStub(grpcServerRule.getChannel());

        people.forEach(person -> {
            try {
                // create the protocol buffer message Greeting
                Greeting greeting = Greeting.newBuilder()
                        .setFirstName(person.x)
                        .setLastName(person.y)
                        .build();
                // create a greeting request with the protocol buffer greeting message
                GreetWithDeadlineRequest request = GreetWithDeadlineRequest.newBuilder()
                        .setGreeting(greeting)
                        .build();
                System.out.println("Sending message: " + greeting.toString());
                // call the gRPC and get back a protocol buffer GreetingResponse
                GreetWithDeadlineResponse greetResponse = blockingStub
                        .withDeadline(Deadline.after(1, TimeUnit.MILLISECONDS))
                        .greetWithDeadline(request);
                System.out.println("Hello from server with deadline: " + greetResponse.getResult());
            } catch (StatusRuntimeException e) {
                listOfCodes.add(e.getStatus().getCode());
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println(listOfCodes.size());
        assertEquals(1, listOfCodes.size());
        listOfCodes.forEach(code -> {
            assertEquals(code, Status.Code.DEADLINE_EXCEEDED);
        });
    }
}
