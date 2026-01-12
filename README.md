This repository contains java spring boot project implemention

Use below commands to test the controllers endpoints:

1) /Health -> Invoke-WebRequest -Method Post -Uri "http://localhost:8080/api/v1/sales/health"
2) /init-sample-data -> Invoke-WebRequest -Method Post -Uri "http://localhost:8080/api/v1/sales/init-sample-data"
3) /calculate -> Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/sales/calculate" -ContentType "application/json" -Body '{"clientId":"IND001","products":[{"productType":"HIGH_END_PHONE","quantity":2}]}'
4) /clients/{clientId} -> Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/v1/sales/clients/IND001"
5) /test -> Invoke-WebRequest -Method Post -Uri "http://localhost:8080/api/v1/sales/test"
