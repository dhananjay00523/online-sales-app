package com.company.onlinesalesapp.adapters.persistence;

import org.junit.jupiter.api.BeforeEach;

class InMemoryClientRepositoryTest {

    private InMemoryClientRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryClientRepository();
    }

}
