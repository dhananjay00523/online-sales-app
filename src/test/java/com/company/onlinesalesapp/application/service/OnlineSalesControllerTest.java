package com.company.onlinesalesapp.application.service;

import com.company.onlinesalesapp.adapters.web.OnlineSalesController;
import com.company.onlinesalesapp.application.CartCalculation;
import com.company.onlinesalesapp.application.dto.request.CartCalculationRequest;
import com.company.onlinesalesapp.application.dto.request.ProductQuantity;
import com.company.onlinesalesapp.application.dto.response.CartCalculationResponse;
import com.company.onlinesalesapp.application.dto.response.ItemDetail;
import com.company.onlinesalesapp.domain.port.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OnlineSalesController Unit Tests")
class OnlineSalesControllerTest {

    @Mock
    private CartCalculation cartCalculation;

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private OnlineSalesController controller;

    private CartCalculationResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new CartCalculationResponse(
                "IND001",
                "INDIVIDUAL",
                List.of(
                        new ItemDetail(
                                "HIGH_END_PHONE",
                                2,
                                new BigDecimal("1500.00"),
                                new BigDecimal("3000.00")
                        )
                ),
                new BigDecimal("3000.00")
        );
    }

    @Test
    @DisplayName("POST /calculate - Should return 200 OK with valid request")
    void shouldReturnOkWithValidRequest() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "IND001",
                List.of(new ProductQuantity("HIGH_END_PHONE", 2))
        );

        when(cartCalculation.execute(any(CartCalculationRequest.class)))
                .thenReturn(sampleResponse);

        // Act
        ResponseEntity<?> response = controller.calculateTotal(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CartCalculationResponse body = (CartCalculationResponse) response.getBody();
        assertNotNull(body);
        assertEquals("IND001", body.clientId());
        assertEquals("INDIVIDUAL", body.clientType());
        assertEquals(new BigDecimal("3000.00"), body.totalAmount());
        assertEquals(1, body.items().size());

        verify(cartCalculation, times(1)).execute(any(CartCalculationRequest.class));
    }

    @Test
    @DisplayName("POST /calculate - Should handle professional client")
    void shouldHandleProfessionalClient() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "PRO001",
                List.of(new ProductQuantity("HIGH_END_PHONE", 5))
        );

        CartCalculationResponse professionalResponse = new CartCalculationResponse(
                "PRO001",
                "PROFESSIONAL",
                List.of(
                        new ItemDetail(
                                "HIGH_END_PHONE", 5, new BigDecimal("1000.00"), new BigDecimal("5000.00")
                        )
                ),
                new BigDecimal("5000.00")
        );

        when(cartCalculation.execute(any(CartCalculationRequest.class)))
                .thenReturn(professionalResponse);

        // Act
        ResponseEntity<?> response = controller.calculateTotal(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CartCalculationResponse body = (CartCalculationResponse) response.getBody();
        assertNotNull(body);
        assertEquals("PRO001", body.clientId());
        assertEquals("PROFESSIONAL", body.clientType());
    }

    @Test
    @DisplayName("GET /test - Should return 200 OK with default parameters")
    void shouldReturnOkWithDefaultParameters() {
        // Arrange
        when(cartCalculation.execute(any(CartCalculationRequest.class)))
                .thenReturn(sampleResponse);

        // Act
        ResponseEntity<?> response = controller.testEndpoint("IND001", "HIGH_END_PHONE", 2);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        CartCalculationResponse body = (CartCalculationResponse) response.getBody();
        assertNotNull(body);
        assertEquals("IND001", body.clientId());

        verify(cartCalculation, times(1)).execute(any(CartCalculationRequest.class));
    }


    @Test
    @DisplayName("GET /health - Should return 200 OK with health status")
    void shouldReturnHealthStatus() {
        // Act
        ResponseEntity<Map<String, String>> response = controller.health();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
        assertEquals("Shopping Cart API", response.getBody().get("service"));

        // Verify no service calls were made
        verifyNoInteractions(cartCalculation);
    }
}