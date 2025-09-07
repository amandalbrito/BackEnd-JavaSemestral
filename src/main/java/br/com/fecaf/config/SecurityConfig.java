package br.com.fecaf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Ativa CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Desativa CSRF (não usado em APIs REST)
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Regras de autorização
                .authorizeHttpRequests(auth -> auth
                        // Permite todas as requisições OPTIONS (preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()

                        // Permite tudo temporariamente (ajuste depois)
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Lista de origens permitidas (frontend)
        config.setAllowedOrigins(Arrays.asList(
                "https://fila-free.vercel.app" // produção
                /*"http://localhost:3000",        // React local
                "http://127.0.0.1:3000",
                "http://localhost:5500",        // Live Server local
                "http://127.0.0.1:5500"*/
        ));

        // Métodos permitidos
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos (necessário Authorization, Content-Type, etc.)
        config.setAllowedHeaders(Arrays.asList("*"));

        // Permite enviar cookies ou headers de autenticação
        config.setAllowCredentials(true);

        // Aplica configuração a todas as rotas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
