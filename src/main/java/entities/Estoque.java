package entities;

import java.util.ArrayList;
import java.util.List;

public class Estoque {

    private List<Produto> produtos = new ArrayList<>();

    public List<Produto> listarProdutos() {
        return produtos;
    }

    public boolean adicionarProduto(Produto produto) {
        if (this.produtos.stream().noneMatch(p -> p.getCodigo().equals(produto.getCodigo()))) {
            this.produtos.add(produto);
            return true;
        } else {
            return false;
        }
    }


    public Produto buscarProdutoPorCodigo(String codigoSelecionado) {
        return this.produtos.stream()
                .filter(p -> p.getCodigo().equals(codigoSelecionado))
                .findFirst()
                .orElse(null);
    }

    public void atualizarProduto(Produto produtoSelecionado, Produto produtoAtualizado) {
        produtoAtualizado.setCodigo(produtoSelecionado.getCodigo());

        int indiceAtual = this.produtos.indexOf(produtoSelecionado);
        if (indiceAtual > -1) {
            this.produtos.set(indiceAtual, produtoAtualizado);
        }
    }

    public void removerProduto(Produto produtoSelecionado) {
        this.produtos.remove(produtoSelecionado);
    }

    public void limparProdutos() {
        this.produtos = new ArrayList<>();
    }

    public double calcularPrecoTotal(Double precoOriginal, Double imposto) {
        double percentualImposto = imposto / 100;
        double valorImposto = precoOriginal * percentualImposto;
        double valorTaxado = precoOriginal + valorImposto;
        double percentualLucro = 0.45;
        double valorLucro = precoOriginal * percentualLucro;

        return valorTaxado + valorLucro;
    }
}
