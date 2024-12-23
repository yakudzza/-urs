package org.example.hacaton.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ADMIN,
    USER,
    MANAGER,
    MEMBER
}