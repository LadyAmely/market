# Getting Started

## Run the application

The application was written in java 17 and is run locally on port 8080 using the command:
```
./gradlew bootRun
```
## Run the tests

To run tests, use:

```
./gradlew test
```

## Create the Database with Docker Compose

To start the PostgreSQL database and pgAdmin, run:

```
docker-compose up -d
```

Make sure to create a .env file in your project root and define the following variables:

```
POSTGRES_PASSWORD=your_db_password
POSTGRES_DB=your_db_name
POSTGRES_USER=your_db_username

```

## Step 1: Insert a Category

Use pgAdmin or any SQL client to insert a category manually:

````
INSERT INTO category (id, name) VALUES ('d3f9c2b1-7e2e-4a9f-8c3a-9f2c1a2b3c4d', 'Akcesoria');
````

 ## Step 2: Create a Product

```
curl -X POST http://localhost:8080/api/v1/products \
-H "Content-Type: application/json" \
-d '{
  "name": "Zara Handbag",
  "price": 159.00,
  "discount": 0.00,
  "barcode": "ZARA123456",
  "quantity": 50,
  "unit": "pcs",
  "sku": "ZARA-TOR-001",
  "vatRate": "23%",
  "active": true,
  "categoryId": "d3f9c2b1-7e2e-4a9f-8c3a-9f2c1a2b3c4d"
}'
```

## Step 3: Scan the Product and Store in Session

```
curl -X GET "http://localhost:8080/api/v1/products/d4a69376-ce04-4cfe-be9e-76652c02fb0c?quantity=1" \
--cookie-jar cookies.txt --cookie cookies.txt
```

## Step 4: Finalize Receipt and Download PDF

```
curl -X POST "http://localhost:8080/api/v1/receipt/finalize?paymentMethod=Mastercard" \
-o paragon.pdf \
--cookie cookies.txt
```
