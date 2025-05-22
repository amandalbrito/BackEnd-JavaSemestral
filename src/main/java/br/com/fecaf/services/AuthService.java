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

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String autenticar(String email, String senha) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new Exception("Usuário não encontrado");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(senha, user.getSenha())) {
            throw new Exception("Senha incorreta");
        }

        return jwtUtil.gerarToken(email);
    }
}
