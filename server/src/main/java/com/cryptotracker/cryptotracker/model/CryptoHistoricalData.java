package com.cryptotracker.cryptotracker.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoHistoricalData {
    private List<List<Double>> prices;
    
    public CryptoHistoricalData() {}
    
    public CryptoHistoricalData(List<List<Double>> prices) {
        this.prices = prices;
    }
    
    public List<List<Double>> getPrices() {
        return prices;
    }
    
    public void setPrices(List<List<Double>> prices) {
        this.prices = prices;
    }
}
