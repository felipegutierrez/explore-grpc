package org.github.felipegutierrez.explore.grpc.dummy;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.24.0)",
    comments = "Source: dummy.proto")
public final class DummyServiceGrpc {

  private DummyServiceGrpc() {}

  public static final String SERVICE_NAME = "DummyService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<org.github.felipegutierrez.explore.grpc.dummy.DummyMessage,
      org.github.felipegutierrez.explore.grpc.dummy.DummyMessage> getDummyHelloMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DummyHello",
      requestType = org.github.felipegutierrez.explore.grpc.dummy.DummyMessage.class,
      responseType = org.github.felipegutierrez.explore.grpc.dummy.DummyMessage.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<org.github.felipegutierrez.explore.grpc.dummy.DummyMessage,
      org.github.felipegutierrez.explore.grpc.dummy.DummyMessage> getDummyHelloMethod() {
    io.grpc.MethodDescriptor<org.github.felipegutierrez.explore.grpc.dummy.DummyMessage, org.github.felipegutierrez.explore.grpc.dummy.DummyMessage> getDummyHelloMethod;
    if ((getDummyHelloMethod = DummyServiceGrpc.getDummyHelloMethod) == null) {
      synchronized (DummyServiceGrpc.class) {
        if ((getDummyHelloMethod = DummyServiceGrpc.getDummyHelloMethod) == null) {
          DummyServiceGrpc.getDummyHelloMethod = getDummyHelloMethod =
              io.grpc.MethodDescriptor.<org.github.felipegutierrez.explore.grpc.dummy.DummyMessage, org.github.felipegutierrez.explore.grpc.dummy.DummyMessage>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DummyHello"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.github.felipegutierrez.explore.grpc.dummy.DummyMessage.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  org.github.felipegutierrez.explore.grpc.dummy.DummyMessage.getDefaultInstance()))
              .setSchemaDescriptor(new DummyServiceMethodDescriptorSupplier("DummyHello"))
              .build();
        }
      }
    }
    return getDummyHelloMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DummyServiceStub newStub(io.grpc.Channel channel) {
    return new DummyServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DummyServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new DummyServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DummyServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new DummyServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class DummyServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void dummyHello(org.github.felipegutierrez.explore.grpc.dummy.DummyMessage request,
        io.grpc.stub.StreamObserver<org.github.felipegutierrez.explore.grpc.dummy.DummyMessage> responseObserver) {
      asyncUnimplementedUnaryCall(getDummyHelloMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getDummyHelloMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                org.github.felipegutierrez.explore.grpc.dummy.DummyMessage,
                org.github.felipegutierrez.explore.grpc.dummy.DummyMessage>(
                  this, METHODID_DUMMY_HELLO)))
          .build();
    }
  }

  /**
   */
  public static final class DummyServiceStub extends io.grpc.stub.AbstractStub<DummyServiceStub> {
    private DummyServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DummyServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DummyServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DummyServiceStub(channel, callOptions);
    }

    /**
     */
    public void dummyHello(org.github.felipegutierrez.explore.grpc.dummy.DummyMessage request,
        io.grpc.stub.StreamObserver<org.github.felipegutierrez.explore.grpc.dummy.DummyMessage> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDummyHelloMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DummyServiceBlockingStub extends io.grpc.stub.AbstractStub<DummyServiceBlockingStub> {
    private DummyServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DummyServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DummyServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DummyServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public org.github.felipegutierrez.explore.grpc.dummy.DummyMessage dummyHello(org.github.felipegutierrez.explore.grpc.dummy.DummyMessage request) {
      return blockingUnaryCall(
          getChannel(), getDummyHelloMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DummyServiceFutureStub extends io.grpc.stub.AbstractStub<DummyServiceFutureStub> {
    private DummyServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private DummyServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DummyServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new DummyServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<org.github.felipegutierrez.explore.grpc.dummy.DummyMessage> dummyHello(
        org.github.felipegutierrez.explore.grpc.dummy.DummyMessage request) {
      return futureUnaryCall(
          getChannel().newCall(getDummyHelloMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_DUMMY_HELLO = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DummyServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DummyServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_DUMMY_HELLO:
          serviceImpl.dummyHello((org.github.felipegutierrez.explore.grpc.dummy.DummyMessage) request,
              (io.grpc.stub.StreamObserver<org.github.felipegutierrez.explore.grpc.dummy.DummyMessage>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class DummyServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DummyServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return org.github.felipegutierrez.explore.grpc.dummy.DummyProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DummyService");
    }
  }

  private static final class DummyServiceFileDescriptorSupplier
      extends DummyServiceBaseDescriptorSupplier {
    DummyServiceFileDescriptorSupplier() {}
  }

  private static final class DummyServiceMethodDescriptorSupplier
      extends DummyServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DummyServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (DummyServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DummyServiceFileDescriptorSupplier())
              .addMethod(getDummyHelloMethod())
              .build();
        }
      }
    }
    return result;
  }
}
