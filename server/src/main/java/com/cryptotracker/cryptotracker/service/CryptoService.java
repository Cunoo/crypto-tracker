package com.cryptotracker.cryptotracker.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cryptotracker.cryptotracker.model.CryptoHistoricalData;
import com.cryptotracker.cryptotracker.model.CryptoPriceData;

@Service
public class CryptoService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private final String COINGECKO_BASE_URL = "https://api.coingecko.com/api/v3";
    
    public List<CryptoPriceData> getTopCryptos(int limit) {
        try {
            String url = COINGECKO_BASE_URL + "/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=" + limit + "&page=1";
            CryptoPriceData[] response = restTemplate.getForObject(url, CryptoPriceData[].class);
            return response != null ? Arrays.asList(response) : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Chyba pri získavaní dát z CoinGecko: " + e.getMessage());
            return createMockData(limit);
        }
    }
    
    public Map<String, Object> getCryptoHistoricalData(String cryptoId, int days) {
        try {
            String url = COINGECKO_BASE_URL + "/coins/" + cryptoId + "/market_chart?vs_currency=usd&days=" + days;
            CryptoHistoricalData response = restTemplate.getForObject(url, CryptoHistoricalData.class);
            
            if (response != null && response.getPrices() != null) {
                List<Map<String, Object>> formattedData = response.getPrices().stream()
                    .map(pricePoint -> {
                        Map<String, Object> point = new HashMap<>();
                        point.put("timestamp", pricePoint.get(0).longValue());
                        point.put("price", pricePoint.get(1));
                        point.put("date", new Date(pricePoint.get(0).longValue()));
                        return point;
                    })
                    .collect(Collectors.toList());
                
                Map<String, Object> result = new HashMap<>();
                result.put("cryptoId", cryptoId);
                result.put("days", days);
                result.put("data", formattedData);
                return result;
            }
        } catch (Exception e) {
            System.err.println("Chyba pri získavaní historických dát: " + e.getMessage());
        }
        
        return createMockHistoricalData(cryptoId, days);
    }
    
    // Záložné mock dáta pre prípad zlyhania API
    private List<CryptoPriceData> createMockData(int limit) {
        List<CryptoPriceData> mockData = new ArrayList<>();
        String[] cryptos = {"bitcoin", "ethereum", "binancecoin", "cardano", "solana"};
        String[] symbols = {"BTC", "ETH", "BNB", "ADA", "SOL"};
        String[] names = {"Bitcoin", "Ethereum", "BNB", "Cardano", "Solana"};
        Double[] prices = {45000.0, 3000.0, 400.0, 1.5, 100.0};
        
        for (int i = 0; i < Math.min(limit, cryptos.length); i++) {
            CryptoPriceData crypto = new CryptoPriceData();
            crypto.setId(cryptos[i]);
            crypto.setSymbol(symbols[i]);
            crypto.setName(names[i]);
            crypto.setCurrentPrice(prices[i] + (Math.random() - 0.5) * prices[i] * 0.1);
            crypto.setPriceChangePercentage24h((Math.random() - 0.5) * 10);
            crypto.setMarketCap((long) (prices[i] * 19000000 * (1 + Math.random())));
            crypto.setTotalVolume((long) (prices[i] * 1000000 * (1 + Math.random())));
            mockData.add(crypto);
        }
        
        return mockData;
    }
    
    private Map<String, Object> createMockHistoricalData(String cryptoId, int days) {
        List<Map<String, Object>> mockData = new ArrayList<>();
        double basePrice = 45000.0; // Bitcoin cena ako základ
        long currentTime = System.currentTimeMillis();
        long dayInMs = 24 * 60 * 60 * 1000L;
        
        for (int i = days; i >= 0; i--) {
            Map<String, Object> point = new HashMap<>();
            long timestamp = currentTime - (i * dayInMs);
            double price = basePrice * (1 + (Math.random() - 0.5) * 0.3); // ±30% variácia
            
            point.put("timestamp", timestamp);
            point.put("price", price);
            point.put("date", new Date(timestamp));
            mockData.add(point);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("cryptoId", cryptoId);
        result.put("days", days);
        result.put("data", mockData);
        return result;
    }
}