package utils;

import model.Settings;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class CurrencyFormatter {

    public static String getCurrencyCode() {
        return Settings.getCurrencyCode();
    }

    public static String format(double amountInINR) {
        String targetCode = getCurrencyCode();

        double converted = ExchangeRate.convert(amountInINR, targetCode);

        Locale locale = getLocaleForCurrency(targetCode);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        formatter.setCurrency(Currency.getInstance(targetCode));
        return formatter.format(converted);
    }

    private static Locale getLocaleForCurrency(String code) {
        switch (code) {
            case "USD": return Locale.US;
            case "EUR": return Locale.GERMANY;
            case "GBP": return Locale.UK;
            default: return new Locale("en", "IN");
        }
    }
}