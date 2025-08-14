package proofit.busticket.dto;

import lombok.Getter;
import lombok.Setter;
import proofit.busticket.model.PassengerTypeEnum;

import java.util.List;

@Getter
@Setter
public class PassengerPricing {
    private PassengerTypeEnum type;
    private double seatPrice;
    private List<Double> luggagePrices;
    private double totalPrice;

    public PassengerPricing(PassengerTypeEnum type, double seatPrice, List<Double> luggagePrices, double totalPrice) {
        this.type = type;
        this.seatPrice = seatPrice;
        this.luggagePrices = luggagePrices;
        this.totalPrice = totalPrice;
    }
}