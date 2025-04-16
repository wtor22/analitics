package quartztop.analitics.configs.securityConfigs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        String userName = authentication.getName();
        LocalDateTime time = LocalDateTime.now();
        log.warn("\uD83D\uDD25  Выполнен вход пользователя: {} время: {}",userName, time);

        // Редирект после логина
        response.sendRedirect("/");

    }
}
