##### Set Active Profile

```bash
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.profiles.active=dev"
```

This will read ```application.properties``` file and then replaced by the active
profile file. In this case ```application-dev.properties```. Here **dev** is the active profile.