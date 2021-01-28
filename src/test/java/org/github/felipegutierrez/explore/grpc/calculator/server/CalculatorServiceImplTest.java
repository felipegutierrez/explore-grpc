package org.github.felipegutierrez.explore.grpc.calculator.server;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcServerRule;
import org.github.felipegutierrez.explore.grpc.calculator.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class CalculatorServiceImplTest {
    @Rule
    public final GrpcServerRule grpcServerRule = new GrpcServerRule().directExecutor();

    @Before
    public void addService() {
        // Add the service to the in-process server.
        grpcServerRule.getServiceRegistry().addService(new CalculatorServiceImpl());
    }

    @Test
    public void testDecompositionToPrimeNumbers() {
        // create the greeting PrimeNumber client (blocking - synchronous)
        CalculatorServiceGrpc.CalculatorServiceBlockingStub syncClient = CalculatorServiceGrpc.newBlockingStub(grpcServerRule.getChannel());

        List<String> result = new ArrayList<String>();
        List<String> expect = Arrays.asList("2", "2", "5");
        // create the protocol buffer message SourceNumber
        SourceNumber sourceNumber = SourceNumber.newBuilder()
                .setValue(20)
                .build();

        // create a PrimeNumber request with the protocol buffer greeting message
        PrimeNumberRequest request = PrimeNumberRequest.newBuilder()
                .setSourceNumber(sourceNumber)
                .build();

        // call the gRPC and get back a protocol buffer GreetingResponse
        syncClient.decomposite(request)
                .forEachRemaining(primeNumberResponse -> {
                    result.add(primeNumberResponse.getResult());
                    System.out.println(primeNumberResponse.getResult());
                });

        assertEquals(expect.size(), result.size());
        result.forEach(r -> assertTrue(expect.contains(r)));
        expect.forEach(e -> assertTrue(result.contains(e)));
    }

    @Test
    public void testLongAverage() {
        // create the greeting service client (asynchronous)
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(grpcServerRule.getChannel());
        CountDownLatch latch = new CountDownLatch(1);

        final Double[] expect = {4.5};
        final Double[] result = {0.0};
        StreamObserver<AverageRequest> requestObserver = asyncClient.longAverage(new StreamObserver<AverageResponse>() {
            @Override
            public void onNext(AverageResponse value) {
                System.out.println("received response from server: " + value.getResult());
                result[0] = new Double(value.getResult());
            }

            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        });

        IntStream.iterate(0, i -> i + 1).limit(10)
                .forEach(i -> {
                    // create the protocol buffer message Greeting
                    AverageRequest request = AverageRequest.newBuilder().setValue(i).build();
                    System.out.println("sending number " + i);
                    requestObserver.onNext(request);
                });

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(2L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(expect[0], result[0]);
    }

    @Test
    public void testFindMaximum() {
        // create the greeting service client (asynchronous)
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(grpcServerRule.getChannel());
        CountDownLatch latch = new CountDownLatch(1);

        Set<Integer> expect = new HashSet<Integer>(Arrays.asList(1, 3, 40, 50));
        Set<Integer> result = new HashSet<Integer>();

        StreamObserver<MaximumRequest> requestObserver = asyncClient.findMaximum(new StreamObserver<MaximumResponse>() {

            @Override
            public void onNext(MaximumResponse value) {
                result.add(value.getResult());
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

        Arrays.asList(1, 3, 40, 50, 5, 7, 30, 20, 8, 3, 3, 20, 20)
                .forEach(n -> {
                    // create the protocol buffer message Greeting
                    MaximumRequest request = MaximumRequest.newBuilder()
                            .setValue(n)
                            .build();
                    requestObserver.onNext(request);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

        // we tell the server that the client is done sending data
        requestObserver.onCompleted();

        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(expect.size(), result.size());
        result.forEach(r -> assertTrue(expect.contains(r)));
    }

    @Test
    public void testSquareRoot() {
        // create the greeting PrimeNumber client (blocking - synchronous)
        CalculatorServiceGrpc.CalculatorServiceBlockingStub syncClient = CalculatorServiceGrpc.newBlockingStub(grpcServerRule.getChannel());

        List<Double> result = new ArrayList<Double>();
        List<Double> expect = Arrays.asList(2.0, 7.0);

        Arrays.asList(4, 49).forEach(n -> {
            try {
                SquareRootRequest request = SquareRootRequest.newBuilder().setNumber(n).build();
                double numberRoot = syncClient.squareRoot(request).getNumberRoot();
                result.add(numberRoot);
            } catch (StatusRuntimeException e) {
                System.err.println("Got a runtime exception from square root.");
                e.printStackTrace();
            }
        });

        assertEquals(expect.size(), result.size());
        result.forEach(r -> assertTrue(expect.contains(r)));
        expect.forEach(e -> assertTrue(result.contains(e)));
    }
}
