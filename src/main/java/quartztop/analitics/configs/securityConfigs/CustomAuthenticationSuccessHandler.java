package quartztop.analitics.configs.securityConfigs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        //log.info("Successfully authentication: {}", authentication.getName());

        // TODO назначить редиректы попозже


//        if (roles.contains("ROLE_ADMIN")) {
//            response.sendRedirect("/");
//        } else if (roles.contains("ROLE_SUPER_ADMIN")) {
//            response.sendRedirect("/");
//        } else if (roles.contains("ROLE_USER")) {
//            response.sendRedirect("/");
//        }else {
//            response.sendRedirect("/");
//        }
    }
}
