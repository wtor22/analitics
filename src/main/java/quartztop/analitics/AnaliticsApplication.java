package quartztop.analitics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AnaliticsApplication {
	public static void main(String[] args) {
		SpringApplication.run(AnaliticsApplication.class, args);
	}
}
