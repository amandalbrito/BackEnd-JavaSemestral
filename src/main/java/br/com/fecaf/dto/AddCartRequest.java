package br.com.fecaf.dto;

public class AddCartRequest {
    private int productId;
    private int quantidade;

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
