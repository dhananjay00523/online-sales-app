package com.company.onlinesalesapp.adapters.persistence;

import com.company.onlinesalesapp.domain.model.Client;
import com.company.onlinesalesapp.domain.port.ClientRepository;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryClientRepository implements ClientRepository {

    private final Map<String, Client> storage = new ConcurrentHashMap<>();

    @Override
    public Optional<Client> findById(String clientId) {
        return Optional.ofNullable(storage.get(clientId));
    }

    @Override
    public Client save(Client client) {
        storage.put(client.clientId(), client);
        return client;
    }
}
