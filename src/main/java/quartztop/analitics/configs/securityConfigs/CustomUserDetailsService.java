package quartztop.analitics.configs.securityConfigs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import quartztop.analitics.models.users.UserEntity;
import quartztop.analitics.repositories.user.UserRepositories;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositories userRepositories;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optUser = userRepositories.findByEmailIgnoreCase(username);

        if(optUser.isEmpty()) {
            log.warn("Попытка входа с несуществующим email: {}", username);
            throw new UsernameNotFoundException("Пользователь не найден");
        }

        UserEntity user = optUser.get();
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getUsersRoles().getRole());

        return new User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority)
        );
    }
}
