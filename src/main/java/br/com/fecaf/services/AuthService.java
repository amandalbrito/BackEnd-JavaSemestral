package br.com.fecaf.services;

import br.com.fecaf.model.User;
import br.com.fecaf.repository.UserRepository;
import br.com.fecaf.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Instancia o PasswordEncoder para usar BCrypt
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String autenticar(String email, String senha) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new Exception("Usuário não encontrado");
        }

        User user = userOpt.get();

        // Usa BCrypt para comparar a senha digitada com a senha hash salva no banco
        if (!passwordEncoder.matches(senha, user.getSenha())) {
            throw new Exception("Senha incorreta");
        }

        // Gera token usando o email do usuário
        return jwtUtil.gerarToken(email);
    }
}
