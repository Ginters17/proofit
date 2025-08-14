package proofit.busticket.service.impl;

import org.springframework.stereotype.Service;
import proofit.busticket.exception.RouteNotFoundException;
import proofit.busticket.service.BasePriceService;

import java.util.Map;

@Service
public class BasePriceServiceImpl implements BasePriceService {

    // Hardcoded some destinations and prices for the task
    private static final Map<String, Double> ROUTE_PRICES = Map.of(
            "Vilnius", 10.0,
            "Riga", 12.0,
            "Tallinn", 15.0,
            "Warsaw", 18.0,
            "Berlin", 25.0,
            "Amsterdam", 30.0
    );

    @Override
    public double getBasePrice(String route) {
        if (route == null || route.trim().isEmpty()) {
            throw new IllegalArgumentException("Route cannot be null or empty");
        }

        return ROUTE_PRICES.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(route.trim()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new RouteNotFoundException(route));
    }
}