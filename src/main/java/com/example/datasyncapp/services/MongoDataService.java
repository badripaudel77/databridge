package com.example.datasyncapp.services;

import com.example.datasyncapp.dtos.ProviderDTO;
import com.example.datasyncapp.enums.ProviderFieldEnum;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

@Service
public class MongoDataService {
     private final MongoClient mongoClient;

    @Value("${com.example.datasyncapp.mongodb.name}")
    private String mongoDBName;

    MongoCollection<Document> providerMongoCollection;

    Logger logger = LoggerFactory.getLogger(MongoDataService.class);

    @Autowired
    public MongoDataService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public void insertProvidersToCollection(String collectionName, List<ProviderDTO> providerDTOS, int offset, int pageSize) {
        MongoDatabase mongoDatabase = this.mongoClient.getDatabase(mongoDBName);

        boolean collectionExists = mongoClient
                    .getDatabase(mongoDBName)
                    .listCollectionNames()
                    .into(new ArrayList<>())
                    .contains(collectionName);

        if(!collectionExists) {
            mongoDatabase.createCollection(collectionName);
            logger.info("Collection {}  doesn't exist, so creating it.", collectionName);
        }
        MongoCollection<Document> documentMongoCollection = mongoDatabase.getCollection(collectionName);
        // Other ways like MongoTemplate, MongoRepository etc
        for (ProviderDTO providerDTO: providerDTOS) {
            Document document = prepareDocument(providerDTO);
            boolean documentExists = doesDocumentAlreadyExist(document, documentMongoCollection);
            if(documentExists) {
                return;
            }
            logger.info("Data from RDBMS inserted into collection: {}  with size: {}  with offset : {} and page size: {} ", collectionName, providerDTOS.size(), offset, pageSize);
            documentMongoCollection.insertOne(document);
        }
    }

    private Document prepareDocument(ProviderDTO providerDTO) {
        Document document = new Document();
        document.append(ProviderFieldEnum.PROVIDER_ID.getField(), providerDTO.providerId());
        document.append(ProviderFieldEnum.PROVIDER_NAME.getField(), providerDTO.providerName());
        document.append(ProviderFieldEnum.PROVIDER_SPECIALTY.getField(), providerDTO.providerSpecialty());
        document.append(ProviderFieldEnum.PROVIDER_TIN.getField(), providerDTO.providerTIN());
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
         /*
         * Filter for provider name either ("OR" operation)  with first name (eg: Special Provider) or second (Provider 20) and so on.
         * filter = regex("providerName", "(Provider|Special)", "i"); //: Filter for text containing with or operation.
         */
        if(filterDocument != null) {
            filter = or(eq(ProviderFieldEnum.PROVIDER_NAME.getField(), "Special Provider"), eq(ProviderFieldEnum.PROVIDER_NAME.getField(), "Provider 20"));
        }
        documents = filterDocument != null ? providerMongoCollection.find(filter) : providerMongoCollection.find();
        List<ProviderDTO> providerDTOS = new ArrayList<>();
        for (Document document : documents) {
            ProviderDTO providerDTO = new ProviderDTO(document.getLong(ProviderFieldEnum.PROVIDER_ID.getField()),
                    document.getString(ProviderFieldEnum.PROVIDER_NAME.getField()),
                    document.getString(ProviderFieldEnum.PROVIDER_SPECIALTY.getField()),
                    document.getLong(ProviderFieldEnum.PROVIDER_TIN.getField()));
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
