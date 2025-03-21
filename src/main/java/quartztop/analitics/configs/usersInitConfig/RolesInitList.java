package quartztop.analitics.configs.usersInitConfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "init-setting")
public class RolesInitList {
    List<RolesInit> usersRoles;
}
