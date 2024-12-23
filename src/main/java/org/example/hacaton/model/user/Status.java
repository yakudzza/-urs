package org.example.hacaton.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Status {
    PENDING("Отправлено"),
    ACCEPTED("Принято"),
    DECLINED("Отклонено");

    private final String value;
}
