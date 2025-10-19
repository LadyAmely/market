INSERT INTO category (id, name) VALUES ('d3f9c2b1-7e2e-4a9f-8c3a-9f2c1a2b3c4d', 'Akcesoria');

curl -X POST http://localhost:8080/api/v1/products \
-H "Content-Type: application/json" \
-d '{
"name": "Torebka Zara",
"price": 159.00,
"discount": 0.00,
"barcode": "ZARA123456",
"quantity": 50,
"unit": "szt",
"sku": "ZARA-TOR-001",
"vatRate": "23%",
"active": true,
"categoryId": "d3f9c2b1-7e2e-4a9f-8c3a-9f2c1a2b3c4d"
}'

curl -X GET "http://localhost:8080/api/v1/products/d4a69376-ce04-4cfe-be9e-76652c02fb0c?quantity=1" \
--cookie-jar cookies.txt --cookie cookies.txt


curl -X POST "http://localhost:8080/api/v1/receipt/finalize?paymentMethod=Mastercard" \
-o paragon.pdf \
--cookie cookies.txt
