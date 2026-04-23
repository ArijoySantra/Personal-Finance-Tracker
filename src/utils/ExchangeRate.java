package utils;

import java.util.HashMap;
import java.util.Map;

public class ExchangeRate {
    private static final Map<String, Double> RATES = new HashMap<>();

    static {
        RATES.put("INR", 1.0);
        RATES.put("USD", 0.012);   // 1 INR = 0.012 USD
        RATES.put("EUR", 0.011);   // 1 INR = 0.011 EUR
        RATES.put("GBP", 0.0095);  // 1 INR = 0.0095 GBP
    }


    public static double convert(double amountInINR, String targetCurrency) {
        double rate = RATES.getOrDefault(targetCurrency, 1.0);
        return amountInINR * rate;
    }

    public static double convertToINR(double amount, String fromCurrency) {
        double rate = RATES.getOrDefault(fromCurrency, 1.0);
        return amount / rate;
    }
}