package com.deliverytech.delivery_api.config;

import com.deliverytech.delivery_api.model.*;
import com.deliverytech.delivery_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

import java.util.Arrays;


@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== INICIANDO CARGA DE DADOS DE TESTE ===");

        // Inserir dados de teste (sem limpar dados existentes)
        inserirClientes();
        inserirRestaurantes();

        System.out.println("=== CARGA DE DADOS CONCLUÍDA ===");
        
        // ✅ ADICIONAR: Spring Boot iniciada com sucesso + Bender
        System.out.println("\n✅ Spring Boot Application iniciada com sucesso!");
        
        
        // ✅ INFORMAR sobre captura automática
        System.out.println("\n🎯 SISTEMA DE CAPTURA AUTOMÁTICA ATIVO!");
        System.out.println("📁 Respostas serão salvas em: ./entregaveis/");
        System.out.println("🔄 Faça requisições para /api/* e veja os arquivos sendo gerados!\n");
    }


private void inserirClientes() {
    System.out.println("--- Inserindo clientes ---");

    Cliente cliente1 = new Cliente();
    cliente1.setNome("João Silva");
    cliente1.setEmail("joao@email.com");
    cliente1.setTelefone("11987654321");
    cliente1.setEndereco("Rua das Flores, 123 - Vila Madalena, São Paulo - SP");
    cliente1.setAtivo(true);

    Cliente cliente2 = new Cliente();
    cliente2.setNome("Maria Santos");
    cliente2.setEmail("maria@email.com");
    cliente2.setTelefone("11876543210");
    cliente2.setEndereco("Av. Paulista, 456 - Bela Vista, São Paulo - SP");
    cliente2.setAtivo(true);

    Cliente cliente3 = new Cliente();
    cliente3.setNome("Pedro Oliveira");
    cliente3.setEmail("pedro@email.com");
    cliente3.setTelefone("11765432109");
    cliente3.setEndereco("Rua Augusta, 789 - Consolação, São Paulo - SP");
    cliente3.setAtivo(false);

    Cliente cliente4 = new Cliente();
    cliente4.setNome("Ana Costa");
    cliente4.setEmail("ana@email.com");
    cliente4.setTelefone("11654321098");
    cliente4.setEndereco("Rua Oscar Freire, 321 - Jardins, São Paulo - SP");
    cliente4.setAtivo(true);

    Cliente cliente5 = new Cliente();
    cliente5.setNome("Carlos Ferreira");
    cliente5.setEmail("carlos@email.com");
    cliente5.setTelefone("11543210987");
    cliente5.setEndereco("Rua 25 de Março, 654 - Centro, São Paulo - SP");
    cliente5.setAtivo(true);

    clienteRepository.saveAll(Arrays.asList(cliente1, cliente2, cliente3, cliente4, cliente5));
    System.out.println("✓ 5 clientes inseridos");
}

private void inserirRestaurantes() {
    System.out.println("--- Inserindo Restaurantes ---");

    Restaurante restaurante1 = new Restaurante();
    restaurante1.setNome("Pizza Express");
    restaurante1.setCategoria("Italiana");
    restaurante1.setTelefone("1133333333");
    restaurante1.setTaxaEntrega(new BigDecimal("3.50"));
    restaurante1.setAtivo(true);

    Restaurante restaurante2 = new Restaurante();
    restaurante2.setNome("Burger King");
    restaurante2.setCategoria("Fast Food");
    restaurante2.setTelefone("1144444444");
    restaurante2.setTaxaEntrega(new BigDecimal("5.00"));
    restaurante2.setAtivo(true);

    Restaurante restaurante3 = new Restaurante();
    restaurante3.setNome("Sushi House");
    restaurante3.setCategoria("Japonesa");
    restaurante3.setTelefone("1155555555");
    restaurante3.setTaxaEntrega(new BigDecimal("4.00"));
    restaurante3.setAtivo(true);

    Restaurante restaurante4 = new Restaurante();
    restaurante4.setNome("Gyros Athenas");
    restaurante4.setCategoria("Grega");
    restaurante4.setTelefone("1166666666");
    restaurante4.setTaxaEntrega(new BigDecimal("6.50"));
    restaurante4.setAtivo(true);

    Restaurante restaurante5 = new Restaurante();
    restaurante5.setNome("Chiparia do Porto");
    restaurante5.setCategoria("Frutos do Mar");
    restaurante5.setTelefone("1177777777");
    restaurante5.setTaxaEntrega(new BigDecimal("7.00"));
    restaurante5.setAtivo(true);

    restauranteRepository.saveAll(Arrays.asList(restaurante1, restaurante2, restaurante3, restaurante4, restaurante5));
    System.out.println("✓ 5 restaurantes inseridos");

    // Inserir produtos após restaurantes para poder associá-los
    inserirProdutos();
}

