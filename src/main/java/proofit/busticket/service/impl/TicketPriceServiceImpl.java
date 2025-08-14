package proofit.busticket.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import proofit.busticket.dto.TicketPriceRequest;
import proofit.busticket.dto.Passenger;
import proofit.busticket.dto.PassengerPricing;
import proofit.busticket.dto.TicketPriceResponse;
import proofit.busticket.model.PassengerTypeEnum;
import proofit.busticket.service.BasePriceService;
import proofit.busticket.service.TaxRateService;
import proofit.busticket.service.TicketPriceService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketPriceServiceImpl implements TicketPriceService {

    private final BasePriceService basePriceService;
    private final TaxRateService taxRateService;

    private static final double CHILD_DISCOUNT = 0.5;
    private static final double BAG_PRICE_MULTIPLIER = 0.3;

    @Override
    public TicketPriceResponse calculateDraftPrice(TicketPriceRequest request) {
        double basePrice = basePriceService.getBasePrice(request.getRoute());
        Double taxRate = taxRateService.getTaxRate();

        List<PassengerPricing> passengerPricings = request.getPassengers().stream()
                .map(passenger -> calculatePassengerPricing(passenger, basePrice, taxRate))
                .collect(Collectors.toList());

        double total = calculateTotalPrice(passengerPricings);

        return new TicketPriceResponse(passengerPricings, total);
    }

    private PassengerPricing calculatePassengerPricing(Passenger passenger, double basePrice, Double taxRate) {
        double ticketPrice = calculatePassengerPrice(passenger.getType(), basePrice, taxRate);
        List<Double> luggagePrices = calculateLuggagePrices(passenger.getLuggage(), basePrice, taxRate);
        double totalPrice = ticketPrice + luggagePrices.stream().mapToDouble(Double::doubleValue).sum();
        totalPrice = Math.round(totalPrice * 100.0) / 100.0;

        return new PassengerPricing(
                passenger.getType(),
                ticketPrice,
                luggagePrices,
                totalPrice
        );
    }

    private double calculatePassengerPrice(PassengerTypeEnum type, double basePrice, Double taxRate) {
        double price = switch (type) {
            case ADULT -> basePrice;
            case CHILD -> basePrice * CHILD_DISCOUNT;
            default -> throw new IllegalArgumentException("Unknown passenger type: " + type);
        };
        return applyTaxes(price, taxRate);
    }

    private List<Double> calculateLuggagePrices(List<?> luggage, double basePrice, Double taxRate) {
        if (luggage == null) {
            return new ArrayList<>();
        }

        return luggage.stream()
                .map(bag -> applyTaxes(basePrice * BAG_PRICE_MULTIPLIER, taxRate))
                .collect(Collectors.toList());
    }

    private double calculateTotalPrice(List<PassengerPricing> passengerPricings) {
        double totalPrice =  passengerPricings.stream()
                .mapToDouble(PassengerPricing::getTotalPrice)
                .sum();
        return Math.round(totalPrice * 100.0) / 100.0;
    }

    private double applyTaxes(double price, Double taxRate) {
        if (taxRate == null) return price;

        BigDecimal priceDecimal = BigDecimal.valueOf(price);
        BigDecimal taxRateDecimal = BigDecimal.valueOf(taxRate);
        BigDecimal hundredDecimal = BigDecimal.valueOf(100);

        BigDecimal taxedPrice = priceDecimal.add(
                priceDecimal.multiply(taxRateDecimal.divide(hundredDecimal, 4, RoundingMode.HALF_UP))
        );

        return taxedPrice.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}