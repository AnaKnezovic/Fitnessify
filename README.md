# Upute za instalaciju i pokretanje

## Zahtjevi

- Node.js (preporučeno v16 ili novije)
- npm (preporučeno v8 ili novije)
- Java 17 ili novija
- Maven (za backend)

---

## Pokretanje backend-a (Spring Boot)

1. U terminalu dođi u backend folder:
```bash
cd fitnessify-backend
```

Pokreni aplikaciju:
```bash
./mvnw spring-boot:run
```
ili, ako nemaš wrapper:
```bash
mvn spring-boot:run
```

Backend će biti dostupan na adresi: http://localhost:8080/

## Pokretanje frontend-a (React)
U novom terminalu dođi u frontend folder:
```bash
cd fitnessify-frontend
```
Instaliraj ovisnosti:
```bash
npm install
```
Pokreni aplikaciju:
```bash
npm start
```
Frontend će biti dostupan na adresi: http://localhost:3000/
