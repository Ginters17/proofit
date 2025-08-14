package proofit.busticket.controller;

import org.springframework.web.bind.annotation.*;
import proofit.busticket.dto.TicketPriceRequest;
import proofit.busticket.dto.TicketPriceResponse;
import proofit.busticket.service.TicketPriceService;

@RestController
@RequestMapping("/api/tickets")
public class TicketPriceController {

    private final TicketPriceService ticketPriceService;

    public TicketPriceController(TicketPriceService ticketPriceService) {
        this.ticketPriceService = ticketPriceService;
    }

    @PostMapping("/draft-price")
    public TicketPriceResponse getDraftPrice(@RequestBody TicketPriceRequest request) {
        return ticketPriceService.calculateDraftPrice(request);
    }
}
