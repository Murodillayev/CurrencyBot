package utils;

import com.google.gson.Gson;
import entity.Currency;
import lombok.SneakyThrows;
import org.json.JSONObject;
import service.CurrencyConversionService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MarkaziyBankCurrencyConversionService implements CurrencyConversionService {

    @Override
    public double getConversionRatio(Currency original, Currency target) {
        double originalRate = getRate(original);
        double targetRate = getRate(target);
        return originalRate / targetRate;
    }

    @SneakyThrows
    private double getRate(Currency currency) {
        if (currency == Currency.UZS) {
            return 1;
        }
        URL url = new URL(String.format("https://cbu.uz/uz/arkhiv-kursov-valyut/json/%s/time", currency));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject json = new JSONObject(response.toString().replace("[","").replace("]",""));
        return json.getDouble("Rate");

    }


}
