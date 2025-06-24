package com.cryptotracker.cryptotracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoPriceData {
    private String id;
    private String symbol;
    private String name;
    
    @JsonProperty("current_price")
    private Double currentPrice;
    
    @JsonProperty("price_change_percentage_24h")
    private Double priceChangePercentage24h;
    
    @JsonProperty("market_cap")
    private Long marketCap;
    
    @JsonProperty("total_volume")
    private Long totalVolume;

    // Konštruktory
    public CryptoPriceData() {}

    public CryptoPriceData(String id, String symbol, String name, Double currentPrice, 
                          Double priceChangePercentage24h, Long marketCap, Long totalVolume) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
        this.priceChangePercentage24h = priceChangePercentage24h;
        this.marketCap = marketCap;
        this.totalVolume = totalVolume;
    }

    // Gettery a settery
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getPriceChangePercentage24h() {
        return priceChangePercentage24h;
    }

    public void setPriceChangePercentage24h(Double priceChangePercentage24h) {
        this.priceChangePercentage24h = priceChangePercentage24h;
    }

    public Long getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(Long marketCap) {
        this.marketCap = marketCap;
    }

    public Long getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Long totalVolume) {
        this.totalVolume = totalVolume;
    }
}