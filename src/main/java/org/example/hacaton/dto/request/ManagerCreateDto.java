package org.example.hacaton.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerCreateDto {

    private String firstname;

    private String lastname;

    private String patronymic;

    private String phoneNumber;

    @Email
    private String email;

    private String password;

    private String additionalInfo;

}
