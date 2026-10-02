package com.triagedesk.ticket_service.ticket;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository repository;

    public TicketController(TicketRepository repository) {
        this.repository = repository;
    }

    // The shape of the data the client sends when creating a ticket
    public record CreateTicketRequest(
            String title,
            String description,
            String priority,
            String createdBy) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket create(@RequestBody CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        if (request.priority() != null) {
            ticket.setPriority(request.priority());
        }
        ticket.setCreatedBy(request.createdBy());
        return repository.save(ticket);
    }

    @GetMapping
    public List<Ticket> list() {
        return repository.findAll();
    }
}