package org.github.felipegutierrez.explore.grpc.prime.server;

import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.greet.GreetEveryoneRequest;
import org.github.felipegutierrez.explore.grpc.greet.GreetEveryoneResponse;
import org.github.felipegutierrez.explore.grpc.prime.*;

public class PrimeNumberDecompositeServiceImpl extends PrimeNumberServiceGrpc.PrimeNumberServiceImplBase {
    @Override
    public void decomposite(PrimeNumberRequest request, StreamObserver<PrimeNumberManyTimesResponse> responseObserver) {
        try {
            // extract necessary fields
            SourceNumber sourceNumber = request.getSourceNumber();
            long n = sourceNumber.getValue();
            long k = 2;
            while (n > 1) {
                if (n % k == 0) {
                    // create the response and streaming k to the client
                    PrimeNumberManyTimesResponse response = PrimeNumberManyTimesResponse.newBuilder()
                            .setResult(String.valueOf(k))
                            .build();
                    // send the response
                    responseObserver.onNext(response);

                    // calculate new factor k
                    n = n / k;
                    Thread.sleep(1000L);
                } else {
                    k = k + 1;
                }
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            // complete the gRPC call
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<AverageRequest> longAverage(StreamObserver<AverageResponse> responseObserver) {
        StreamObserver<AverageRequest> requestObserver = new StreamObserver<AverageRequest>() {
            int counter = 0;
            double sum = 0;

            @Override
            public void onNext(AverageRequest value) {
                // client sends a message
                sum += value.getValue();
                counter++;
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                // client processed all messages
                AverageResponse response = AverageResponse.newBuilder()
                        .setResult(sum / counter)
                        .build();
                responseObserver.onNext(response);
            }
        };
        return requestObserver;
    }

    @Override
    public StreamObserver<MaximumRequest> findMaximum(StreamObserver<MaximumResponse> responseObserver) {
        StreamObserver<MaximumRequest> requestObserver = new StreamObserver<MaximumRequest>() {
            int maximum = 0;

            @Override
            public void onNext(MaximumRequest value) {
                if (value.getValue() > maximum) {
                    maximum = value.getValue();
                }
                MaximumResponse response = MaximumResponse.newBuilder()
                        .setResult(maximum)
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
