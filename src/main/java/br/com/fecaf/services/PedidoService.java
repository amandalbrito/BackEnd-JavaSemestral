package br.com.fecaf.services;

import br.com.fecaf.model.*;
import br.com.fecaf.repository.CartRepository;
import br.com.fecaf.repository.PedidoRepository;
import br.com.fecaf.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PedidoService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UserRepository userRepository;

    public Pedido criarPedidoFinalizado(Long userId, String paymentIntentId) {
        Cart carrinho = cartRepository.findByUserIdAndFinalizadoFalse(userId.intValue())
                .orElseThrow(() -> new RuntimeException("Carrinho não encontrado"));

        if (carrinho.getCartItems().isEmpty()) {
            throw new RuntimeException("Carrinho está vazio");
        }

        Pedido pedido = new Pedido();
        pedido.setData(LocalDateTime.now());
        pedido.setUser(carrinho.getUser());
        pedido.setPaymentIntentId(paymentIntentId);

        double total = carrinho.getCartItems().stream()
                .mapToDouble(item -> item.getProduct().getPreco() * item.getQuantity())
                .sum();

        pedido.setValorTotal(total);

        carrinho.getCartItems().forEach(item -> item.setPedido(pedido));
        pedido.setItens(carrinho.getCartItems());

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        carrinho.setFinalizado(true);
        cartRepository.save(carrinho);

        return pedidoSalvo;
    }
}