private void inserirProdutos() {
    System.out.println("--- Inserindo Produtos ---");

    // Buscar restaurantes para associar aos produtos
    var restaurantes = restauranteRepository.findAll();
    var pizzaExpress = restaurantes.stream().filter(r -> r.getNome().equals("Pizza Express")).findFirst().orElse(null);
    var burgerKing = restaurantes.stream().filter(r -> r.getNome().equals("Burger King")).findFirst().orElse(null);
    var sushiHouse = restaurantes.stream().filter(r -> r.getNome().equals("Sushi House")).findFirst().orElse(null);
    var gyrosAthenas = restaurantes.stream().filter(r -> r.getNome().equals("Gyros Athenas")).findFirst().orElse(null);
    var chipariaPorto = restaurantes.stream().filter(r -> r.getNome().equals("Chiparia do Porto")).findFirst().orElse(null);

    Produto produto1 = new Produto();
    produto1.setNome("Pizza Margherita");
    produto1.setCategoria("Pizza"); // ← ADICIONADO
    produto1.setDescricao("Pizza clássica com molho de tomate, mussarela e manjericão");
    produto1.setPreco(new BigDecimal("25.90"));
    produto1.setRestaurante(pizzaExpress);
    produto1.setAtivo(true);

    Produto produto2 = new Produto();
    produto2.setNome("Pizza Pepperoni");
    produto2.setCategoria("Pizza"); // ← ADICIONADO
    produto2.setDescricao("Pizza com molho de tomate, mussarela e pepperoni");
    produto2.setPreco(new BigDecimal("29.90"));
    produto2.setRestaurante(pizzaExpress);
    produto2.setAtivo(true);

    Produto produto3 = new Produto();
    produto3.setNome("Big Burger");
    produto3.setCategoria("Hambúrguer"); // ← ADICIONADO
    produto3.setDescricao("Hambúrguer duplo com queijo, alface, tomate e molho especial");
    produto3.setPreco(new BigDecimal("18.50"));
    produto3.setRestaurante(burgerKing);
    produto3.setAtivo(true);

    Produto produto4 = new Produto();
    produto4.setNome("Batata Frita Grande");
    produto4.setCategoria("Acompanhamento"); // ← ADICIONADO
    produto4.setDescricao("Porção grande de batatas fritas crocantes");
    produto4.setPreco(new BigDecimal("8.90"));
    produto4.setRestaurante(burgerKing);
    produto4.setAtivo(true);

    Produto produto5 = new Produto();
    produto5.setNome("Sushi Salmão");
    produto5.setCategoria("Sushi"); // ← ADICIONADO
    produto5.setDescricao("8 peças de sushi de salmão fresco");
    produto5.setPreco(new BigDecimal("32.00"));
    produto5.setRestaurante(sushiHouse);
    produto5.setAtivo(true);

    Produto produto6 = new Produto();
    produto6.setNome("Hot Roll");
    produto6.setCategoria("Sushi"); // ← ADICIONADO
    produto6.setDescricao("8 peças de hot roll empanado com salmão");
    produto6.setPreco(new BigDecimal("28.50"));
    produto6.setRestaurante(sushiHouse);
    produto6.setAtivo(true);

    Produto produto7 = new Produto();
    produto7.setNome("Gyros de Cordeiro");
    produto7.setCategoria("Espeto"); // ← ADICIONADO
    produto7.setDescricao("Espeto de cordeiro grelhado com molho tzatziki, tomate e cebola roxa");
    produto7.setPreco(new BigDecimal("35.90"));
    produto7.setRestaurante(gyrosAthenas);
    produto7.setAtivo(true);

    Produto produto8 = new Produto();
    produto8.setNome("Souvlaki de Frango");
    produto8.setCategoria("Espeto"); // ← ADICIONADO
    produto8.setDescricao("Espetinho de frango marinado com ervas gregas e batata frita");
    produto8.setPreco(new BigDecimal("28.50"));
    produto8.setRestaurante(gyrosAthenas);
    produto8.setAtivo(true);

    Produto produto9 = new Produto();
    produto9.setNome("Fish & Chips Tradicional");
    produto9.setCategoria("Peixe"); // ← ADICIONADO
    produto9.setDescricao("Filé de bacalhau empanado com batatas fritas e molho tártaro");
    produto9.setPreco(new BigDecimal("42.90"));
    produto9.setRestaurante(chipariaPorto);
    produto9.setAtivo(true);

    Produto produto10 = new Produto();
    produto10.setNome("Porção de Camarão Empanado");
    produto10.setCategoria("Frutos do Mar"); // ← ADICIONADO
    produto10.setDescricao("500g de camarão empanado com molho agridoce");
    produto10.setPreco(new BigDecimal("52.00"));
    produto10.setRestaurante(chipariaPorto);
    produto10.setAtivo(true);

    produtoRepository.saveAll(Arrays.asList(
        produto1, produto2, produto3, produto4, produto5, 
        produto6, produto7, produto8, produto9, produto10
    ));
    System.out.println("✓ 10 produtos inseridos");
}
}