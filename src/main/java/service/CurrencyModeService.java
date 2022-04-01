package service;

import entity.Currency;
import utils.HashMapCurrencyModeService;


public interface CurrencyModeService {

    static CurrencyModeService getInstance() {
        return new HashMapCurrencyModeService();//TODO nima qilish kerak
    }

    Currency getOrginalCurrency(long chatId);

    Currency getTargetCurrency(long chatId);

    void setOriginalCurrency(long chatId, Currency currency);

    void setTargetCurrency(long chatId,Currency currency);


}
