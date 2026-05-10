package HaberSitesiSistemi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HaberSitesiSistemiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaberSitesiSistemiApplication.class, args);
	}

}
