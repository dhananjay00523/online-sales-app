package com.company.onlinesalesapp.adapters.web;

import com.company.onlinesalesapp.application.dto.request.CartCalculationRequest;
import com.company.onlinesalesapp.application.dto.request.ProductQuantity;
import com.company.onlinesalesapp.application.dto.response.CartCalculationResponse;
import com.company.onlinesalesapp.application.dto.response.ItemDetail;
import com.company.onlinesalesapp.application.CartCalculation;
import com.company.onlinesalesapp.domain.model.*;
import com.company.onlinesalesapp.domain.port.ClientRepository;
import com.company.onlinesalesapp.domain.service.OnlineSalesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartCalculation Tests")
class CartCalculationTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private OnlineSalesService onlineSalesService;

    private CartCalculation service;

    private IndividualClient individualClient;
    private ProfessionalClient professionalClientHighRevenue;
    private ProfessionalClient professionalClientLowRevenue;

    @BeforeEach
    void setUp() {
        service = new CartCalculation(clientRepository, onlineSalesService);

        // Setup test clients
        individualClient = new IndividualClient("IND001", "John", "Doe");

        professionalClientHighRevenue = new ProfessionalClient(
                "PRO001",
                "Tech Corp",
                Optional.of("VAT123"),
                "REG456",
                Money.of(15_000_000)
        );

        professionalClientLowRevenue = new ProfessionalClient(
                "PRO002",
                "Small Business",
                Optional.empty(),
                "REG789",
                Money.of(5_000_000)
        );
    }

    @Test
    @DisplayName("Should calculate cart total for individual client with single product")
    void shouldCalculateCartForIndividualClientWithSingleProduct() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "IND001",
                List.of(new ProductQuantity("HIGH_END_PHONE", 2))
        );

        when(clientRepository.findById("IND001")).thenReturn(Optional.of(individualClient));
        when(onlineSalesService.calculateCartTotal(eq(individualClient), anyList()))
                .thenReturn(Money.of(3000)); // 2 × €1500

        // Act
        CartCalculationResponse response = service.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("IND001", response.clientId());
        assertEquals("INDIVIDUAL", response.clientType());
        assertEquals(new BigDecimal("3000.00"), response.totalAmount());
        assertEquals(1, response.items().size());

        ItemDetail item = response.items().get(0);
        assertEquals("HIGH_END_PHONE", item.productType());
        assertEquals(2, item.quantity());
        assertEquals(new BigDecimal("1500.00"), item.unitPrice());
        assertEquals(new BigDecimal("3000.00"), item.lineTotal());

        // Verify interactions
        verify(clientRepository).findById("IND001");
        verify(onlineSalesService).calculateCartTotal(eq(individualClient), anyList());
    }

    @Test
    @DisplayName("Should calculate cart total for individual client with multiple products")
    void shouldCalculateCartForIndividualClientWithMultipleProducts() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "IND001",
                List.of(
                        new ProductQuantity("HIGH_END_PHONE", 2),
                        new ProductQuantity("MID_RANGE_PHONE", 3),
                        new ProductQuantity("LAPTOP", 1)
                )
        );

        when(clientRepository.findById("IND001")).thenReturn(Optional.of(individualClient));
        when(onlineSalesService.calculateCartTotal(eq(individualClient), anyList()))
                .thenReturn(Money.of(7800)); // 2×1500 + 3×800 + 1×1200 = 7800

        // Act
        CartCalculationResponse response = service.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("IND001", response.clientId());
        assertEquals("INDIVIDUAL", response.clientType());
        assertEquals(3, response.items().size());
        assertEquals(new BigDecimal("7800.00"), response.totalAmount());

        // Verify each item detail
        List<ItemDetail> items = response.items();

        // Item 1: HIGH_END_PHONE
        assertEquals("HIGH_END_PHONE", items.get(0).productType());
        assertEquals(2, items.get(0).quantity());
        assertEquals(new BigDecimal("1500.00"), items.get(0).unitPrice());
        assertEquals(new BigDecimal("3000.00"), items.get(0).lineTotal());

        // Item 2: MID_RANGE_PHONE
        assertEquals("MID_RANGE_PHONE", items.get(1).productType());
        assertEquals(3, items.get(1).quantity());
        assertEquals(new BigDecimal("800.00"), items.get(1).unitPrice());
        assertEquals(new BigDecimal("2400.00"), items.get(1).lineTotal());

        // Item 3: LAPTOP
        assertEquals("LAPTOP", items.get(2).productType());
        assertEquals(1, items.get(2).quantity());
        assertEquals(new BigDecimal("1200.00"), items.get(2).unitPrice());
        assertEquals(new BigDecimal("1200.00"), items.get(2).lineTotal());
    }

    @Test
    @DisplayName("Should calculate cart total for professional client with high revenue")
    void shouldCalculateCartForProfessionalHighRevenue() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "PRO001",
                List.of(
                        new ProductQuantity("HIGH_END_PHONE", 5),
                        new ProductQuantity("LAPTOP", 2)
                )
        );

        when(clientRepository.findById("PRO001"))
                .thenReturn(Optional.of(professionalClientHighRevenue));
        when(onlineSalesService.calculateCartTotal(eq(professionalClientHighRevenue), anyList()))
                .thenReturn(Money.of(6800)); // 5×1000 + 2×900 = 6800

        // Act
        CartCalculationResponse response = service.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("PRO001", response.clientId());
        assertEquals("PROFESSIONAL", response.clientType());
        assertEquals(new BigDecimal("6800.00"), response.totalAmount());
        assertEquals(2, response.items().size());

        // Verify professional high revenue pricing
        ItemDetail phone = response.items().get(0);
        assertEquals(new BigDecimal("1000.00"), phone.unitPrice()); // High revenue price
        assertEquals(new BigDecimal("5000.00"), phone.lineTotal());

        ItemDetail laptop = response.items().get(1);
        assertEquals(new BigDecimal("900.00"), laptop.unitPrice()); // High revenue price
        assertEquals(new BigDecimal("1800.00"), laptop.lineTotal());
    }

    @Test
    @DisplayName("Should calculate cart total for professional client with low revenue")
    void shouldCalculateCartForProfessionalLowRevenue() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "PRO002",
                List.of(new ProductQuantity("MID_RANGE_PHONE", 10))
        );

        when(clientRepository.findById("PRO002"))
                .thenReturn(Optional.of(professionalClientLowRevenue));
        when(onlineSalesService.calculateCartTotal(eq(professionalClientLowRevenue), anyList()))
                .thenReturn(Money.of(6000)); // 10×600 = 6000

        // Act
        CartCalculationResponse response = service.execute(request);

        // Assert
        assertNotNull(response);
        assertEquals("PRO002", response.clientId());
        assertEquals("PROFESSIONAL", response.clientType());
        assertEquals(new BigDecimal("6000.00"), response.totalAmount());

        ItemDetail item = response.items().get(0);
        assertEquals(new BigDecimal("600.00"), item.unitPrice()); // Low revenue price
        assertEquals(new BigDecimal("6000.00"), item.lineTotal());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when client not found")
    void shouldThrowExceptionWhenClientNotFound() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "INVALID_CLIENT",
                List.of(new ProductQuantity("HIGH_END_PHONE", 1))
        );

        when(clientRepository.findById("INVALID_CLIENT")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(request)
        );

        assertEquals("Client not found: INVALID_CLIENT", exception.getMessage());

        // Verify service was never called
        verify(onlineSalesService, never()).calculateCartTotal(any(), any());
    }

    @Test
    @DisplayName("Should throw exception for invalid product type")
    void shouldThrowExceptionForInvalidProductType() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "IND001",
                List.of(new ProductQuantity("INVALID_PRODUCT", 1))
        );

        when(clientRepository.findById("IND001")).thenReturn(Optional.of(individualClient));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.execute(request));
    }

    @Test
    @DisplayName("Should throw exception when request is null")
    void shouldThrowExceptionWhenRequestIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> service.execute(null));
    }

    @Test
    @DisplayName("Should handle all product types in single cart")
    void shouldHandleAllProductTypes() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "IND001",
                List.of(
                        new ProductQuantity("HIGH_END_PHONE", 1),
                        new ProductQuantity("MID_RANGE_PHONE", 1),
                        new ProductQuantity("LAPTOP", 1)
                )
        );

        when(clientRepository.findById("IND001")).thenReturn(Optional.of(individualClient));
        when(onlineSalesService.calculateCartTotal(eq(individualClient), anyList()))
                .thenReturn(Money.of(3500)); // 1500 + 800 + 1200

        // Act
        CartCalculationResponse response = service.execute(request);

        // Assert
        assertEquals(3, response.items().size());
        assertEquals(new BigDecimal("3500.00"), response.totalAmount());
    }

   // Integration Tests

    @Test
    @DisplayName("Should determine correct client type for individual client")
    void shouldDetermineClientTypeForIndividual() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "IND001",
                List.of(new ProductQuantity("HIGH_END_PHONE", 1))
        );

        when(clientRepository.findById("IND001")).thenReturn(Optional.of(individualClient));
        when(onlineSalesService.calculateCartTotal(any(), any())).thenReturn(Money.of(1500));

        // Act
        CartCalculationResponse response = service.execute(request);

        // Assert
        assertEquals("INDIVIDUAL", response.clientType());
    }

    @Test
    @DisplayName("Should determine correct client type for professional client")
    void shouldDetermineClientTypeForProfessional() {
        // Arrange
        CartCalculationRequest request = new CartCalculationRequest(
                "PRO001",
                List.of(new ProductQuantity("HIGH_END_PHONE", 1))
        );

        when(clientRepository.findById("PRO001"))
                .thenReturn(Optional.of(professionalClientHighRevenue));
        when(onlineSalesService.calculateCartTotal(any(), any())).thenReturn(Money.of(1000));

        // Act
        CartCalculationResponse response = service.execute(request);

        // Assert
        assertEquals("PROFESSIONAL", response.clientType());
    }
}