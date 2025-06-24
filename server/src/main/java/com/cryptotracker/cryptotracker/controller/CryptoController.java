package com.cryptotracker.cryptotracker.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cryptotracker.cryptotracker.model.CryptoPriceData;
import com.cryptotracker.cryptotracker.service.CryptoService;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {
    
    @Autowired
    private CryptoService cryptoService;
    
    @GetMapping("/top/{limit}")
    public ResponseEntity<List<CryptoPriceData>> getTopCryptos(@PathVariable int limit) {
        try {
            List<CryptoPriceData> cryptos = cryptoService.getTopCryptos(limit);
            return ResponseEntity.ok(cryptos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/historical/{cryptoId}")
    public ResponseEntity<Map<String, Object>> getHistoricalData(
            @PathVariable String cryptoId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> historicalData = cryptoService.getCryptoHistoricalData(cryptoId, days);
            return ResponseEntity.ok(historicalData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "Crypto Tracker API",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}