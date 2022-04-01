import entity.Currency;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.CurrencyConversionService;
import service.CurrencyModeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CurrencyBot extends TelegramLongPollingBot {

    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();
    private final CurrencyConversionService currencyConversionService = CurrencyConversionService.getInstance();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
        if (update.hasMessage()) {
            handleMessage(message);
        }
    }

    private Optional<Double> parseDouble(String messageText) {
        try {
            return Optional.of(Double.parseDouble(messageText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @SneakyThrows
    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        Currency newCurrency = Currency.valueOf(param[1]);
        switch (action) {
            case "ORIGINAL" -> currencyModeService.setOriginalCurrency(message.getChatId(), newCurrency);
            case "TARGET" -> currencyModeService.setTargetCurrency(message.getChatId(), newCurrency);
        }
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        Currency orgCurrency = currencyModeService.getOrginalCurrency(message.getChatId());
        Currency tarCurrency = currencyModeService.getTargetCurrency(message.getChatId());

        for (Currency value : Currency.values()) {
            buttons.add(
                    Arrays.asList(
                            InlineKeyboardButton.builder().text(getCurrencyButton(orgCurrency, value)).callbackData("ORIGINAL:" + value).build(),
                            InlineKeyboardButton.builder().text(getCurrencyButton(tarCurrency, value)).callbackData("TARGET:" + value).build()
                    )
            );
        }
        execute(EditMessageReplyMarkup.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                .build());
    }

    @SneakyThrows
    private void handleMessage(Message message) {
        if (message.hasEntities() && message.hasText()) {
            switch (message.getText()){
                case "/start":{
                    execute(
                            SendMessage.builder()
                                    .text("Valyuta kurslari botiga xush kelibsiz!!\n\nBuni bosing\n👇👇")
                                    .chatId(message.getChatId().toString())
                                    .build()
                    );
                    return;
                }
                case "/about_bot":{
                    execute(
                            SendMessage.builder()
                                    .text("Bu bot Markaziy Bank ko`rsatmasiga ko`ra valyuta kursalari haqida ma`lumot beradi\n\nBot haqida e`tiroz, kamchilik, talab va takliflar bo`lsa👉 @MuhammadkomilMurodillayev")
                                    .chatId(message.getChatId().toString())
                                    .build()
                    );
                    return;
                }
            }
            Optional<MessageEntity> commandEntity = message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();

            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case "/set_currency":
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        Currency orgCurrency = currencyModeService.getOrginalCurrency(message.getChatId());
                        Currency tarCurrency = currencyModeService.getTargetCurrency(message.getChatId());
                        for (Currency value : Currency.values()) {
                            buttons.add(
                                    Arrays.asList(
                                            InlineKeyboardButton.builder().text(getCurrencyButton(orgCurrency, value)).callbackData("ORIGINAL:" + value).build(),
                                            InlineKeyboardButton.builder().text(getCurrencyButton(tarCurrency, value)).callbackData("TARGET: " + value).build()
                                    )
                            );
                        }
                        execute(SendMessage.builder()
                                .text("qaysi valyutadan qaysi valyutaga o`tishni tanlang")
                                .chatId(message.getChatId().toString())
                                .replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build())
                                .build());
                }

            }
            return;
        }
        if (message.hasText()) {
            String messageText = message.getText();

            Optional<Double> value = parseDouble(messageText);
            Currency orgCurrency = currencyModeService.getOrginalCurrency(message.getChatId());
            Currency tarCurrency = currencyModeService.getTargetCurrency(message.getChatId());
            double ratio = currencyConversionService.getConversionRatio(orgCurrency, tarCurrency);
            if (value.isPresent()) {
                execute(SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(String.format("%4.2f %s -> %4.2f %s", value.get(), orgCurrency, (value.get() * ratio), tarCurrency))
                        .build());
            }
        }
    }

    private String getCurrencyButton(Currency saved, Currency current) {
        return saved == current ? current.name() + "✅" : current.name();
    }

    @Override
    public String getBotUsername() {
        return "@currency_informationbot";
    }

    @Override
    public String getBotToken() {
        return "5026704624:AAHs4wXGIHzQeHSQWHdtOxGFKLmAE4nTNwo";
    }

}
