package service;

import entity.Currency;
import utils.MarkaziyBankCurrencyConversionService;

import java.util.Scanner;

public interface CurrencyConversionService {
    static CurrencyConversionService getInstance(){
        return new MarkaziyBankCurrencyConversionService();
    }

    double getConversionRatio(Currency original,Currency target);



}
