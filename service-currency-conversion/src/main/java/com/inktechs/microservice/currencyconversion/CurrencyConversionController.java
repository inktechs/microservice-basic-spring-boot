package com.inktechs.microservice.currencyconversion;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;

@RestController
public class CurrencyConversionController {

    @Autowired
    private Environment environment;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CurrencyExchangeServiceProxy proxy;


    // http://localhost:8100//get-currencies/quantity/34
    @GetMapping("/get-currencies/quantity/{quantity}")
    public  Map<String, Object> getCurrencies(@PathVariable BigDecimal quantity) {

        logger.info("{getCurrencies}", quantity);

        ResponseEntity<Object> getCurrencies = new RestTemplate().getForEntity("http://localhost:8000/get-exchanges", Object.class);
        //System.out.println("getCurrencies"+ dfgdfg.getBody());

        Map<String, Object> map = new HashMap<>();
        map.put("app", "service-currency-conversion");
        map.put("port", environment.getProperty("local.server.port"));
        map.put("getCurrencies", getCurrencies.getBody());

        return map;
    }




    // http://localhost:8100/currency-converter/from/USD/to/INR/quantity/34
    @GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrency(@PathVariable String from, @PathVariable String to,
                                                  @PathVariable BigDecimal quantity) {


        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);

        ResponseEntity<CurrencyConversionBean> responseEntity = new RestTemplate().getForEntity(
                "http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class,
                uriVariables);

        CurrencyConversionBean response = responseEntity.getBody();

        logger.info("{convertCurrency}", response);

        return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity,
                quantity.multiply(response.getConversionMultiple()), response.getPort());
    }

    // http://localhost:8100/currency-converter-feign/from/USD/to/INR/quantity/34
    @GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from, @PathVariable String to,
                                                       @PathVariable BigDecimal quantity) {

        CurrencyConversionBean response = proxy.retrieveExchangeValue(from, to);

        logger.info("{convertCurrencyFeign}", response);

        return new CurrencyConversionBean(response.getId(), from, to, response.getConversionMultiple(), quantity,
                quantity.multiply(response.getConversionMultiple()), response.getPort());
    }




}

// https://www.baeldung.com/spring-rest-template-list
//https://www.oodlestechnologies.com/blogs/Learn-To-Make-REST-calls-With-RestTemplate-In-Spring-Boot
