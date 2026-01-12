This repository contains java spring boot project implemention

Use below commands to test the controllers endpoints:

Invoke-WebRequest -Method Post -Uri "http://localhost:8080/api/v1/sales/health"
Invoke-WebRequest -Method Post -Uri "http://localhost:8080/api/v1/sales/init-sample-data"
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/sales/calculate" -ContentType "application/json" -Body '{"clientId":"IND001","products":[{"productType":"HIGH_END_PHONE","quantity":2}]}'
Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/v1/sales/clients/IND001"
Invoke-WebRequest -Method Post -Uri "http://localhost:8080/api/v1/sales/test"