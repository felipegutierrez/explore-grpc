package org.github.felipegutierrez.explore.grpc.chat.util;

import io.grpc.stub.StreamObserver;
import org.github.felipegutierrez.explore.grpc.chat.EchoResponse;

import java.io.Serializable;
import java.util.ArrayList;

public class SingletlonChatStreamObserver implements Serializable {

    private static volatile SingletlonChatStreamObserver singletonSoleInstance;
    private static volatile ArrayList<StreamObserver<EchoResponse>> observers;

    private SingletlonChatStreamObserver() {
        //Prevent form the reflection api.
        if (singletonSoleInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SingletlonChatStreamObserver getInstance() {
        if (singletonSoleInstance == null) { //if there is no instance available... create new one
            synchronized (SingletlonChatStreamObserver.class) {
                if (singletonSoleInstance == null) {
                    observers = new ArrayList<StreamObserver<EchoResponse>>();
                    singletonSoleInstance = new SingletlonChatStreamObserver();
                }
            }
        }
        return singletonSoleInstance;
    }

    //Make singleton from serializing and deserialize operation.
    protected SingletlonChatStreamObserver readResolve() {
        return getInstance();
    }

    public void addObserver(StreamObserver<EchoResponse> streamObserver) {
        observers.add(streamObserver);
    }

    public ArrayList<StreamObserver<EchoResponse>> getObservers() {
        return observers;
    }
}
