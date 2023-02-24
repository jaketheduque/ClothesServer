package me.jaketheduque.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan("me.jaketheduque")
@EnableJpaRepositories("me.jaketheduque.sql")
@EntityScan("me.jaketheduque.data")
@SpringBootApplication
public class ClothesIndexerServer {

	public static void main(String[] args) {
		SpringApplication.run(ClothesIndexerServer.class, args);
	}

}
