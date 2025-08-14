package proofit.busticket.service.impl;

import org.springframework.stereotype.Service;
import proofit.busticket.service.TaxRateService;

@Service
public class TaxRateServiceImpl implements TaxRateService {

    @Override
    public Double getTaxRate() {
        // Hardcoded VAT 21% for the task
        return 21.0;
    }
}
