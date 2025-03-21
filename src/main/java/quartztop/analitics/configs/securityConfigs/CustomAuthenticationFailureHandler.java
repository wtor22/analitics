package quartztop.analitics.configs.securityConfigs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        // Устанавливаем статус 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        //log.error("AUTHENTICATION ERROR: {}", exception.getMessage());
        // Формируем JSON ответ
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Отправляем JSON с сообщением об ошибке
        response.getWriter().write("{\"error\": \" Неверный Email или Пароль \"}");
    }
}
