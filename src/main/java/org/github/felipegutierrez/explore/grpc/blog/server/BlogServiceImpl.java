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
                responseObserver.onNext(ReadBlogResponse
                        .newBuilder()
                        .setBlog(documentToBlog(result))
                        .build()
                );
                responseObserver.onCompleted();
            }
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("The read blog with id " + blogId + " was aborted.")
                    .augmentDescription(e.getLocalizedMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
        System.out.println("received update blog request");
        Blog newBlog = request.getBlog();
        String blogId = newBlog.getId();

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
                Document replacement = new Document("author_id", newBlog.getAuthorId())
                        .append("title", newBlog.getTitle())
                        .append("content", newBlog.getContent())
                        .append("_id", new ObjectId(blogId));

                System.out.println("Replacing blog in database...");
                collection.replaceOne(eq("_id", result.getObjectId("_id")), replacement);

                System.out.println("Replaced! Sending as a response");
                responseObserver.onNext(UpdateBlogResponse
                        .newBuilder()
                        .setBlog(documentToBlog(replacement))
                        .build()
                );
                responseObserver.onCompleted();
            }
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("The read blog with id " + blogId + " was aborted.")
                    .augmentDescription(e.getLocalizedMessage())
                    .asRuntimeException());
        }
    }

    private Blog documentToBlog(Document document) {
        return Blog.newBuilder()
                .setAuthorId(document.getString("author_id"))
                .setTitle(document.getString("title"))
                .setContent(document.getString("content"))
                .setId(document.getObjectId("_id").toString())
                .build();
    }
}
