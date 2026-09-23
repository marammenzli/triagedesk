# API Contract

Base URL (through the gateway): `http://localhost:8080`

## Tickets (ticket-service)

| Method | Path                          | Description                          | Request body                                  | Success response |
|--------|--------------------------------|---------------------------------------|-----------------------------------------------|-------------------|
| POST   | /api/tickets                  | Create a ticket                       | `{ title, description, priority }`            | 201 + ticket      |
| GET    | /api/tickets                  | List tickets (filters: status, priority, page, size) | —                        | 200 + list        |
| GET    | /api/tickets/{id}              | Get one ticket                        | —                                              | 200 + ticket      |
| PUT    | /api/tickets/{id}              | Update a ticket                       | `{ title, description, priority }`            | 200 + ticket      |
| PATCH  | /api/tickets/{id}/status        | Change status only                    | `{ status }`                                   | 200 + ticket      |
| PATCH  | /api/tickets/{id}/assign        | Assign ticket to an agent             | `{ agentId }`                                  | 200 + ticket      |
| POST   | /api/tickets/{id}/comments      | Add a comment                         | `{ author, body }`                             | 201 + comment     |
| GET    | /api/tickets/{id}/comments      | List comments on a ticket             | —                                              | 200 + list        |

## Agents (agent-service)

| Method | Path              | Description                | Request body                              | Success response |
|--------|-------------------|-----------------------------|--------------------------------------------|-------------------|
| POST   | /api/agents       | Create an agent             | `{ name, email, team, skills }`            | 201 + agent       |
| GET    | /api/agents       | List agents (filter: team, active) | —                                    | 200 + list        |
| GET    | /api/agents/{id}   | Get one agent                | —                                          | 200 + agent       |
| PUT    | /api/agents/{id}   | Update an agent              | `{ name, team, skills, active }`           | 200 + agent       |

## Ticket statuses

`OPEN` → `IN_PROGRESS` → `RESOLVED` (or `CLOSED`)

## Priorities

`LOW`, `MEDIUM`, `HIGH`