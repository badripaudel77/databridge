package com.example.datasyncapp.services;

import com.example.datasyncapp.dtos.ProviderDTO;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

@Service
public class MongoDataService {
    MongoClient mongoClient;

    @Value("${com.example.datasyncapp.mongodb.name}")
    private String mongoDBName;

    MongoCollection<Document> providerMongoCollection;

    Logger logger = LoggerFactory.getLogger(MongoDataService.class);

    public MongoDataService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void insertProvidersToCollection(String collectionName, List<ProviderDTO> providerDTOList, int offset, int pageSize) {
        MongoDatabase mongoDatabase = this.mongoClient.getDatabase(mongoDBName);

        List<ProviderDTO> providerDTOS = providerDTOList.stream().toList();
        boolean collectionExists = mongoClient.getDatabase(mongoDBName).listCollectionNames()
                .into(new ArrayList<>()).contains(collectionName);

        if(!collectionExists) {
            mongoDatabase.createCollection(collectionName);
            logger.info("Collection {} " + collectionName + " doesn't exist, so creating the collection");
        }
        MongoCollection<Document> documentMongoCollection = mongoDatabase.getCollection(collectionName);
        // Other ways like MongoTemplate, MongoRepository etc
        for (ProviderDTO providerDTO: providerDTOS) {
            Document document = prepareDocument(providerDTO);
            boolean documentExists = doesDocumentAlreadyExist(document, documentMongoCollection);
            if(!documentExists) {
                logger.info("Data from RDBMS inserted into collection " + collectionName + " with size " + providerDTOList.size() +  " and offset = " + offset + " and page size = " + pageSize);
                documentMongoCollection.insertOne(document);
            }
        }
    }

    private Document prepareDocument(ProviderDTO providerDTO) {
        Document document = new Document();
        document.append("providerId", providerDTO.providerId());
        document.append("providerName", providerDTO.providerName());
        document.append("providerSpecialty", providerDTO.providerSpecialty());
        document.append("providerTIN", providerDTO.providerTIN());
        return document;
    }

    /**
     * @param collectionName
     * @return List<ProviderDTO>
     * This reads data from mongodb collection, maps to the ProviderDTO and returns the consolidated list.
     * Use this whenever necessary.
     * fetchDocuments(collectionName, "Special Provider");
     */
    private List<ProviderDTO> fetchDocuments(String collectionName, String filterDocument) {
        MongoDatabase mongoDatabase = this.mongoClient.getDatabase(mongoDBName);
        providerMongoCollection = mongoDatabase.getCollection(collectionName);
        // Map to ProviderDTO
        FindIterable<Document> documents;
        Bson filter = null;
        if(filterDocument != null) {
            /**
             * Filter for provider name either ("OR" operation)  with first name (eg: Special Provider) or second (Provider 20) and so on.
             * filter = regex("providerName", "(Provider|Special)", "i"); //: Filter for text containing with or operation.
             */
            filter = or(eq("providerName", "Special Provider"), eq("providerName", "Provider 20"));
        }
        documents = filterDocument != null ? providerMongoCollection.find(filter) : providerMongoCollection.find();
        List<ProviderDTO> providerDTOS = new ArrayList<>();
        for (Document document : documents) {
            ProviderDTO providerDTO = new ProviderDTO(document.getLong("providerId"), document.getString("providerName"),
                    document.getString("providerSpecialty"), document.getLong("providerTIN"));
            providerDTOS.add(providerDTO);
        }
        return providerDTOS;
    }

    /**
     * @param document
     * @param collection
     * @return boolean
     * This method matches all the fields of the document( not just the _id) and returns true if all the fields match else false.
     * Handly when some data has to be updated in RDBMS and must be reflected in NoSQL
     */
    private boolean doesDocumentAlreadyExist(Document document, MongoCollection<Document> collection) {
        Document existingDocument = collection.find(document).first();
        return existingDocument != null;
    }
}
