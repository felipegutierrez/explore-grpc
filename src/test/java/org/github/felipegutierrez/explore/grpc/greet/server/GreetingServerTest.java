package org.github.felipegutierrez.explore.grpc.greet.server;

import io.grpc.testing.GrpcServerRule;
import org.github.felipegutierrez.explore.grpc.greet.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

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
        System.out.println(result);
        System.out.println(sum);
        assertEquals("Hello " + firstName + " " + lastName + ". Welcome to gRPC!", result);
        assertEquals("The sum is: 30 =)", sum);
    }
}
