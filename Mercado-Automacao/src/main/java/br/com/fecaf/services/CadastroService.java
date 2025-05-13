package br.com.fecaf.services;

import br.com.fecaf.model.Cadastro;
import br.com.fecaf.repository.CadastroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CadastroService {

    @Autowired
    private CadastroRepository cadastroRepository;

    public List<Cadastro> listaCadastro() {
        return cadastroRepository.findAll();
    }

    public Cadastro salvarCadastro(Cadastro cadastro) {
        return cadastroRepository.save(cadastro);
    }

    public void deletarCadastro(int id) {
        cadastroRepository.deleteById(id);
    }

}
