package com.triagedesk.ticket_service.ticket;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository repository;

    public TicketController(TicketRepository repository) {
        this.repository = repository;
    }

    public record CreateTicketRequest(
            @NotBlank @Size(max = 200) String title,
            @NotBlank String description,
            @Pattern(regexp = "LOW|MEDIUM|HIGH|URGENT",
                     message = "must be LOW, MEDIUM, HIGH or URGENT") String priority,
            @NotBlank @Size(max = 100) String createdBy) {
    }

    public record UpdateStatusRequest(
            @NotBlank
            @Pattern(regexp = "OPEN|IN_PROGRESS|RESOLVED|CLOSED",
                     message = "must be OPEN, IN_PROGRESS, RESOLVED or CLOSED") String status) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket create(@Valid @RequestBody CreateTicketRequest request) {
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
    public List<Ticket> list(@RequestParam(required = false) String status) {
        if (status != null) {
            return repository.findByStatus(status);
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Ticket getById(@PathVariable UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    @PatchMapping("/{id}/status")
    public Ticket updateStatus(@PathVariable UUID id,
                               @Valid @RequestBody UpdateStatusRequest request) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket not found"));
        ticket.setStatus(request.status());
        return repository.save(ticket);
    }
}