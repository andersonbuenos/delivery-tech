package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.request.ClienteRequest;
import com.deliverytech.delivery_api.model.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class ClienteControllerIT {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private ClienteRequest clienteRequest;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // Limpar banco antes de cada teste
        clienteRepository.deleteAll();

        // Setup dados de teste
        clienteRequest = new ClienteRequest();
        clienteRequest.setNome("João Silva Santos");
        clienteRequest.setEmail("joao@teste.com");
        clienteRequest.setTelefone("11999999999");
        clienteRequest.setEndereco("Rua Teste, 123");

        cliente = new Cliente();
        cliente.setNome("Maria Silva Santos");
        cliente.setEmail("maria@teste.com");
        cliente.setTelefone("11888888888");
        cliente.setEndereco("Av Teste, 456");
        cliente.setAtivo(true);
    }

    @Test
    @DisplayName("POST /api/clientes - Deve criar cliente com dados válidos")
    @WithMockUser
    void testCriarCliente_ComDadosValidos_DeveRetornar201() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João Silva Santos"))
                .andExpect(jsonPath("$.email").value("joao@teste.com"))
                .andExpect(jsonPath("$.telefone").value("11999999999"))
                .andExpect(jsonPath("$.ativo").value(true))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("POST /api/clientes - Deve retornar 400 com dados inválidos")
    @WithMockUser
    void testCriarCliente_ComDadosInvalidos_DeveRetornar400() throws Exception {
        // Given - dados inválidos
        ClienteRequest requestInvalido = new ClienteRequest();
        requestInvalido.setNome("Jo"); // Nome muito curto
        requestInvalido.setEmail("email-inválido"); // Email inválido
        requestInvalido.setTelefone("123"); // Telefone inválido
        requestInvalido.setEndereco(""); // Endereço vazio

        // When & Then
        mockMvc.perform(post("/api/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Deve retornar cliente existente")
    @WithMockUser
    void testBuscarClientePorId_ClienteExistente_DeveRetornar200() throws Exception {
        // Given
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // When & Then
        mockMvc.perform(get("/api/clientes/{id}", clienteSalvo.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteSalvo.getId()))
                .andExpect(jsonPath("$.nome").value("Maria Silva Santos"))
                .andExpect(jsonPath("$.email").value("maria@teste.com"));
    }

    @Test
    @DisplayName("GET /api/clientes/{id} - Deve retornar 404 para cliente inexistente")
    @WithMockUser
    void testBuscarClientePorId_ClienteInexistente_DeveRetornar404() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/clientes/{id}", 999L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/clientes - Deve listar clientes ativos")
    @WithMockUser
    void testListarClientesAtivos_DeveRetornar200() throws Exception {
        // Given
        clienteRepository.save(cliente);
        
        Cliente cliente2 = new Cliente();
        cliente2.setNome("Pedro Santos Silva");
        cliente2.setEmail("pedro@teste.com");
        cliente2.setTelefone("11777777777");
        cliente2.setEndereco("Rua Pedro, 789");
        cliente2.setAtivo(true);
        clienteRepository.save(cliente2);

        // When & Then
        mockMvc.perform(get("/api/clientes")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Maria Silva Santos"))
                .andExpect(jsonPath("$[1].nome").value("Pedro Santos Silva"));
    }

    @Test
    @DisplayName("PUT /api/clientes/{id} - Deve atualizar cliente existente")
    @WithMockUser
    void testAtualizarCliente_ClienteExistente_DeveRetornar200() throws Exception {
        // Given
        Cliente clienteSalvo = clienteRepository.save(cliente);
        
        ClienteRequest requestAtualizacao = new ClienteRequest();
        requestAtualizacao.setNome("Maria Silva Atualizada");
        requestAtualizacao.setEmail("maria.atualizada@teste.com");
        requestAtualizacao.setTelefone("11888888888");
        requestAtualizacao.setEndereco("Av Atualizada, 999");

        // When & Then
        mockMvc.perform(put("/api/clientes/{id}", clienteSalvo.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteSalvo.getId()))
                .andExpect(jsonPath("$.nome").value("Maria Silva Atualizada"))
                .andExpect(jsonPath("$.email").value("maria.atualizada@teste.com"));
    }

    @Test
    @DisplayName("GET /api/clientes/email/{email} - Deve buscar cliente por email")
    @WithMockUser
    void testBuscarClientePorEmail_EmailExistente_DeveRetornar200() throws Exception {
        // Given
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // When & Then
        mockMvc.perform(get("/api/clientes/email/{email}", clienteSalvo.getEmail())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("maria@teste.com"))
                .andExpect(jsonPath("$.nome").value("Maria Silva Santos"));
    }

    @Test
    @DisplayName("PATCH /api/clientes/{id}/status - Deve ativar/desativar cliente")
    @WithMockUser
    void testAlterarStatusCliente_DeveRetornar200() throws Exception {
        // Given
        Cliente clienteSalvo = clienteRepository.save(cliente);

        // When & Then
        mockMvc.perform(patch("/api/clientes/{id}/status", clienteSalvo.getId())
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clienteSalvo.getId()));
    }

    @Test
    @DisplayName("GET /api/clientes/nome/{nome} - Deve buscar clientes por nome")
    @WithMockUser
    void testBuscarClientesPorNome_DeveRetornar200() throws Exception {
        // Given
        clienteRepository.save(cliente);

        // When & Then
        mockMvc.perform(get("/api/clientes/nome/{nome}", "Silva")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nome").value("Maria Silva Santos"));
    }

    @Test
    @DisplayName("POST /api/clientes - Deve validar email duplicado")
    @WithMockUser
    void testCriarCliente_EmailDuplicado_DeveRetornar400() throws Exception {
        // Given - primeiro cliente
        clienteRepository.save(cliente);
        
        // Segundo cliente com mesmo email
        ClienteRequest clienteDuplicado = new ClienteRequest();
        clienteDuplicado.setNome("Outro Cliente Santos");
        clienteDuplicado.setEmail("maria@teste.com"); // Email duplicado
        clienteDuplicado.setTelefone("11999999999");
        clienteDuplicado.setEndereco("Outro Endereço");

        // When & Then
        mockMvc.perform(post("/api/clientes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteDuplicado)))
                .andExpect(status().isBadRequest());
    }
}
