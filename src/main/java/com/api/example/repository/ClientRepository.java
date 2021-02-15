package com.api.example.repository;

import com.api.example.domain.Client;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ClientRepository extends PagingAndSortingRepository<Client, String>, JpaSpecificationExecutor<Client> {
}
