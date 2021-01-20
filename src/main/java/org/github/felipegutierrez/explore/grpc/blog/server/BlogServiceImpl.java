package org.github.felipegutierrez.explore.grpc.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.github.felipegutierrez.explore.grpc.blog.Blog;
import org.github.felipegutierrez.explore.grpc.blog.BlogServiceGrpc;
import org.github.felipegutierrez.explore.grpc.blog.CreateBlogRequest;
import org.github.felipegutierrez.explore.grpc.blog.CreateBlogResponse;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

//    private final MongoCredential credential = MongoCredential.createCredential("root", "mydb", "rootpassword".toCharArray());
//    private final MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017), Arrays.asList(credential));

//    MongoClientURI uri = new MongoClientURI("mongodb://user:passwd@localhost:27017/?authSource=test");
//    MongoClient mongoClient = new MongoClient(uri);

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
}
