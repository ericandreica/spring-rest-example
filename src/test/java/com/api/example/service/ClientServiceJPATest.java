package com.api.example.service;

import com.api.example.domain.Client;
import com.api.example.exception.ResourceNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientServiceJPATest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ClientService clientService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testSaveUpdateDeleteClient() throws Exception {
        Client c = new Client();
        c.setName("Portgas D. Ace");
        c.setId("9012345678");
        c.setEmail("ace@whitebeard.com");

        clientService.save(c);
        assertNotNull(c.getId());

        Client findClient = clientService.findById(c.getId());
        assertEquals("Portgas D. Ace", findClient.getName());
        assertEquals("ace@whitebeard.com", findClient.getEmail());

        // update record
        c.setEmail("ace@whitebeardpirat.es");
        clientService.update(c);

        // test after update
        findClient = clientService.findById(c.getId());
        assertEquals("ace@whitebeardpirat.es", findClient.getEmail());

        // test delete
        clientService.deleteById(c.getId());

        // query after delete
        exceptionRule.expect(ResourceNotFoundException.class);
        clientService.findById(c.getId());
    }
}