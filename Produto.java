public class Produto {
    private String codigo;
    private String nome;
    private double preco;
    private int quantidade;

    public Produto(String codigo, String nome, double preco, int quantidadeInicial) {
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
        if (quantidadeInicial < 0) {
            System.out.println("Atenção: Quantidade inicial para '" + nome + "' era negativa. Definida como 0.");
            this.quantidade = 0;
        } else {
            this.quantidade = quantidadeInicial;
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void adicionarQuantidade(int quantidadeAdicional) {
        if (quantidadeAdicional > 0) {
            this.quantidade += quantidadeAdicional;
        } else {
            System.out.println("Atenção: Quantidade de entrada deve ser positiva.");
        }
    }

    public boolean removerQuantidade(int quantidadeARemover) {
        if (quantidadeARemover <= 0) {
            System.out.println("Atenção: Quantidade a remover deve ser positiva.");
            return false;
        }

        if (this.quantidade >= quantidadeARemover) {
            this.quantidade -= quantidadeARemover;
            return true; 
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Produto [" +
                "Código: '" + codigo + '\'' +
                ", Nome: '" + nome + '\'' +
                ", Preço: R$ " + String.format("%.2f", preco) +
                ", Estoque: " + quantidade + " unidades" +
                ']';
    }
}