package com.lybrate.javaparser.helpers;

import static com.lybrate.javaparser.constants.Constant.*;

import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DatabaseHelper {

	private static MongoClient mClient;

	private MongoClient getMongoClient() {
		if (mClient == null) {
			mClient = new MongoClient(HOST, 27017);
		}
		return mClient;
	}

	// Utility method to get database instance
	private MongoDatabase getOrCreateDB(String dbName) {
		return getMongoClient().getDatabase(dbName);
	}

	// Utility method to get collection
	private MongoCollection<Document> getOrCreateCollection(String dbName, String collectionName) {
		return getOrCreateDB(dbName).getCollection(collectionName);
	}

	// Read all documents from collection
	public FindIterable<Document> queryCollection(String dbName, String collectionName) {
		return getOrCreateCollection(dbName, collectionName).find();
	}

	// Insert documents in collection
	public void insertData(List<Document> documents, String dbName, String collectionName) {
		getOrCreateCollection(dbName, collectionName).drop();
		getOrCreateCollection(dbName, collectionName).insertMany(documents);
	}

}
