### About the Project
_________________________________
- This application is responsible for syncing data from Relational Database (PostgreSQL) to Non-Relational Database (MongoDB & Redis)
- This fetches the data in a batch and inserts to MongoDB and Redis.

##### Set Active Profile

```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=dev"
```

This will read ```application.properties``` file and then replaced by the active
profile file. In this case ```application-dev.properties```. Here **dev** is the active profile.
