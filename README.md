# sisVendas-BP
Sistema de Ponto de Vendas em Java seguindo boas práticas (SOLID, camadas claras, testes).

Componentes do grupo:
- Brenda Gomes da Silva (mat. 20240021503)
- Carlos Eduardo Miranda da Silva (mat. 20200118231)
- Hugo José de Lima Nunes (mat. 20240062319)

## Requisitos
- Java 17+
- Maven

## Como executar os testes

```bash
mvn -q test
```

## Exemplo de uso do serviço (registrar vendas)

```java
// Setup básico com repositórios em memória (para demo/teste)
InMemoryProdutoRepository prodRepo = new InMemoryProdutoRepository();
prodRepo.salvar(new Produto("001", "Caneta", 2.5, 100));
prodRepo.salvar(new Produto("002", "Caderno", 15.0, 50));

InMemoryVendaRepository vendaRepo = new InMemoryVendaRepository();
VendaService service = new VendaService(prodRepo, vendaRepo);

// LOJA
service.registrarVenda(
    TipoVenda.LOJA,
    List.of(new Par<>("001", 2), new Par<>("002", 1)),
    Optional.empty()
);

// WEB (com endereço)
EnderecoEntrega endereco = new EnderecoEntrega(
    "Cliente X","Rua A","123","Centro","Natal","RN","59000-000");
service.registrarVenda(
    TipoVenda.WEB,
    List.of(new Par<>("001", 1)),
    Optional.of(endereco)
);

// Listar vendas em ordem decrescente por data
service.listarVendas();
```

## Presenter de Console (opcional)
Existe um adaptador `view.ConsoleVendasPresenter` para imprimir vendas e listas no console:

```java
ConsoleVendasPresenter presenter = new ConsoleVendasPresenter();
presenter.imprimirListaVendas(service.listarVendas());
```

## Demo rápida
Execute a classe `com.sisvendas.demo.DemoVendas` (IDE ou `mvn exec` se desejar configurar plugin) para ver um fluxo completo sem UI.

## Observações
- Esta camada está pronta para integração com um menu CLI ou UI futura.
- Regras de negócio e validações estão centralizadas no `service.VendaService`.
