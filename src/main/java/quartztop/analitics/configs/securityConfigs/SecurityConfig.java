package quartztop.analitics.configs.securityConfigs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // Игнорируем CSRF для DELETE
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/login","/logout") // Отключаем CSRF для /login
//                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/api/**","/registration/**","/registration","/password-reset/**","/403","/error/**","/fonts/**","/images/**","/css/**", "/js/**", "/api/**",
                                        "/order/**", "/users/export","/login","/logout", "/first-registration","/settings").permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/login")
                        .failureHandler(new CustomAuthenticationFailureHandler())
                        .permitAll()
                        .successHandler(customAuthenticationSuccessHandler)
                )
                .userDetailsService(customUserDetailsService)
                .logout(logout ->
                        logout
                                .logoutUrl("/logout")  // URL для logout
                                .logoutSuccessUrl("/")  // URL после успешного logout
                                .invalidateHttpSession(true)  // Инвалидируем сессию
                                .deleteCookies("JSESSIONID")  // Удаляем cookies
                                .permitAll()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling
                                .authenticationEntryPoint((request, response, authException) -> {
                                    // Редирект на 403 для неавторизованных пользователей
                                    response.sendRedirect("/403");
                                })
                                .accessDeniedPage("/403") // для авторизованных, но без прав
                );
        return http.build();
    }
}
