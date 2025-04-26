package br.com.fecaf.services;

import br.com.fecaf.model.User;
import br.com.fecaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> listarUsers() {
        return userRepository.findAll();
    }

    public User salvarUser(User user) {
        return userRepository.save(user);
    }

    public void deletarUser(int id) {
        userRepository.deleteById(id);
    }

}
