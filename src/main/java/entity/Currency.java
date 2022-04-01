package entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    USD(840),
    EUR(978),
    RUB(643),
    UZS(0);

    private final double id;
}