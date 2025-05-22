    package br.com.fecaf.dto;

    public class AddCartRequest {
        private String codigoBarras; // Adiciona o campo para o código de barras
        private int quantidade;
        public String getCodigoBarras() { return codigoBarras; }
        public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    }

