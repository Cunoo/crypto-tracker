import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer, BarChart, Bar } from 'recharts';
import './App.css';

const API_BASE_URL = 'http://localhost:8080/api/crypto';

function App() {
  const [cryptos, setCryptos] = useState([]);
  const [selectedCrypto, setSelectedCrypto] = useState(null);
  const [historicalData, setHistoricalData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [days, setDays] = useState(30);

  // Načítanie top kryptomien pri spustení
  useEffect(() => {
    fetchTopCryptos();
  }, []);

  // Načítanie historických dát pri zmene vybranej kryptomeny alebo dní
  useEffect(() => {
    if (selectedCrypto) {
      fetchHistoricalData(selectedCrypto.id, days);
    }
  }, [selectedCrypto, days]);

  const fetchTopCryptos = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE_URL}/top/10`);
      setCryptos(response.data);
      if (response.data.length > 0) {
        setSelectedCrypto(response.data[0]);
      }
      setError(null);
    } catch (err) {
      setError('Chyba pri načítavaní kryptomien: ' + err.message);
      console.error('Error fetching cryptos:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchHistoricalData = async (cryptoId, days) => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE_URL}/historical/${cryptoId}?days=${days}`);
      
      // Formátovanie dát pre graf
      const formattedData = response.data.data.map(point => ({
        date: new Date(point.timestamp).toLocaleDateString('sk-SK'),
        price: parseFloat(point.price.toFixed(2)),
        timestamp: point.timestamp
      }));
      
      setHistoricalData(formattedData);
      setError(null);
    } catch (err) {
      setError('Chyba pri načítavaní historických dát: ' + err.message);
      console.error('Error fetching historical data:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('sk-SK', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2,
      maximumFractionDigits: 6
    }).format(price);
  };

  const formatMarketCap = (marketCap) => {
    if (marketCap >= 1e12) {
      return `$${(marketCap / 1e12).toFixed(2)}T`;
    } else if (marketCap >= 1e9) {
      return `$${(marketCap / 1e9).toFixed(2)}B`;
    } else if (marketCap >= 1e6) {
      return `$${(marketCap / 1e6).toFixed(2)}M`;
    }
    return `$${marketCap?.toLocaleString()}`;
  };

  if (loading && cryptos.length === 0) {
    return (
      <div className="app">
        <div className="loading">
          <div className="spinner"></div>
          <p>Načítavam dáta...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>Crypto Tracker</h1>
        <p>Sledovanie kurzov kryptomien v reálnom čase</p>
      </header>

      {error && (
        <div className="error-message">
          <p>⚠️ {error}</p>
          <button onClick={fetchTopCryptos}>Skúsiť znovu</button>
        </div>
      )}

      <div className="main-content">
        {/* Zoznam kryptomien */}
        <div className="crypto-list">
          <h2>Top 10 Kryptomien</h2>
          <div className="crypto-grid">
            {cryptos.map((crypto) => (
              <div
                key={crypto.id}
                className={`crypto-card ${selectedCrypto?.id === crypto.id ? 'selected' : ''}`}
                onClick={() => setSelectedCrypto(crypto)}
              >
                <div className="crypto-info">
                  <h3>{crypto.name} ({crypto.symbol.toUpperCase()})</h3>
                  <p className="price">{formatPrice(crypto.currentPrice)}</p>
                  <p className={`change ${crypto.priceChangePercentage24h >= 0 ? 'positive' : 'negative'}`}>
                    {crypto.priceChangePercentage24h >= 0 ? '↗️' : '↘️'} 
                    {crypto.priceChangePercentage24h?.toFixed(2)}%
                  </p>
                  <p className="market-cap">
                    Tržná kap.: {formatMarketCap(crypto.marketCap)}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Graf historických dát */}
        {selectedCrypto && (
          <div className="chart-section">
            <div className="chart-header">
              <h2>
                Vývoj kurzu {selectedCrypto.name} ({selectedCrypto.symbol.toUpperCase()})
              </h2>
              <div className="time-selector">
                <label>Obdobie: </label>
                <select value={days} onChange={(e) => setDays(parseInt(e.target.value))}>
                  <option value={7}>7 dní</option>
                  <option value={30}>30 dní</option>
                  <option value={90}>90 dní</option>
                  <option value={365}>1 rok</option>
                </select>
              </div>
            </div>

            {loading ? (
              <div className="chart-loading">
                <div className="spinner"></div>
                <p>Načítavam historické dáta...</p>
              </div>
            ) : (
              <div className="chart-container">
                <ResponsiveContainer width="100%" height={400}>
                  <LineChart data={historicalData}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                    <XAxis 
                      dataKey="date" 
                      stroke="#666"
                      tick={{ fontSize: 12 }}
                    />
                    <YAxis 
                      stroke="#666"
                      tick={{ fontSize: 12 }}
                      tickFormatter={(value) => `$${value.toLocaleString()}`}
                    />
                    <Tooltip 
                      formatter={(value) => [formatPrice(value), 'Cena']}
                      labelStyle={{ color: '#333' }}
                      contentStyle={{ 
                        backgroundColor: '#fff', 
                        border: '1px solid #ccc',
                        borderRadius: '8px'
                      }}
                    />
                    <Legend />
                    <Line 
                      type="monotone" 
                      dataKey="price" 
                      stroke="#8884d8" 
                      strokeWidth={2}
                      dot={false}
                      name="Cena (USD)"
                    />
                  </LineChart>
                </ResponsiveContainer>
              </div>
            )}
          </div>
        )}

        {/* Stĺpcový graf pre porovnanie cien */}
        <div className="comparison-chart">
          <h2>Porovnanie aktuálnych cien</h2>
          <div className="chart-container">
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={cryptos.slice(0, 5)}>
                <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
                <XAxis 
                  dataKey="symbol" 
                  stroke="#666"
                  tick={{ fontSize: 12 }}
                />
                <YAxis 
                  stroke="#666"
                  tick={{ fontSize: 12 }}
                  tickFormatter={(value) => `$${value.toLocaleString()}`}
                />
                <Tooltip 
                  formatter={(value) => [formatPrice(value), 'Cena']}
                  labelStyle={{ color: '#333' }}
                  contentStyle={{ 
                    backgroundColor: '#fff', 
                    border: '1px solid #ccc',
                    borderRadius: '8px'
                  }}
                />
                <Bar 
                  dataKey="currentPrice" 
                  fill="#82ca9d"
                  name="Aktuálna cena (USD)"
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

    </div>
  );
}

export default App;