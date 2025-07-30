//mudança 16/07
package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.request.ClienteRequest;
import com.deliverytech.delivery_api.model.Cliente;


import java.util.List;
import java.util.Optional;

/**
 * Interface de serviços para gerenciamento de clientes
 */

public interface ClienteService {

  
    Cliente cadastrar(ClienteRequest clienteRequest);
    
    Optional<Cliente> buscarPorId(Long id);
    

    Optional<Cliente> buscarPorEmail(String email);
    

    List<Cliente> listarAtivos();
    

    List<Cliente> buscarPorNome(String nome);
    

    Cliente atualizar(Long id, ClienteRequest clienteRequest);
    

    void inativar(Long id);

    // ADICIONAR APENAS ESTE MÉTODO (para atender atividade)
    /**
     * Ativar/Desativar cliente (toggle status ativo)
     * @param id ID do cliente
     * @return cliente com status alterado
     */
    Cliente ativarDesativarCliente(Long id);

}