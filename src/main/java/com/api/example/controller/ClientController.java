package com.api.example.controller;

import com.api.example.domain.Client;
import com.api.example.exception.BadResourceException;
import com.api.example.exception.ResourceAlreadyExistsException;
import com.api.example.exception.ResourceNotFoundException;
import com.api.example.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ClientController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClientService clientService;

    @Operation(summary = "Get all clients")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Get clients")})
    @GetMapping(value = "/clients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Client>> findAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @Operation(summary = "Get client by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the client",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Client.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content)})
    @GetMapping(value = "/clients/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Client> findClientById(@Parameter(description = "id of client to be searched") @PathVariable String clientId) {
        try {
            Client book = clientService.findById(clientId);
            return ResponseEntity.ok(book);  // return 200, with json body
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // return 404, with null body
        }
    }

    @Operation(summary = "Create new client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Client.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to create client", content = @Content),
            @ApiResponse(responseCode = "409", description = "Client already exists", content = @Content)})
    @PostMapping(value = "/clients")
    public ResponseEntity<Client> addClient(@Valid @RequestBody Client client)
            throws URISyntaxException {
        try {
            Client newClient = clientService.save(client);
            return ResponseEntity.created(new URI("/api/clients/" + newClient.getId()))
                    .body(client);
        } catch (ResourceAlreadyExistsException ex) {
            // log exception first, then return Conflict (409)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (BadResourceException ex) {
            // log exception first, then return Bad Request (400)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Update existing client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Client.class))}),
            @ApiResponse(responseCode = "400", description = "Failed to update client", content = @Content),
            @ApiResponse(responseCode = "404", description = "Client does not exists", content = @Content)})
    @PutMapping(value = "/clients/{clientId}")
    public ResponseEntity<Client> updateClient(@Valid @RequestBody Client client,
                                               @PathVariable String clientId) {
        try {
            client.setId(clientId);
            clientService.update(client);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            // log exception first, then return Not Found (404)
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BadResourceException ex) {
            // log exception first, then return Bad Request (400)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Update client information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client updated"),
            @ApiResponse(responseCode = "400", description = "Failed to update client", content = @Content),
            @ApiResponse(responseCode = "404", description = "Client does not exists", content = @Content)})
    @PatchMapping("/clients/{clientId}")
    public ResponseEntity<Void> updateClientInfo(@PathVariable String clientId,
                                                 @RequestBody Client client) {
        try {
            clientService.updateNameEmail(clientId, client.getName(), client.getEmail());
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            // log exception first, then return Not Found (404)
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        } catch (BadResourceException ex) {
            // log exception first, then return Bad Request (400)
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Delete client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client deleted"),
            @ApiResponse(responseCode = "404", description = "Client does not exists", content = @Content)})
    @DeleteMapping(path = "/clients/{clientId}")
    public ResponseEntity<Void> deleteClientById(@PathVariable String clientId) {
        try {
            clientService.deleteById(clientId);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}