# Todo API

A simple REST API for managing todo items, built with Kotlin and Spring Boot.

## Background

This is a coding test submission. I do not have prior Kotlin experience, but with a background in Scala, many features felt similar.

## Tech

- Kotlin + Spring Boot 4
- Gradle
- In-memory storage (non-persistent by design — restarting the server clears all todos)

## Running the server

```bash
./gradlew bootRun
```

Server starts on port **9000**.

## Running the tests

```bash
./gradlew test
```

## Schemas

### Todo (response)
```json
{
  "id": "uuid",
  "title": "string (max 200 chars)",
  "description": "string (max 2000 chars) | null",
  "status": "OPEN | COMPLETED",
  "createdAt": "2026-01-01T12:00:00Z",
  "updatedAt": "2026-01-01T12:00:00Z"
}
```

### Create todo (POST body)
```json
{
  "title": "string, required, max 200 chars",
  "description": "string (optional, max 2000 chars) | null"
}
```

### Update todo (PATCH body)
All fields are optional — only the ones you include are updated.
```json
{
  "title": "string | null (null does nothing)",
  "description": "string | null  ()null clears it, omitting keeps existing)",
  "status": "OPEN | COMPLETED | null"
}
```

## API


### Create a todo

Minimal
```
curl -s -X POST http://localhost:9000/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Remember to lock the door"}' | jq
```

With description
```
curl -s -X POST http://localhost:9000/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Remember to lock the door", "description": "Use an appropriate key"}' | jq
```

### List todos

All todos
```
curl -s http://localhost:9000/todos | jq

```
Only open todos
```
curl -s "http://localhost:9000/todos?status=OPEN" | jq
```

Only completed
```
curl -s "http://localhost:9000/todos?status=COMPLETED" | jq
```

### Get a todo

```
curl -s http://localhost:9000/todos/YOUR-UUID-HERE | jq
```

### Update a todo (PATCH)

Only the fields you include are updated. Omitting a field leaves it unchanged.

Update title
```
curl -s -X PATCH http://localhost:9000/todos/YOUR-UUID-HERE \
  -H "Content-Type: application/json" \
  -d '{"title": "Buy oat milk"}' | jq
```

Mark as completed
```
curl -s -X PATCH http://localhost:9000/todos/YOUR-UUID-HERE \
  -H "Content-Type: application/json" \
  -d '{"status": "COMPLETED"}' | jq

```

Reopen
```
curl -s -X PATCH http://localhost:9000/todos/YOUR-UUID-HERE \
  -H "Content-Type: application/json" \
  -d '{"status": "OPEN"}' | jq
```

Add or update description
```
curl -s -X PATCH http://localhost:9000/todos/YOUR-UUID-HERE \
  -H "Content-Type: application/json" \
  -d '{"description": "It is a small key"}' | jq

```

# Clear description (explicitly set to null — omitting it would keep the existing value)
```
curl -s -X PATCH http://localhost:9000/todos/YOUR-UUID-HERE \
  -H "Content-Type: application/json" \
  -d '{"description": null}' | jq
```

### Delete a todo

```
curl -s -X DELETE http://localhost:9000/todos/YOUR-UUID-HERE -v
```