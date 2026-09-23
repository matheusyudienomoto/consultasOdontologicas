package com.example.demo;

import com.example.demo.controller.LoginInterceptor;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class DemoApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	// Todas as rotas exigem login, exceto a própria tela de login
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginInterceptor())
				.addPathPatterns("/**")
				.excludePathPatterns("/login", "/error", "/css/**", "/js/**");
	}

	// Cria o usuário administrador ao iniciar, caso ainda não exista
	@Bean
	CommandLineRunner criarAdmin(UsuarioService usuarioService,
								 @Value("${app.admin.login}") String login,
								 @Value("${app.admin.senha}") String senha) {
		return args -> usuarioService.criarAdminSeNaoExistir(login, senha);
	}

}
