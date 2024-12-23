package org.example.hacaton.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель для входа или обновления пользователя")
public class AuthRequest {

    @Email(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]+$", message = "email должен иметь формат адреса электронной почты")
    @Size(max = 255, message = "Должно иметь не больше 255 символов")
    @Schema(description = "электронный адрес пользователя. Не заполняется, передаётся как было", example = "Ivan@mail.com", accessMode = Schema.AccessMode.READ_ONLY)
    private String email;

    @Schema(description = "пароль для входа")
    private String password;

}
