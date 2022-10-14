import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import entities.Estoque;
import entities.Produto;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Locale;
import java.util.Scanner;

public class Program {

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        System.out.println("Bem vindo ao menu!");



        Estoque estoque = new Estoque();
        try (Scanner sc = new Scanner(System.in)) {
            boolean continuar;
            do {
                continuar = exibirMenu(sc, estoque);
            } while (continuar);
        }

        System.out.println(estoque.listarProdutos());
        exportarEstoque(estoque);

        System.out.println("Muito obrigado, volte sempre! ");
    }

    private static void importarEstoqueMostruario(Estoque estoque) throws IOException {
        String nomeArquivo = "mostruario.input.csv";
        System.out.println("Importando arquivo " + nomeArquivo);

        Reader reader = Files.newBufferedReader(Paths.get(nomeArquivo));
        try (CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

            estoque.limparProdutos();

            String [] linhaAtual;
            while ((linhaAtual = csvReader.readNext()) != null) {
                Double precoOriginal = Double.parseDouble(linhaAtual[6].replace(",", "."));
                Double imposto = Double.parseDouble(linhaAtual[7].replace(",", "."));
                double precoTotal = estoque.calcularPrecoTotal(precoOriginal, imposto);

                Produto novoProduto = new Produto(linhaAtual[0], linhaAtual[3], precoTotal, 1, linhaAtual[5]);
                estoque.adicionarProduto(novoProduto);
            }
        }

        exibirListaProdutos(estoque);
    }

    private static void exportarEstoque(Estoque estoque) throws IOException {
        System.out.println(" Salvando estoque...");

        String nomeArquivo = "estoque.output.csv";
        try (CSVWriter writer = new CSVWriter(new FileWriter(nomeArquivo))) {
            writer.writeNext(new String[]{"Codigo", "Nome", "Preco", "Quantidade", "Categoria"});

            for (Produto produto : estoque.listarProdutos()) {
                writer.writeNext(new String[] {
                        produto.getCodigo(),
                        produto.getNome(),
                        String.valueOf(produto.getPreco()),
                        String.valueOf(produto.getQuantidade()),
                        produto.getCategoria()
                });
            }
        }

        System.out.println("Estoque salvo em: " + nomeArquivo);
    }

    private static boolean exibirMenu(Scanner sc, Estoque estoque) throws IOException {
        System.out.println("1: Adicionar novo produto");
        System.out.println("2: Editar produto ");
        System.out.println("3: Remover produto ");
        System.out.println("4: Importar mostruário da fábrica ");
        System.out.println("5: Cancelar ");
        System.out.println("Por gentileza, selecione o número desejado: ");

        int opcaoSelecionada = sc.nextInt();
        if (opcaoSelecionada == 5) return false;

        if (opcaoSelecionada == 1) {
            adicionarProduto(sc, estoque);
        } else if (opcaoSelecionada == 2) {
            editarProduto(sc, estoque);
        } else if (opcaoSelecionada == 3) {
            removerProduto(sc, estoque);
        } else if (opcaoSelecionada == 4) {
            importarEstoqueMostruario(estoque);
        }

        return true;
    }

    private static void removerProduto(Scanner sc, Estoque estoque) {
        if (estoque.listarProdutos().isEmpty()) {
            System.out.println("Não existem produtos a serem removidos!");
            return;
        }

        System.out.println("Deseja realmente remover um produto? s/n");

        char resposta = sc.next().charAt(0);
        if (resposta != 's') return;

        exibirListaProdutos(estoque);

        Produto produtoSelecionado;
        do {
            System.out.println("Digite o código do produto a ser removido: ");
            String codigoSelecionado = sc.next();
            produtoSelecionado = estoque.buscarProdutoPorCodigo(codigoSelecionado);

            if (produtoSelecionado == null) System.out.println("Código inválido!");
        } while (produtoSelecionado == null);

        System.out.println("Produto selecionado: " + produtoSelecionado);
        estoque.removerProduto(produtoSelecionado);
    }

    private static void editarProduto(Scanner sc, Estoque estoque) {
        if (estoque.listarProdutos().isEmpty()) {
            System.out.println("Não existem produtos a serem editados!");
            return;
        }

        System.out.println("Deseja realmente editar um produto? s/n");

        char resposta = sc.next().charAt(0);
        if (resposta != 's') return;

        exibirListaProdutos(estoque);

        Produto produtoSelecionado;
        do {
            System.out.println("Digite o código do produto a ser editado: ");
            String codigoSelecionado = sc.next();
            produtoSelecionado = estoque.buscarProdutoPorCodigo(codigoSelecionado);

            if (produtoSelecionado == null) System.out.println("Código inválido!");
        } while (produtoSelecionado == null);

        System.out.println("Produto selecionado: " + produtoSelecionado);
        Produto produtoAtualizado = pedirDadosProduto(sc);
        estoque.atualizarProduto(produtoSelecionado, produtoAtualizado);
    }

    private static void exibirListaProdutos(Estoque estoque) {
        System.out.println(" Produtos no Estoque: ");
        for (Produto produto : estoque.listarProdutos()) {
            System.out.println(produto);
        }
    }

    private static void adicionarProduto(Scanner sc, Estoque estoque) {
        System.out.println("Deseja realmente adicionar um produto? s/n");

        char resposta = sc.next().charAt(0);
        if (resposta != 's') return;

        Produto novoProduto = pedirDadosProduto(sc);
        boolean adicionado = false;
        do {
            System.out.println("Digite um codigo unico para esse produto: ");
            novoProduto.setCodigo(sc.next());

            adicionado = estoque.adicionarProduto(novoProduto);

            if (!adicionado) System.out.println("Código já existente!");
        } while (!adicionado);
    }

    private static Produto pedirDadosProduto(Scanner sc) {
        Produto novoProduto = new Produto();

        System.out.println("Digite o nome: ");
        novoProduto.setNome(sc.next());

        System.out.println("Digite o preço: ");
        novoProduto.setPreco(sc.nextDouble());

        System.out.println("Digite a quantidade: ");
        novoProduto.setQuantidade(sc.nextInt());

        System.out.println("Digite a categoria: ");
        novoProduto.setCategoria(sc.next());

        return novoProduto;
    }
}
