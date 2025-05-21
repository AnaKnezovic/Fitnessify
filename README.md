# Upute za instalaciju i pokretanje

## Zahtjevi

- Node.js (preporučeno v16 ili novije)
- npm (preporučeno v8 ili novije)
- Java 17 ili novija
- Maven (za backend)

---

## Pokretanje backend-a (Spring Boot)

1. U terminalu dođi u backend folder:


Pokretanje backend-a (Spring Boot)
U terminalu dođi u backend folder:

cd fitnessify-backend
Pokreni aplikaciju:

./mvnw spring-boot:run

ili, ako nemaš wrapper:

mvn spring-boot:run

Backend će biti dostupan na adresi:
http://localhost:8080/

Pokretanje frontend-a (React)
U novom terminalu dođi u frontend folder:

cd fitnessify-frontend

Instaliraj ovisnosti:
npm install

Pokreni aplikaciju:
npm start

Frontend će biti dostupan na adresi:
http://localhost:3000/
