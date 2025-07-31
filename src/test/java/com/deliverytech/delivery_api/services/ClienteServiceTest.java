package com.deliverytech.delivery_api.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deliverytech.delivery_api.dto.request.ClienteRequest;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.service.ClienteService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock  // ✅ Mockando a interface
    private ClienteService clienteService;

    private ClienteRequest clienteRequest;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        clienteRequest = new ClienteRequest();
        clienteRequest.setNome("João Silva Santos");
        clienteRequest.setEmail("teste@email.com");
        clienteRequest.setTelefone("11999999999");
        clienteRequest.setEndereco("Av Teste 123 - Bairro Centro");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva Santos");
        cliente.setEmail("teste@email.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Av Teste 123 - Bairro Centro");
        cliente.setDataCriacao(LocalDateTime.now());
        cliente.setAtivo(true);
    }

    @Test
    @DisplayName("Cadastrar cliente deve retornar cliente salvo")
    void testCadastrarCliente_DeveRetornarClienteSalvo() {
        // Given
        when(clienteService.cadastrar(any(ClienteRequest.class))).thenReturn(cliente);

        // When
        Cliente resultado = clienteService.cadastrar(clienteRequest);

        // Then
        assertNotNull(resultado);
        assertEquals("João Silva Santos", resultado.getNome());
        assertEquals("teste@email.com", resultado.getEmail());
        assertTrue(resultado.getAtivo());
        
        verify(clienteService, times(1)).cadastrar(clienteRequest);
    }

    @Test
    @DisplayName("Buscar cliente por ID deve retornar cliente quando existir")
    void testBuscarClientePorId_DeveRetornarCliente() {
        // Given
        when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));
        
        // When
        Optional<Cliente> resultado = clienteService.buscarPorId(1L);
        
        // Then
        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("João Silva Santos", resultado.get().getNome());
        
        verify(clienteService, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("Buscar cliente por ID inexistente deve retornar vazio")
    void testBuscarClientePorId_DeveRetornarVazioQuandoNaoEncontrado() {
        // Given
        when(clienteService.buscarPorId(999L)).thenReturn(Optional.empty());

        // When
        Optional<Cliente> resultado = clienteService.buscarPorId(999L);
        
        // Then
        assertFalse(resultado.isPresent());
        verify(clienteService, times(1)).buscarPorId(999L);
    }

    @Test
    @DisplayName("Buscar cliente por email deve retornar cliente quando existir")
    void testBuscarClientePorEmail_DeveRetornarCliente() {
        // Given
        when(clienteService.buscarPorEmail("teste@email.com"))
            .thenReturn(Optional.of(cliente));
        
        // When
        Optional<Cliente> resultado = clienteService.buscarPorEmail("teste@email.com");
        
        // Then
        assertTrue(resultado.isPresent());
        assertEquals("teste@email.com", resultado.get().getEmail());
        assertEquals("João Silva Santos", resultado.get().getNome());
        
        verify(clienteService, times(1)).buscarPorEmail("teste@email.com");
    }

    @Test
    @DisplayName("Listar clientes ativos deve retornar lista de clientes")
    void testListarAtivos_DeveRetornarListaClientes() {
        // Given
        Cliente cliente2 = new Cliente();
        cliente2.setId(2L);
        cliente2.setNome("Maria Silva Santos");
        cliente2.setEmail("maria@email.com");
        cliente2.setAtivo(true);
        
        List<Cliente> clientes = List.of(cliente, cliente2);
        when(clienteService.listarAtivos()).thenReturn(clientes);
        
        // When
        List<Cliente> resultado = clienteService.listarAtivos();
        
        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("João Silva Santos", resultado.get(0).getNome());
        assertEquals("Maria Silva Santos", resultado.get(1).getNome());
        
        verify(clienteService, times(1)).listarAtivos();
    }

    @Test
    @DisplayName("Buscar por nome deve retornar clientes que contenham o termo")
    void testBuscarPorNome_DeveRetornarClientesComTermo() {
        // Given
        List<Cliente> clientes = List.of(cliente);
        when(clienteService.buscarPorNome("Silva")).thenReturn(clientes);
        
        // When
        List<Cliente> resultado = clienteService.buscarPorNome("Silva");
        
        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getNome().contains("Silva"));
        
        verify(clienteService, times(1)).buscarPorNome("Silva");
    }

    @Test
    @DisplayName("Atualizar cliente deve retornar cliente atualizado")
    void testAtualizarCliente_DeveRetornarClienteAtualizado() {
        // Given
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setId(1L);
        clienteAtualizado.setNome("João Silva Atualizado");
        clienteAtualizado.setEmail("teste@email.com");

        when(clienteService.atualizar(eq(1L), any(ClienteRequest.class)))
                .thenReturn(clienteAtualizado);
        
        // When
        Cliente resultado = clienteService.atualizar(1L, clienteRequest);
        
        // Then
        assertNotNull(resultado);
        assertEquals("João Silva Atualizado", resultado.getNome());
        
        verify(clienteService, times(1)).atualizar(1L, clienteRequest);
    }

    @Test
    @DisplayName("Ativar/Desativar cliente deve alterar status")
    void testAtivarDesativarCliente_DeveAlterarStatus() {
        // Given
        Cliente clienteInativo = new Cliente();
        clienteInativo.setId(1L);
        clienteInativo.setNome("João Silva Santos");
        clienteInativo.setAtivo(false);

        when(clienteService.ativarDesativarCliente(1L)).thenReturn(clienteInativo);
        
        // When
        Cliente resultado = clienteService.ativarDesativarCliente(1L);
        
        // Then
        assertNotNull(resultado);
        assertFalse(resultado.getAtivo());
        
        verify(clienteService, times(1)).ativarDesativarCliente(1L);
    }

    @Test
    @DisplayName("Inativar cliente deve chamar método inativar")
    void testInativarCliente_DeveChamarMetodo() {
        // Given
        doNothing().when(clienteService).inativar(1L);
        
        // When
        clienteService.inativar(1L);
        
        // Then
        verify(clienteService, times(1)).inativar(1L);
    }
}
