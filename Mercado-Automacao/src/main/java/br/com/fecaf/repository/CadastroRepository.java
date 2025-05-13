package br.com.fecaf.repository;

import br.com.fecaf.model.Cadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CadastroRepository extends JpaRepository<Cadastro, Integer> {

    Optional<Cadastro> findByEmail(String email);

}
