package local.leporidaeyellow.infrastructure.qwolic_sql_exporter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class QwolicSqlExporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(QwolicSqlExporterApplication.class, args);
	}
}
