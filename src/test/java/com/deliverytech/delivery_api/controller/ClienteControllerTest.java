package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.ClienteRequest;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteRequest clienteRequest;
    private Cliente cliente;
    private List<Cliente> clientes;

    @BeforeEach
    void setUp() {
        clienteRequest = new ClienteRequest();
        clienteRequest.setNome("João Silva Santos");
        clienteRequest.setTelefone("11999999999");
        clienteRequest.setEmail("joao@email.com");
        clienteRequest.setEndereco("Av João da Silva");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("João Silva Santos");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("11999999999");
        cliente.setEndereco("Av João da Silva");
        cliente.setAtivo(true);

        clientes = Arrays.asList(cliente);
    }

    @Test
    @DisplayName("POST /api/clientes - Deve cadastrar cliente com sucesso")
    @WithMockUser
    void deveCadastrarClienteComSucesso() throws Exception {
        // Given
        when(clienteService.cadastrar(any(ClienteRequest.class))).thenReturn(cliente);

        // When & Then
        mockMvc.perform(post("/api/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1)) // ✅ Controller retorna Cliente direto
                .andExpect(jsonPath("$.nome").value("João Silva Santos"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    @DisplayName("GET /api/clientes - Deve listar clientes ativos")
    @WithMockUser
    void deveListarClientesAtivos() throws Exception {
        // Given
        when(clienteService.listarAtivos()).thenReturn(clientes);

        // When & Then
        mockMvc.perform(get("/api/clientes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1)) // ✅ Array direto de Clientes
                .andExpect(jsonPath("$[0].nome").value("João Silva Santos"));
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Deve buscar cliente por ID")
    @WithMockUser
    void deveBuscarClientePorId() throws Exception {
        // Given
        when(clienteService.buscarPorId(1L)).thenReturn(Optional.of(cliente));

        // When & Then
        mockMvc.perform(get("/api/clientes/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1)) // ✅ ApiResponseWrapper format
                .andExpect(jsonPath("$.data.nome").value("João Silva Santos"))
                .andExpect(jsonPath("$.message").value("Cliente encontrado"));
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Deve retornar 404 quando cliente não encontrado")
    @WithMockUser
    void deveRetornar404QuandoClienteNaoEncontrado() throws Exception {
        // Given
        when(clienteService.buscarPorId(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/clientes/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cliente não encontrado"));
    }

    @Test
    @DisplayName("GET /api/clientes/email/{email} - Deve buscar cliente por email")
    @WithMockUser
    void deveBuscarClientePorEmail() throws Exception {
        // Given
        when(clienteService.buscarPorEmail("joao@email.com")).thenReturn(Optional.of(cliente));

        // When & Then
        mockMvc.perform(get("/api/clientes/email/{email}", "joao@email.com")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1)) // ✅ Cliente direto (sem wrapper)
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.nome").value("João Silva Santos"));
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} - Deve atualizar cliente")
    @WithMockUser
    void deveAtualizarCliente() throws Exception {
        // Given
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setId(1L);
        clienteAtualizado.setNome("João Silva Atualizado");
        clienteAtualizado.setEmail("joao@email.com");
        clienteAtualizado.setAtivo(true);

        when(clienteService.atualizar(eq(1L), any(ClienteRequest.class)))
                .thenReturn(clienteAtualizado);

        // When & Then
        mockMvc.perform(put("/api/clientes/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1)) // ✅ Controller retorna Cliente direto
                .andExpect(jsonPath("$.nome").value("João Silva Atualizado"));
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id}/status - Deve ativar/desativar cliente")
    @WithMockUser
    void deveAtivarDesativarCliente() throws Exception {
        // Given
        Cliente clienteInativo = new Cliente();
        clienteInativo.setId(1L);
        clienteInativo.setNome("João Silva Santos");
        clienteInativo.setEmail("joao@email.com");
        clienteInativo.setAtivo(false);

        when(clienteService.ativarDesativarCliente(1L)).thenReturn(clienteInativo);

        // When & Then
        mockMvc.perform(patch("/api/clientes/{id}/status", 1L)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Cliente desativado com sucesso")) // ✅ Estrutura do Map
                .andExpect(jsonPath("$.cliente.id").value(1)) // ✅ Cliente dentro do Map
                .andExpect(jsonPath("$.cliente.nome").value("João Silva Santos"))
                .andExpect(jsonPath("$.cliente.ativo").value(false));
    }

    @Test
    @DisplayName("GET /api/clientes/buscar - Deve buscar clientes por nome")
    @WithMockUser
    void deveBuscarPorNome() throws Exception {
        // Given
        when(clienteService.buscarPorNome("Silva")).thenReturn(clientes);

        // When & Then
        mockMvc.perform(get("/api/clientes/buscar")
                .param("nome", "Silva")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1)) // ✅ Array direto de Clientes
                .andExpect(jsonPath("$[0].nome").value("João Silva Santos"));
    }

    @Test
    @DisplayName("POST /api/clientes - Deve validar campos obrigatórios")
    @WithMockUser
    void deveValidarCamposObrigatorios() throws Exception {
        // Given - Request inválido
        ClienteRequest requestInvalido = new ClienteRequest();

        // When & Then
        mockMvc.perform(post("/api/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/clientes/email/{email} - Deve retornar 404 para email não encontrado")
    @WithMockUser
    void deveRetornar404ParaEmailNaoEncontrado() throws Exception {
        // Given
        when(clienteService.buscarPorEmail("inexistente@email.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/clientes/email/{email}", "inexistente@email.com"))
                .andExpect(status().isNotFound());
    }
}
