package com.api.example.service;

import com.api.example.domain.Client;
import com.api.example.exception.BadResourceException;
import com.api.example.exception.ResourceAlreadyExistsException;
import com.api.example.exception.ResourceNotFoundException;
import com.api.example.repository.ClientRepository;
import com.api.example.specification.ClientSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    private boolean existsById(String id) {
        return clientRepository.existsById(id);
    }

    public Client findById(String id) throws ResourceNotFoundException {
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            throw new ResourceNotFoundException("Cannot find Client with id: " + id);
        } else return client;
    }

    public List<Client> findAllFromPage(int pageNumber, int rowPerPage) {
        List<Client> clients = new ArrayList<>();
        clientRepository.findAll(PageRequest.of(pageNumber - 1, rowPerPage)).forEach(clients::add);
        return clients;
    }

    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        clientRepository.findAll().forEach(clients::add);
        return clients;
    }

    public List<Client> findAllByName(String name) {
        Client filter = new Client();
        filter.setName(name);
        Specification<Client> spec = new ClientSpecification(filter);

        return new ArrayList<>(clientRepository.findAll(spec));
    }

    public Client save(Client client) throws BadResourceException, ResourceAlreadyExistsException {
        if (!ObjectUtils.isEmpty(client.getName())) {
            if (client.getId() != null && existsById(client.getId())) {
                throw new ResourceAlreadyExistsException("Client with id: " + client.getId() +
                        " already exists");
            }
            return clientRepository.save(client);
        } else {
            BadResourceException exc = new BadResourceException("Failed to save client");
            exc.addErrorMessage("Client name is null or empty");
            throw exc;
        }
    }

    public void update(Client client)
            throws BadResourceException, ResourceNotFoundException {
        if (!ObjectUtils.isEmpty(client.getName())) {
            if (!existsById(client.getId())) {
                throw new ResourceNotFoundException("Cannot find Client with id: " + client.getId());
            }
            clientRepository.save(client);
        } else {
            BadResourceException exc = new BadResourceException("Failed to save client");
            exc.addErrorMessage("Client name is null or empty");
            throw exc;
        }
    }

    public void updateNameEmail(String id, String name, String email)
            throws ResourceNotFoundException, BadResourceException {
        Client client = findById(id);
        if (!existsById(client.getId())) {
            throw new ResourceNotFoundException("Cannot find Client with id: " + client.getId());
        }
        if (ObjectUtils.isEmpty(name) && ObjectUtils.isEmpty(email)) {
            BadResourceException exc = new BadResourceException("Failed to update client");
            exc.addErrorMessage("Client name and email can't be empty");
            throw exc;
        } else {
            if (!ObjectUtils.isEmpty(name))
                client.setName(name);
            if (!ObjectUtils.isEmpty(email))
                client.setEmail(email);
            clientRepository.save(client);
        }
    }


    public void deleteById(String id) throws ResourceNotFoundException {
        if (!existsById(id)) {
            throw new ResourceNotFoundException("Cannot find client with id: " + id);
        } else {
            clientRepository.deleteById(id);
        }
    }

    public Long count() {
        return clientRepository.count();
    }
}