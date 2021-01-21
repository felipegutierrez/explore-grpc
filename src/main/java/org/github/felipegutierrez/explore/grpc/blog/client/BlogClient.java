package org.github.felipegutierrez.explore.grpc.blog.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.github.felipegutierrez.explore.grpc.blog.*;

public class BlogClient {

    private ManagedChannel channel;

    public static void main(String[] args) {
        BlogClient client = new BlogClient();

        client.createChannel();
        String blogId = client.createBlog("Felipe", "Blog title =)", "hello this is my new blog");
        client.readBlog(blogId);
        client.readBlog("6008690d9ffc137a3ca8f22a");
        client.updateBlog(blogId, "Felipe Oliveira Gutierrez", "Blog new title updated ;)", "hello this is my new blog and I have updated it.");
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

    private String createBlog(String authorId, String title, String content) {
        try {
            BlogServiceGrpc.BlogServiceBlockingStub syncBlogClient = BlogServiceGrpc.newBlockingStub(channel);
            Blog blog = Blog.newBuilder()
                    .setAuthorId(authorId)
                    .setTitle(title)
                    .setContent(content)
                    .build();
            CreateBlogRequest blogRequest = CreateBlogRequest.newBuilder()
                    .setBlog(blog)
                    .build();

            CreateBlogResponse blogResponse = syncBlogClient.createBlog(blogRequest);
            System.out.println("received create blog response: " + blogResponse.toString());
            return blogResponse.getBlog().getId();
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void readBlog(String blogId) {
        try {
            System.out.println("reading blog: " + blogId);
            BlogServiceGrpc.BlogServiceBlockingStub syncBlogClient = BlogServiceGrpc.newBlockingStub(channel);

            ReadBlogRequest blogRequest = ReadBlogRequest.newBuilder()
                    .setBlogId(blogId)
                    .build();

            ReadBlogResponse blogResponse = syncBlogClient.readBlog(blogRequest);
            System.out.println("read blog response: " + blogResponse.toString());
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }

    private void updateBlog(String blogId, String authorId, String title, String content) {
        try {
            System.out.println("updating blog: " + blogId);
            BlogServiceGrpc.BlogServiceBlockingStub syncBlogClient = BlogServiceGrpc.newBlockingStub(channel);

            Blog updateBlog = Blog.newBuilder()
                    .setAuthorId(authorId)
                    .setTitle(title)
                    .setContent(content)
                    .setId(blogId)
                    .build();
            UpdateBlogRequest blogRequest = UpdateBlogRequest.newBuilder()
                    .setBlog(updateBlog)
                    .build();

            System.out.println("sending the blog update request");
            UpdateBlogResponse blogResponse = syncBlogClient.updateBlog(blogRequest);

            System.out.println("received the blog update response: " + blogResponse.toString());
        } catch (StatusRuntimeException e) {
            e.printStackTrace();
        }
    }
}
