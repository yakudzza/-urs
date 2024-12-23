package org.example.hacaton.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.hacaton.model.user.TypeDev;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Модель для входа или обновления пользователя")
public class RegisterRequest {
    @Schema(description = "email пользователя")
    @Email(regexp = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]+$", message = "email должен иметь формат адреса электронной почты")
    @Size(max = 255, message = "Должно иметь не больше 255 символов")
    private String email;

    @Pattern(regexp = "^\\+7\\d{10}$", message = "Должно быть в формате +71234567890")
    @Schema(description = "номер телефона пользователя. Не заполняется, передаётся как было", example = "+79522411200", accessMode = Schema.AccessMode.READ_ONLY)
    private String phoneNumber;

    @Schema(description = "пароль пользователя")
    private String password;

    @Schema(description = "имя пользователя")
    private String firstname;

    @Schema(description = "фамилия пользователя")
    private String lastname;

    @Schema(description = "отчество пользователя")
    private String patronymic;

    @Schema(description = "Дополнительная информация, что он хочет рассказать о себе")
    private String additionalInfo;

    @Schema(description = "кто по жизни ты")
    private TypeDev typeDev;
}
