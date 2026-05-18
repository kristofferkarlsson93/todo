package krikar.todo.api

import jakarta.validation.Valid
import krikar.todo.TodoId
import krikar.todo.TodoStatus
import krikar.todo.api.models.TodoResponse
import krikar.todo.service.TodoService
import krikar.todo.api.models.CreateTodoRequest
import krikar.todo.api.models.ErrorResponse
import krikar.todo.api.models.UpdateTodoRequest
import krikar.todo.service.FailedToUpdateTodo
import krikar.todo.service.NotFound
import krikar.todo.service.UpdatedTodo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@RestController
@RequestMapping("/todos")
class TodoController(
    private val todoService: TodoService,
) {
    @GetMapping
    fun listTodos(@RequestParam status: TodoStatus? = null): List<TodoResponse> =
        todoService.listAll(status).map(TodoResponse.Companion::from)

    @GetMapping("/{id}")
    fun getTodo(@PathVariable id: UUID): ResponseEntity<Any> {
        val todo = todoService.getById(TodoId(id))
            ?: return todoNotFound()

        return ResponseEntity.ok(TodoResponse.from(todo))
    }

    @PostMapping
    fun createTodo(@Valid @RequestBody request: CreateTodoRequest): ResponseEntity<TodoResponse> {
        val todo = todoService.create(request)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(TodoResponse.from(todo))
    }

    @PatchMapping("/{id}")
    fun updateTodo(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateTodoRequest,
    ): ResponseEntity<Any> = when (val result = todoService.update(TodoId(id), request)) {
        is UpdatedTodo -> ResponseEntity.ok(TodoResponse.from(result.todo))
        is FailedToUpdateTodo -> when (result.reason) {
            is NotFound -> todoNotFound()
        }
    }

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: UUID): ResponseEntity<Any> {
        if (!todoService.delete(TodoId(id))) {
            return todoNotFound()
        }

        return ResponseEntity.noContent().build()
    }

    private fun todoNotFound(): ResponseEntity<Any> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse("TODO_NOT_FOUND", "Todo was not found"))
}