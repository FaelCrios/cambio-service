package com.tcc.cambioservice.controller;

import com.tcc.cambioservice.entity.Cambio;
import com.tcc.cambioservice.repository.CambioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("cambio-service")
public class CambioController {

    private Logger logger = LoggerFactory.getLogger(CambioController.class);

    @Autowired
    private Environment environment;

    @Autowired
    private CambioRepository repository;


    @GetMapping(value = "/{amount}/{from}/{to}")
    public Cambio getCambio(
            @PathVariable("amount")BigDecimal amount,
            @PathVariable("from") String from,
            @PathVariable("to") String to
            ){

        logger.info("getCambio -> {} {} {}", amount,from, to);
        var port = environment.getProperty("local.server.port");
        var cambio = repository.findByFromAndTo(from, to);
        if(cambio == null) throw new RuntimeException("Currency Unsupported");
        cambio.setEnvironment(port);
        BigDecimal conversionFactor = cambio.getConversionFactor();
        BigDecimal convertedValue = conversionFactor.multiply(amount);
        cambio.setConvertedValue(convertedValue.setScale(2, RoundingMode.CEILING));

        return cambio;
    }

    @GetMapping(value = "teste")
    public String teste(){
        return "Teste";
    }

}
