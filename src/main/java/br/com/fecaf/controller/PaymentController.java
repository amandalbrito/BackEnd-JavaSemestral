package br.com.fecaf.controller;

import br.com.fecaf.model.Pedido;
import br.com.fecaf.services.EmailService;
import br.com.fecaf.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stripe")
public class PaymentController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/confirmar-pagamento")
    public ResponseEntity<String> confirmarPagamento(
            @RequestParam String paymentIntentId,
            @RequestParam String destinatario,
            @RequestParam Long pessoa) {

        try {
            System.out.println("Pagamento simulado recebido: " + paymentIntentId);

            Pedido pedido = pedidoService.criarPedidoFinalizado(pessoa, paymentIntentId);

            return ResponseEntity.ok("Pagamento fake confirmado e pedido registrado com sucesso!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao confirmar pagamento fake: " + e.getMessage());
        }
    }
}
