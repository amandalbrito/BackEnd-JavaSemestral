package br.com.fecaf.services;

import br.com.fecaf.model.User;
import br.com.fecaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; //SecurityConfig

    public List<User> listarUsers() {
        return userRepository.findAll();
    }

    public User salvarUser(User user) {
        // Criptografa a senha ANTES de salvar no banco
        String senhaCriptografada = passwordEncoder.encode(user.getSenha());
        user.setSenha(senhaCriptografada);
        return userRepository.save(user);
    }

    public void deletarUser(int id) {
        userRepository.deleteById(id);
    }
}
