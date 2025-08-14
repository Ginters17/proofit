package proofit.busticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import proofit.busticket.dto.Passenger;
import proofit.busticket.dto.PassengerPricing;
import proofit.busticket.dto.TicketPriceRequest;
import proofit.busticket.dto.TicketPriceResponse;
import proofit.busticket.model.PassengerTypeEnum;
import proofit.busticket.service.BasePriceService;
import proofit.busticket.service.TaxRateService;
import proofit.busticket.service.impl.TicketPriceServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketPriceServiceImplTest {

    @Mock
    private BasePriceService basePriceService;

    @Mock
    private TaxRateService taxRateService;

    private TicketPriceServiceImpl ticketPriceService;

    @BeforeEach
    void setUp() {
        ticketPriceService = new TicketPriceServiceImpl(basePriceService, taxRateService);
    }

    @Test
    void calculateDraftPrice_AdultWithoutLuggage_ShouldCalculateCorrectly() {
        // Given
        when(basePriceService.getBasePrice("TestRoute")).thenReturn(10.0);
        when(taxRateService.getTaxRate()).thenReturn(21.0);

        Passenger adult = new Passenger();
        adult.setType(PassengerTypeEnum.ADULT);
        adult.setLuggage(null);

        TicketPriceRequest request = new TicketPriceRequest();
        request.setRoute("TestRoute");
        request.setPassengers(Collections.singletonList(adult));

        // When
        TicketPriceResponse response = ticketPriceService.calculateDraftPrice(request);

        // Then
        PassengerPricing pricing = response.getPassengers().get(0);
        assertEquals(12.10, pricing.getSeatPrice());
        assertTrue(pricing.getLuggagePrices().isEmpty());
        assertEquals(12.10, pricing.getTotalPrice());
        assertEquals(12.10, response.getTotalPrice());
    }

    @Test
    void calculateDraftPrice_ChildWithoutLuggage_ShouldApply50PercentDiscount() {
        // Given
        when(basePriceService.getBasePrice("TestRoute")).thenReturn(10.0);
        when(taxRateService.getTaxRate()).thenReturn(21.0);

        Passenger child = new Passenger();
        child.setType(PassengerTypeEnum.CHILD);
        child.setLuggage(Collections.emptyList());

        TicketPriceRequest request = new TicketPriceRequest();
        request.setRoute("TestRoute");
        request.setPassengers(Collections.singletonList(child));

        // When
        TicketPriceResponse response = ticketPriceService.calculateDraftPrice(request);

        // Then
        PassengerPricing pricing = response.getPassengers().get(0);
        assertEquals(6.05, pricing.getSeatPrice()); // (10 * 0.5) + 21% tax
        assertTrue(pricing.getLuggagePrices().isEmpty());
        assertEquals(6.05, pricing.getTotalPrice());
    }

    @Test
    void calculateDraftPrice_NoTaxRate_ShouldCalculateWithoutTax() {
        // Given
        when(basePriceService.getBasePrice("TestRoute")).thenReturn(10.0);
        when(taxRateService.getTaxRate()).thenReturn(null);

        Passenger adult = new Passenger();
        adult.setType(PassengerTypeEnum.ADULT);
        adult.setLuggage(Collections.singletonList("bag1"));

        TicketPriceRequest request = new TicketPriceRequest();
        request.setRoute("TestRoute");
        request.setPassengers(Collections.singletonList(adult));

        // When
        TicketPriceResponse response = ticketPriceService.calculateDraftPrice(request);

        // Then
        PassengerPricing pricing = response.getPassengers().get(0);
        assertEquals(10.0, pricing.getSeatPrice());
        assertEquals(3.0, pricing.getLuggagePrices().get(0)); // 10 * 0.3
        assertEquals(13.0, pricing.getTotalPrice());
    }

    @Test
    void calculateDraftPrice_MultiplePassengersWithVaryingLuggage_ShouldCalculateCorrectTotal() {
        // Given
        when(basePriceService.getBasePrice("TestRoute")).thenReturn(20.0);
        when(taxRateService.getTaxRate()).thenReturn(10.0);

        Passenger adult1 = new Passenger();
        adult1.setType(PassengerTypeEnum.ADULT);
        adult1.setLuggage(Collections.singletonList("bag1"));

        Passenger adult2 = new Passenger();
        adult2.setType(PassengerTypeEnum.ADULT);
        adult2.setLuggage(Arrays.asList("bag1", "bag2", "bag3"));

        Passenger child = new Passenger();
        child.setType(PassengerTypeEnum.CHILD);
        child.setLuggage(null);

        TicketPriceRequest request = new TicketPriceRequest();
        request.setRoute("TestRoute");
        request.setPassengers(Arrays.asList(adult1, adult2, child));

        // When
        TicketPriceResponse response = ticketPriceService.calculateDraftPrice(request);

        // Then
        assertEquals(3, response.getPassengers().size());

        // Adult1: 20 + 10% tax = 22, bag: 6 + 10% tax = 6.6, total = 28.6
        PassengerPricing adult1Pricing = response.getPassengers().get(0);
        assertEquals(22.0, adult1Pricing.getSeatPrice());
        assertEquals(6.6, adult1Pricing.getLuggagePrices().get(0));
        assertEquals(28.6, adult1Pricing.getTotalPrice());

        // Adult2: 22, bags: 3 * 6.6 = 19.8, total = 41.8
        PassengerPricing adult2Pricing = response.getPassengers().get(1);
        assertEquals(22.0, adult2Pricing.getSeatPrice());
        assertEquals(3, adult2Pricing.getLuggagePrices().size());
        assertEquals(41.8, adult2Pricing.getTotalPrice());

        // Child: 10 + 10% tax = 11, no bags, total = 11
        PassengerPricing childPricing = response.getPassengers().get(2);
        assertEquals(11.0, childPricing.getSeatPrice());
        assertTrue(childPricing.getLuggagePrices().isEmpty());
        assertEquals(11.0, childPricing.getTotalPrice());

        // Total: 28.6 + 41.8 + 11 = 81.4
        assertEquals(81.4, response.getTotalPrice());
    }

    @Test
    void calculateDraftPrice_EmptyPassengerList_ShouldReturnZeroTotal() {
        // Given
        when(basePriceService.getBasePrice("TestRoute")).thenReturn(10.0);
        when(taxRateService.getTaxRate()).thenReturn(21.0);

        TicketPriceRequest request = new TicketPriceRequest();
        request.setRoute("TestRoute");
        request.setPassengers(Collections.emptyList());

        // When
        TicketPriceResponse response = ticketPriceService.calculateDraftPrice(request);

        // Then
        assertTrue(response.getPassengers().isEmpty());
        assertEquals(0.0, response.getTotalPrice());
    }
}