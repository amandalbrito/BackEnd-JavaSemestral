package br.com.fecaf.controller;

import br.com.fecaf.model.Cadastro;
import br.com.fecaf.services.CadastroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cadastro")
@CrossOrigin(origins = "http://127.0.0.1:5500", allowedHeaders = "*")
public class CadastroController {

    @Autowired
    private CadastroService cadastroService;

    @GetMapping("/listaCadastro")
    public List<Cadastro> listaCadastro() {
        return cadastroService.listaCadastro();
    }

    @PostMapping("/adicionarCadastro")
    public ResponseEntity<Cadastro> salvarCadastro(@RequestBody Cadastro cadastro) {
        Cadastro newCadastro = cadastroService.salvarCadastro(cadastro);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCadastro);
    }

    @DeleteMapping("/deletarCadastro/{id}")
    public ResponseEntity<Void> deletarCadastro(@PathVariable int id) {
        cadastroService.deletarCadastro(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}
