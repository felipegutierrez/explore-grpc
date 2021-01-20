package org.github.felipegutierrez.explore.grpc.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.github.felipegutierrez.explore.grpc.blog.*;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private final MongoClient mongoClient = MongoClients.create("mongodb://root:rootpassword@localhost:27017");
    private final MongoDatabase mongoDatabase = mongoClient.getDatabase("mydb");
    private final MongoCollection<Document> collection = mongoDatabase.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {
        System.out.println("received create blog request");
        Blog blog = request.getBlog();
        Document document = new Document("author_id", blog.getAuthorId())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

        System.out.println("insert (create) a document in mongodb");
        collection.insertOne(document);

        String id = document.getObjectId("_id").toString();
        System.out.println("retrieve the mongodb ID [" + id + "] generated");

        CreateBlogResponse response = CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build())
                .build();

        // send the response to the client
        responseObserver.onNext(response);
        // say to the client that the entry was successfully added to the blog
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        System.out.println("received read blog request");
        String blogId = request.getBlogId();

        System.out.println("searching blog request");
        Document result = null;
        try {
            result = collection.find(eq("_id", new ObjectId(blogId))).first();

            if (result == null) {
                System.out.println("blog not found");
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("The blog with id " + blogId + " was not found.")
                        .asRuntimeException());
            } else {
                System.out.println("blog found");
                Blog blog = Blog.newBuilder()
                        .setAuthorId(result.getString("author_id"))
                        .setTitle(result.getString("title"))
                        .setContent(result.getString("content"))
                        .setId(blogId)
                        .build();
                responseObserver.onNext(ReadBlogResponse.newBuilder().setBlog(blog).build());
                responseObserver.onCompleted();
            }
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("The blog with id " + blogId + " was not found.")
                    .augmentDescription(e.getLocalizedMessage())
                    .asRuntimeException());
        }
    }
}
