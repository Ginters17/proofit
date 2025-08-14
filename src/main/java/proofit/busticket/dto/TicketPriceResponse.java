package proofit.busticket.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketPriceResponse {
    private List<PassengerPricing> passengers;
    private double totalPrice;

    public TicketPriceResponse(List<PassengerPricing> passengers, double totalPrice) {
        this.passengers = passengers;
        this.totalPrice = totalPrice;
    }
}
