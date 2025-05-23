package br.com.fecaf.services;

import br.com.fecaf.model.User;
import br.com.fecaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        String senhaCriptografada = passwordEncoder.encode(user.getSenha());
        user.setSenha(senhaCriptografada);
        return userRepository.save(user);
    }

    public Optional<User> pesquisarUser(String email) {
        return userRepository.findByEmail(email);
    }


    public void deletarUser(int id) {
        userRepository.deleteById(id);
    }

    public boolean resetPassword(String email, String verificationCode, String newPassword) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            String senhaCriptografada = passwordEncoder.encode(newPassword);
            user.setSenha(senhaCriptografada);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
