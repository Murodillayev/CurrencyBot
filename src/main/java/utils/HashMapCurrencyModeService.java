package utils;

import entity.Currency;
import service.CurrencyModeService;


public class HashMapCurrencyModeService implements CurrencyModeService {

    private Currency orgCurrency = Currency.USD;
    private Currency tarCurrency = Currency.USD;

    @Override
    public Currency getOrginalCurrency(long chatId) {
        return orgCurrency;
    }

    @Override
    public Currency getTargetCurrency(long chatId) {
        return tarCurrency;
    }

    @Override
    public void setOriginalCurrency(long chatId, Currency currency) {
        this.orgCurrency = currency;

    }

    @Override
    public void setTargetCurrency(long chatId, Currency currency) {
        this.tarCurrency = currency;
    }
}
