package com.Utp.DesarrolloWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class DesarrolloWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(DesarrolloWebApplication.class, args);
	}

	@Bean
	CommandLineRunner fixDatabaseColumns(JdbcTemplate jdbcTemplate) {
		return args -> {
			try {
				try {
					jdbcTemplate.execute("ALTER TABLE usuarios ADD COLUMN zona VARCHAR(50)");
					System.out.println("✅ Columna 'zona' añadida a 'usuarios' exitosamente.");
				} catch (Exception e) {}

				try {
					jdbcTemplate.execute("ALTER TABLE productos ADD COLUMN IF NOT EXISTS genero VARCHAR(255) DEFAULT 'Caballero'");
					System.out.println("✅ Columna 'genero' parcheada exitosamente.");
				} catch (Exception e) {}

				// Asignar zonas a los repartidores existentes (basado en lo indicado por el usuario)
				jdbcTemplate.execute("UPDATE usuarios SET zona = 'NORTE' WHERE email = 'repartidor@gmail.com'");
				jdbcTemplate.execute("UPDATE usuarios SET zona = 'SUR' WHERE email = 'repartidor2@gmail.com'");
				jdbcTemplate.execute("UPDATE usuarios SET zona = 'ESTE' WHERE email = 'repartidor3@gmail.com'");
				jdbcTemplate.execute("UPDATE usuarios SET zona = 'CENTRO' WHERE email = 'repartidor4@gmail.com'");
				System.out.println("✅ Zonas asignadas a los repartidores exitosamente.");

				// Corregir rol y email de ventas
				try {
					jdbcTemplate.execute("UPDATE usuarios SET email = 'ventas@gmail.com', id_rol = 4 WHERE email = 'vendedor@gmail.com'");
				} catch (Exception e) {
					jdbcTemplate.execute("DELETE FROM usuarios WHERE email = 'vendedor@gmail.com'");
				}
				jdbcTemplate.execute("UPDATE usuarios SET id_rol = 4 WHERE email = 'ventas@gmail.com'");
				System.out.println("✅ Cuenta 'ventas@gmail.com' asignada al rol VENTAS exitosamente.");

			} catch (Exception e) {
				System.err.println("❌ ERROR AL PARCHEAR LA BASE DE DATOS:");
				e.printStackTrace();
				try { jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1"); } catch(Exception ex) {}
			}
		};
	}

}
