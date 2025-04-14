package quartztop.analitics.configs.securityConfigs;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Value("${security.remember-me.key}")
    private String rememberKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(  "/api/v1/client/report/stock/by-store",
                                        "/api/v1/client/product/bundle/**","/api/v1/bot/stock/search",
                                        "/api/v1/client/product/bundle/**",
                                        "/api/v1/client/demand/**","/images/**","/css/**",
                                        "/js/**", "/favicon.ico", "/login", "/logout").permitAll() // Разрешаем доступ к этим страницам без аутентификации
                                .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                )
                        .formLogin(form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .failureHandler(new CustomAuthenticationFailureHandler())
                                .permitAll()
                                //.successHandler(customAuthenticationSuccessHandler)
                                .defaultSuccessUrl("/", true) // После успешного входа перенаправляем на главную страницу
                )
                .rememberMe(rememberMe ->
                        rememberMe
                                .key(rememberKey) // уникальный ключ для шифрования cookie
                                .tokenValiditySeconds(86400) // время жизни токена в секундах (1 день)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )
                // Остальная конфигурация...
        ;
        return http.build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // Игнорируем CSRF для DELETE
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/login","/logout") // Отключаем CSRF для /login
//                )
//                .authorizeHttpRequests(authorizeRequests ->
//                        authorizeRequests
//
//                                //.requestMatchers("/api/**","/css/**", "/js/**", "/favicon.ico","/login","/logout", "/first-registration","/settings").permitAll()
//                                .anyRequest().authenticated()
//                )
////                .formLogin(form -> form
////                        .loginProcessingUrl("/login")
////                        .failureHandler(new CustomAuthenticationFailureHandler())
////                        .permitAll()
////                        .successHandler(customAuthenticationSuccessHandler)
////                )
//                .userDetailsService(customUserDetailsService)
//                .logout(logout ->
//                        logout
//                                .logoutUrl("/logout")  // URL для logout
//                                .logoutSuccessUrl("/")  // URL после успешного logout
//                                .invalidateHttpSession(true)  // Инвалидируем сессию
//                                .deleteCookies("JSESSIONID")  // Удаляем cookies
//                                .permitAll()
//                )
//                .exceptionHandling(exceptionHandling ->
//                        exceptionHandling
//                                .authenticationEntryPoint((request, response, authException) -> {
//                                    // Редирект на 403 для неавторизованных пользователей
//                                    response.sendRedirect("/403");
//                                })
//                                .accessDeniedPage("/403") // для авторизованных, но без прав
//                );
//        return http.build();
//    }
}
