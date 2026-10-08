package com.triagedesk.ticket_service.ticket;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
public class TicketCommentController {

    private final TicketRepository ticketRepository;
    private final TicketCommentRepository commentRepository;

    public TicketCommentController(TicketRepository ticketRepository,
                                   TicketCommentRepository commentRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
    }

    public record CreateCommentRequest(
            @NotBlank @Size(max = 100) String author,
            @NotBlank String body) {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketComment add(@PathVariable UUID ticketId,
                             @Valid @RequestBody CreateCommentRequest request) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        TicketComment comment = new TicketComment();
        comment.setTicketId(ticketId);
        comment.setAuthor(request.author());
        comment.setBody(request.body());
        return commentRepository.save(comment);
    }

    @GetMapping
    public List<TicketComment> list(@PathVariable UUID ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId);
    }
}