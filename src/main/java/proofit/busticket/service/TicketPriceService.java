package proofit.busticket.service;

import proofit.busticket.dto.TicketPriceRequest;
import proofit.busticket.dto.TicketPriceResponse;

public interface TicketPriceService {
    TicketPriceResponse calculateDraftPrice(TicketPriceRequest request);
}
