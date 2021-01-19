package org.github.felipegutierrez.explore.grpc.prime.server;

import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.prime.PrimeNumberManyTimesResponse;
import org.github.felipegutierrez.explore.grpc.prime.PrimeNumberRequest;
import org.github.felipegutierrez.explore.grpc.prime.PrimeNumberServiceGrpc;
import org.github.felipegutierrez.explore.grpc.prime.SourceNumber;

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
}
