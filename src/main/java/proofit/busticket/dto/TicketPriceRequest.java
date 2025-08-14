package proofit.busticket.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TicketPriceRequest {
    private String route;
    private List<Passenger> passengers;
}
