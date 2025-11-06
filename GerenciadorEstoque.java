import java.util.ArrayList;
import java.util.List;

public class GerenciadorEstoque {
    private List<Produto> produtosEmEstoque;

    public GerenciadorEstoque() {
        this.produtosEmEstoque = new ArrayList<>();
    }

    public void cadastrarProduto(Produto produto) {
        if (buscarProdutoPorCodigo(produto.getCodigo()) == null) {
            this.produtosEmEstoque.add(produto);
            System.out.println("Cadastro realizado: " + produto.getNome());
        } else {
            System.out.println("Erro: Já existe um produto com o código " + produto.getCodigo());
        }
    }
    
    public void listarProdutos() {
        if (this.produtosEmEstoque.isEmpty()) {
            System.out.println("Nenhum produto cadastrado no estoque.");
            return;
        }

        System.out.println("--- Listagem de Produtos no Estoque ---");
        for (Produto p : this.produtosEmEstoque) {
            System.out.println(p);
        }
        System.out.println("----------------------------------------");
    }
public void darEntradaEstoque(String codigoProduto, int quantidadeAdicionar) {
        Produto produto = buscarProdutoPorCodigo(codigoProduto);

        if (produto != null) {
            produto.adicionarQuantidade(quantidadeAdicionar);
            System.out.println("Entrada de " + quantidadeAdicionar + 
                               " unidades para '" + produto.getNome() + "'. Novo estoque: " + 
                               produto.getQuantidade());
        } else {
            System.out.println("Erro na entrada: Produto com código '" + codigoProduto + "' não encontrado.");
        }
    }


    public Produto buscarProdutoPorCodigo(String codigo) {
        for (Produto p : this.produtosEmEstoque) {
            if (p.getCodigo().equalsIgnoreCase(codigo)) {
                return p;
            }
        }
        return null; 
    }
}
