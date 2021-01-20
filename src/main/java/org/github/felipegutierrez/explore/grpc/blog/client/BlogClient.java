package org.github.felipegutierrez.explore.grpc.blog.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.github.felipegutierrez.explore.grpc.blog.*;

public class BlogClient {

    private ManagedChannel channel;

    public static void main(String[] args) {
        BlogClient client = new BlogClient();

        client.createChannel();
        String blogId = client.createBlog();
        client.readBlog(blogId);
        client.readBlog("6008690d9ffc137a3ca8f22a");
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

    private String createBlog() {
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
        return blogResponse.getBlog().getId();
    }

    private void readBlog(String blogId) {
        System.out.println("reading blog: " + blogId);
        BlogServiceGrpc.BlogServiceBlockingStub syncBlogClient = BlogServiceGrpc.newBlockingStub(channel);

        ReadBlogRequest blogRequest = ReadBlogRequest.newBuilder()
                .setBlogId(blogId)
                .build();

        ReadBlogResponse blogResponse = syncBlogClient.readBlog(blogRequest);
        System.out.println("read blog response: " + blogResponse.toString());
    }
}
