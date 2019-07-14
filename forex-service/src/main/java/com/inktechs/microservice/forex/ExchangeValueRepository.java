package com.inktechs.microservice.forex;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeValueRepository extends JpaRepository<ExchangeValue, Long> {

    ExchangeValue findByFromAndTo(String from, String to);

    List<ExchangeValue> findAll();
}