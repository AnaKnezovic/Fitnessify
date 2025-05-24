# Upute za instalaciju i pokretanje

## Zahtjevi

- Node.js (preporučeno v16 ili novije)
- npm (preporučeno v8 ili novije)
- Java 17 ili novija
- Maven (za backend)
- PostreSQL (baza podataka)

---
## Korištenje PostgreSQL baze
1. Kreiranje postgresql baze podataka pod nazivom npr. fitnessify_db
   
2. U folderu application.properties urediti podatke. Zamijeni TVOJ_USERNAME i TVOJA_LOZINKA sa svojim podacima, a po potrebi prilagodi port (default je 5432).
   
```
spring.datasource.url=jdbc:postgresql://localhost:PORT/fitnessify_db
spring.datasource.username=TVOJ_USERNAME
spring.datasource.password=TVOJA_LOZINKA
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

## Pokretanje backend-a (Spring Boot)

1. U terminalu dođi u backend folder:
```bash
cd fitnessify-backend
```

2. Pokreni aplikaciju:
```bash
./mvnw spring-boot:run
```
ili, ako nemaš wrapper:
```bash
mvn spring-boot:run
```

Backend će biti dostupan na adresi: http://localhost:8080/

## Pokretanje frontend-a (React)
1. U novom terminalu dođi u frontend folder:
```bash
cd fitnessify-frontend
```
2. Instaliraj ovisnosti:
```bash
npm install
```
3. Pokreni aplikaciju:
```bash
npm start
```
Frontend će biti dostupan na adresi: http://localhost:3000/
