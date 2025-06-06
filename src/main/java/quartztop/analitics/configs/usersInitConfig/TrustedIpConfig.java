package quartztop.analitics.configs.usersInitConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "myapp")
public class TrustedIpConfig {
    private String allowedIp;

    public String getAllowedIp() {
        return allowedIp;
    }

    public void setAllowedIp(String allowedIp) {
        this.allowedIp = allowedIp;
    }
}
