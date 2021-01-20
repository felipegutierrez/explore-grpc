package org.github.felipegutierrez.explore.grpc.blog.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.github.felipegutierrez.explore.grpc.blog.Blog;
import org.github.felipegutierrez.explore.grpc.blog.BlogServiceGrpc;
import org.github.felipegutierrez.explore.grpc.blog.CreateBlogRequest;
import org.github.felipegutierrez.explore.grpc.blog.CreateBlogResponse;

public class BlogClient {

    private ManagedChannel channel;

    public static void main(String[] args) {
        BlogClient client = new BlogClient();

        client.createChannel();
        client.createBlog();
        client.closeChannel();
    }

    private void createChannel() {
        System.out.println("BlogClient client - Hello gRPC");
        channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
    }

    private void closeChannel() {
        System.out.println("Shutting down BlogClient");
        channel.shutdown();
    }

    private void createBlog() {
        BlogServiceGrpc.BlogServiceBlockingStub syncBlogClient = BlogServiceGrpc.newBlockingStub(channel);
        Blog blog = Blog.newBuilder()
                .setAuthorId("Felipe")
                .setTitle("Blog title =)")
                .setContent("hello this is my new blog")
                .build();
        CreateBlogRequest blogRequest = CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build();

        CreateBlogResponse blogResponse = syncBlogClient.createBlog(blogRequest);
        System.out.println("received create blog response: " + blogResponse.toString());
    }
}
