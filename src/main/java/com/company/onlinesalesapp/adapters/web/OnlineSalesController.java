package com.company.onlinesalesapp.adapters.web;

import com.company.onlinesalesapp.application.dto.request.CartCalculationRequest;
import com.company.onlinesalesapp.application.dto.request.ProductQuantity;
import com.company.onlinesalesapp.application.dto.response.CartCalculationResponse;
import com.company.onlinesalesapp.application.CartCalculation;
import com.company.onlinesalesapp.domain.model.Client;
import com.company.onlinesalesapp.domain.model.IndividualClient;
import com.company.onlinesalesapp.domain.model.Money;
import com.company.onlinesalesapp.domain.model.ProfessionalClient;
import com.company.onlinesalesapp.domain.port.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/sales")
public class OnlineSalesController {

    private final CartCalculation cartCalculation;
    private final ClientRepository clientRepository;

    public OnlineSalesController(CartCalculation cartCalculation, ClientRepository clientRepository, ClientRepository clientRepository1) {
        this.cartCalculation = cartCalculation;
        this.clientRepository = clientRepository1;
    }

    @PostMapping("/init-sample-data")
    public ResponseEntity<?> initializeSampleData() {
        try {
            // Individual Client
            clientRepository.save(new IndividualClient(
                    "IND001", "John", "Doe"
            ));

            // Professional High Revenue
            clientRepository.save(new ProfessionalClient(
                    "PRO001",
                    "Tech Corp Inc.",
                    Optional.of("VAT123456789"),
                    "REG789012345",
                    Money.of(15_000_000)
            ));

            // Professional Low Revenue
            clientRepository.save(new ProfessionalClient(
                    "PRO002",
                    "Small Business LLC",
                    Optional.empty(),
                    "REG345678901",
                    Money.of(5_000_000)
            ));

            return ResponseEntity.ok(Map.of(
                    "message", "Sample data initialized successfully",
                    "clients", List.of("IND001", "PRO001", "PRO002")
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/calculate")
    public ResponseEntity<?> calculateTotal(
            @RequestBody CartCalculationRequest request
    ) {
        try {
            CartCalculationResponse response =
                    cartCalculation.execute(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "hint", "Make sure client exists. Use POST /init-sample-data to create test clients."
            ));
        }
    }

    @GetMapping("/clients/{clientId}")
    public ResponseEntity<?> getClient(@PathVariable String clientId) {
        Optional<Client> client = clientRepository.findById(clientId);

        if (client.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Client c = client.get();
        if (c instanceof IndividualClient ic) {
            return ResponseEntity.ok(Map.of(
                    "clientId", ic.clientId(),
                    "type", "INDIVIDUAL",
                    "firstName", ic.firstName(),
                    "lastName", ic.lastName()
            ));
        } else if (c instanceof ProfessionalClient pc) {
            return ResponseEntity.ok(Map.of(
                    "clientId", pc.clientId(),
                    "type", "PROFESSIONAL",
                    "companyName", pc.companyName(),
                    "annualRevenue", pc.annualRevenue().amount(),
                    "revenueCategory", pc.hasHighRevenue() ? "HIGH" : "LOW"
            ));
        }

        return ResponseEntity.ok(client);
    }


    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint(
            @RequestParam(defaultValue = "IND001") String clientId,
            @RequestParam(defaultValue = "HIGH_END_PHONE") String productType,
            @RequestParam(defaultValue = "2") int quantity
    ) {
        try {
            CartCalculationRequest request = new CartCalculationRequest(
                    clientId,
                    List.of(new ProductQuantity(productType, quantity))
            );

            CartCalculationResponse response = cartCalculation.execute(request);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "hint", "Client not found. Use POST /api/v1/shopping-cart/init-sample-data to create test clients.",
                    "availableEndpoints", Map.of(
                            "initData", "POST /api/v1/shopping-cart/init-sample-data",
                            "createIndividual", "POST /api/v1/shopping-cart/clients/individual",
                            "createProfessional", "POST /api/v1/shopping-cart/clients/professional"
                    )
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "Shopping Cart API"
        ));
    }
}