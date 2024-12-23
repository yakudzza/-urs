package org.example.hacaton.model.hackathon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum State {

    ANNOUNCED("Анносирован"),
    OPENED_REGISTRATION("Открыта регистрация"),
    CLOSED_REGISTRATION("Регистрация закрыта"),
    COMPLETED("Завершён");

    private final String value;
}
