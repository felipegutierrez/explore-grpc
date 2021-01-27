package org.github.felipegutierrez.explore.grpc.dummy.server;

import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.dummy.DummyMessage;
import org.github.felipegutierrez.explore.grpc.dummy.DummyServiceGrpc;

public class DummyServerImpl extends DummyServiceGrpc.DummyServiceImplBase {

    @Override
    public void dummyHello(DummyMessage request, StreamObserver<DummyMessage> responseObserver) {
        super.dummyHello(request, responseObserver);
    }
}
