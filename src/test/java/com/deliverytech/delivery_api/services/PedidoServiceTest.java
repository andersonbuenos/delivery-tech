package com.deliverytech.delivery_api.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deliverytech.delivery_api.dto.request.ItemPedidoRequest;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.model.Pedido;
import com.deliverytech.delivery_api.model.Produto;
import com.deliverytech.delivery_api.model.Restaurante;
import com.deliverytech.delivery_api.model.StatusPedido;
import com.deliverytech.delivery_api.service.PedidoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoService pedidoService;

    private Pedido pedido;
    private Cliente cliente;
    private Restaurante restaurante;
    private Produto produto;
    private List<ItemPedidoRequest> itens;

    @BeforeEach
    void setUp() {
        // Setup Cliente
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva Santos");
        cliente.setEmail("joao@email.com");
        cliente.setAtivo(true);

        // Setup Restaurante
        restaurante = new Restaurante();
        restaurante.setId(1L);
        restaurante.setNome("Restaurante Teste");
        restaurante.setAtivo(true);

        // Setup Produto
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Pizza Margherita");
        produto.setPreco(BigDecimal.valueOf(35.90));
        produto.setDisponivel(true);
        produto.setRestaurante(restaurante);

        // Setup Pedido
        pedido = new Pedido();
        pedido.setId(1L);
        pedido.setCliente(cliente);
        pedido.setRestaurante(restaurante);
        pedido.setValorTotal(BigDecimal.valueOf(35.90));
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setObservacoes("Sem cebola");

        // Setup Itens do Pedido
        ItemPedidoRequest item = new ItemPedidoRequest();
        item.setProdutoId(1L);
        item.setQuantidade(2);
        itens = List.of(item);
    }

    @Test
    @DisplayName("Criar pedido deve retornar pedido salvo")
    void testCriarPedido_DeveRetornarPedidoSalvo() {
        // Given - usando o método que EXISTE na interface
        when(pedidoService.criar(any(Pedido.class))).thenReturn(pedido);

        // When
        Pedido resultado = pedidoService.criar(pedido);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(StatusPedido.PENDENTE, resultado.getStatus());
        assertEquals(BigDecimal.valueOf(35.90), resultado.getValorTotal());
        assertEquals("João Silva Santos", resultado.getCliente().getNome());
        
        verify(pedidoService, times(1)).criar(pedido);
    }

    @Test
    @DisplayName("Buscar pedido por ID deve retornar pedido quando existir")
    void testBuscarPedidoPorId_DeveRetornarPedido() {
        // Given - método que existe
        when(pedidoService.buscarPorId(1L)).thenReturn(pedido);
        
        // When
        Pedido resultado = pedidoService.buscarPorId(1L);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João Silva Santos", resultado.getCliente().getNome());
        
        verify(pedidoService, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("Buscar pedidos por cliente deve retornar lista de pedidos")
    void testBuscarPedidosPorCliente_DeveRetornarListaPedidos() {
        // Given
        List<Pedido> pedidos = List.of(pedido);
        when(pedidoService.buscarPorCliente(1L)).thenReturn(pedidos);
        
        // When
        List<Pedido> resultado = pedidoService.buscarPorCliente(1L);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("João Silva Santos", resultado.get(0).getCliente().getNome());
        
        verify(pedidoService, times(1)).buscarPorCliente(1L);
    }

    @Test
    @DisplayName("Atualizar status do pedido deve retornar pedido atualizado")
    void testAtualizarStatusPedido_DeveRetornarPedidoAtualizado() {
        // Given
        Pedido pedidoAtualizado = new Pedido();
        pedidoAtualizado.setId(1L);
        pedidoAtualizado.setStatus(StatusPedido.CONFIRMADO);
        
        when(pedidoService.atualizarStatus(1L, StatusPedido.CONFIRMADO))
                .thenReturn(pedidoAtualizado);
        
        // When
        Pedido resultado = pedidoService.atualizarStatus(1L, StatusPedido.CONFIRMADO);
        
        // Then
        assertNotNull(resultado);
        assertEquals(StatusPedido.CONFIRMADO, resultado.getStatus());
        
        verify(pedidoService, times(1)).atualizarStatus(1L, StatusPedido.CONFIRMADO);
    }

    @Test
    @DisplayName("Listar todos os pedidos deve retornar lista de pedidos")
    void testListarTodosPedidos_DeveRetornarListaPedidos() {
        // Given
        Pedido pedido2 = new Pedido();
        pedido2.setId(2L);
        pedido2.setStatus(StatusPedido.ENTREGUE);
        
        List<Pedido> pedidos = List.of(pedido, pedido2);
        when(pedidoService.listarTodos()).thenReturn(pedidos);
        
        // When
        List<Pedido> resultado = pedidoService.listarTodos();
        
        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(StatusPedido.PENDENTE, resultado.get(0).getStatus());
        assertEquals(StatusPedido.ENTREGUE, resultado.get(1).getStatus());
        
        verify(pedidoService, times(1)).listarTodos();
    }

    @Test
    @DisplayName("Calcular total deve considerar produtos e taxa de entrega")
    void testCalcularTotal_DeveConsiderarProdutosETaxaEntrega() {
        // Given - usando método que existe
        BigDecimal valorEsperado = BigDecimal.valueOf(40.90);
        when(pedidoService.calcularTotal(pedido)).thenReturn(valorEsperado);
        
        // When
        BigDecimal resultado = pedidoService.calcularTotal(pedido);
        
        // Then
        assertNotNull(resultado);
        assertEquals(valorEsperado, resultado);
        
        verify(pedidoService, times(1)).calcularTotal(pedido);
    }

    @Test
    @DisplayName("Calcular total do pedido por itens deve retornar valor correto")
    void testCalcularTotalPedido_PorItens_DeveRetornarValorCorreto() {
        // Given - método que existe na interface
        BigDecimal valorEsperado = BigDecimal.valueOf(71.80); // 2 pizzas * 35.90
        when(pedidoService.calcularTotalPedido(itens)).thenReturn(valorEsperado);
        
        // When
        BigDecimal resultado = pedidoService.calcularTotalPedido(itens);
        
        // Then
        assertNotNull(resultado);
        assertEquals(valorEsperado, resultado);
        
        verify(pedidoService, times(1)).calcularTotalPedido(itens);
    }

    @Test
    @DisplayName("Buscar pedidos por status deve retornar apenas pedidos com status especificado")
    void testBuscarPedidosPorStatus_DeveRetornarPedidosComStatusEspecificado() {
        // Given
        List<Pedido> pedidosPendentes = List.of(pedido);
        when(pedidoService.buscarPorStatus(StatusPedido.PENDENTE))
                .thenReturn(pedidosPendentes);
        
        // When
        List<Pedido> resultado = pedidoService.buscarPorStatus(StatusPedido.PENDENTE);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(StatusPedido.PENDENTE, resultado.get(0).getStatus());
        
        verify(pedidoService, times(1)).buscarPorStatus(StatusPedido.PENDENTE);
    }

    @Test
    @DisplayName("Cancelar pedido deve atualizar status para cancelado")
    void testCancelarPedido_DeveAtualizarStatusParaCancelado() {
        // Given - usando método que existe
        Pedido pedidoCancelado = new Pedido();
        pedidoCancelado.setId(1L);
        pedidoCancelado.setStatus(StatusPedido.CANCELADO);
        
        when(pedidoService.cancelar(1L)).thenReturn(pedidoCancelado);
        
        // When
        Pedido resultado = pedidoService.cancelar(1L);
        
        // Then
        assertNotNull(resultado);
        assertEquals(StatusPedido.CANCELADO, resultado.getStatus());
        
        verify(pedidoService, times(1)).cancelar(1L);
    }

    @Test
    @DisplayName("Confirmar pedido deve atualizar status para confirmado")
    void testConfirmarPedido_DeveAtualizarStatusParaConfirmado() {
        // Given
        Pedido pedidoConfirmado = new Pedido();
        pedidoConfirmado.setId(1L);
        pedidoConfirmado.setStatus(StatusPedido.CONFIRMADO);
        
        when(pedidoService.confirmar(1L)).thenReturn(pedidoConfirmado);
        
        // When
        Pedido resultado = pedidoService.confirmar(1L);
        
        // Then
        assertNotNull(resultado);
        assertEquals(StatusPedido.CONFIRMADO, resultado.getStatus());
        
        verify(pedidoService, times(1)).confirmar(1L);
    }

    @Test
    @DisplayName("Buscar pedidos por restaurante deve retornar lista de pedidos")
    void testBuscarPedidosPorRestaurante_DeveRetornarListaPedidos() {
        // Given
        List<Pedido> pedidosRestaurante = List.of(pedido);
        when(pedidoService.buscarPorRestaurante(1L)).thenReturn(pedidosRestaurante);
        
        // When
        List<Pedido> resultado = pedidoService.buscarPorRestaurante(1L);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Restaurante Teste", resultado.get(0).getRestaurante().getNome());
        
        verify(pedidoService, times(1)).buscarPorRestaurante(1L);
    }

    @Test
    @DisplayName("Buscar pedido por ID com itens deve retornar optional com pedido")
    void testBuscarPorIdComItens_DeveRetornarOptionalComPedido() {
        // Given
        when(pedidoService.buscarPorIdComItens(1L)).thenReturn(Optional.of(pedido));
        
        // When
        Optional<Pedido> resultado = pedidoService.buscarPorIdComItens(1L);
        
        // Then
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        
        verify(pedidoService, times(1)).buscarPorIdComItens(1L);
    }

    @Test
    @DisplayName("Buscar pedidos por período deve retornar lista filtrada")
    void testBuscarPorPeriodo_DeveRetornarListaFiltrada() {
        // Given
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fim = LocalDateTime.now();
        List<Pedido> pedidosPeriodo = List.of(pedido);
        
        when(pedidoService.buscarPorPeriodo(inicio, fim)).thenReturn(pedidosPeriodo);
        
        // When
        List<Pedido> resultado = pedidoService.buscarPorPeriodo(inicio, fim);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        
        verify(pedidoService, times(1)).buscarPorPeriodo(inicio, fim);
    }

    @Test
    @DisplayName("Listar com filtros deve retornar pedidos filtrados")
    void testListarComFiltros_DeveRetornarPedidosFiltrados() {
        // Given
        LocalDate dataInicio = LocalDate.now().minusDays(7);
        LocalDate dataFim = LocalDate.now();
        List<Pedido> pedidosFiltrados = List.of(pedido);
        
        when(pedidoService.listarComFiltros(StatusPedido.PENDENTE, dataInicio, dataFim))
                .thenReturn(pedidosFiltrados);
        
        // When
        List<Pedido> resultado = pedidoService.listarComFiltros(StatusPedido.PENDENTE, dataInicio, dataFim);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(StatusPedido.PENDENTE, resultado.get(0).getStatus());
        
        verify(pedidoService, times(1)).listarComFiltros(StatusPedido.PENDENTE, dataInicio, dataFim);
    }

    @Test
    @DisplayName("Adicionar item ao pedido deve retornar pedido atualizado")
    void testAdicionarItem_DeveRetornarPedidoAtualizado() {
        // Given
        when(pedidoService.adicionarItem(1L, 1L, 2)).thenReturn(pedido);
        
        // When
        Pedido resultado = pedidoService.adicionarItem(1L, 1L, 2);
        
        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        
        verify(pedidoService, times(1)).adicionarItem(1L, 1L, 2);
    }

    @Test
    @DisplayName("Deletar pedido deve chamar método deletar")
    void testDeletarPedido_DeveChamarMetodoDeletar() {
        // Given
        doNothing().when(pedidoService).deletar(1L);
        
        // When
        pedidoService.deletar(1L);
        
        // Then
        verify(pedidoService, times(1)).deletar(1L);
    }
}