package com.inktechs.microservice.forex;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ForexController {

    @Autowired
    private Environment environment;

    @Autowired
    private ExchangeValueRepository repository;

    // http://localhost:8000/currency-exchange/from/USD/to/INR
    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public ExchangeValue retrieveExchangeValue(@PathVariable String from, @PathVariable String to) {

        ExchangeValue exchangeValue = repository.findByFromAndTo(from, to);

        exchangeValue.setPort(Integer.parseInt(environment.getProperty("local.server.port")));

        return exchangeValue;
    }

    // http://localhost:8000/currency-exchanges
    @GetMapping("/get-exchanges")
    public Map<String, Object> getExchanges() {

        List<ExchangeValue> exchangeValue = repository.findAll();

        Map<String, Object> map = new HashMap<>();
        map.put("port", environment.getProperty("local.server.port"));
        map.put("exchangeValue", exchangeValue);

        return map;
    }



}