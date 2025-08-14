package proofit.busticket.dto;

import lombok.Getter;
import lombok.Setter;
import proofit.busticket.model.PassengerTypeEnum;

import java.util.List;

@Getter
@Setter
public class Passenger {
    private PassengerTypeEnum type;
    private List<String> luggage;
}
