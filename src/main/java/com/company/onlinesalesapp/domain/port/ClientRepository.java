package com.company.onlinesalesapp.domain.port;

import com.company.onlinesalesapp.domain.model.Client;

import java.util.Optional;

public interface ClientRepository {
    Optional<Client> findById(String clientId);
    Client save(Client client);
}
