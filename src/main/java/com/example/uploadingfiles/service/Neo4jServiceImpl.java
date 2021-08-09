package com.example.uploadingfiles.service;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.stereotype.Service;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.uploadingfiles.parser.ParsingProperties;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.File;

@Service
public class Neo4jServiceImpl implements Neo4jService{

    private final Path rootLocation;

    @Autowired
    public Neo4jServiceImpl(ParsingProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    enum MyRelationshipTypes implements RelationshipType
    {
        PARENT;
    }

    @Override
    public void init(StringModel model){
        GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
        GraphDatabaseService db = dbFactory.newEmbeddedDatabase(rootLocation.toFile());
        try (Transaction tx = db.beginTx()) {
            Node document = db.createNode(Label.label("Document"));
            document.setProperty("Source text", model.getReceivedText());
            Node dataPoint = db.createNode(Label.label("Data point"));
            dataPoint.setProperty("Data point", model.getDefinitions());
            Relationship relationship = dataPoint.createRelationshipTo(document, MyRelationshipTypes.PARENT);

            tx.success();
        }
    }

}
