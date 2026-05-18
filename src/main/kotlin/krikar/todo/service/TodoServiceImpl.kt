package krikar.todo.service

import krikar.todo.Todo
import krikar.todo.TodoDescription
import krikar.todo.TodoId
import krikar.todo.TodoTitle
import krikar.todo.TodoStatus
import krikar.todo.api.models.CreateTodoRequest
import krikar.todo.api.models.UpdateTodoRequest
import krikar.todo.api.models.Patch
import krikar.todo.storage.TodoRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

@Service
class TodoServiceImpl(val storage: TodoRepository) : TodoService {
    override fun listAll(status: TodoStatus?): List<Todo> {
        return storage.listAll(status)
    }

    override fun getById(id: TodoId): Todo? {
        return storage.getById(id)
    }

    override fun create(request: CreateTodoRequest): Todo {
        val now = OffsetDateTime.now()
        val newTodo = Todo(
            id = TodoId.random(),
            title = TodoTitle(request.title),
            description = request.description?.let { TodoDescription(it) },
            status = TodoStatus.OPEN,
            createdAt = now,
            updatedAt = now
        )
        return storage.insert(newTodo)
    }

    override fun update(id: TodoId, request: UpdateTodoRequest): UpdateResult {
        val updated = storage.update(id) { current ->
            current.copy(
                title = request.title?.let { TodoTitle(it) } ?: current.title,
                description = when (val description = request.description) {
                    is Patch.Absent -> current.description
                    is Patch.Nulled -> null
                    is Patch.Present -> TodoDescription(description.value)
                },
                status = request.status ?: current.status,
                updatedAt = OffsetDateTime.now()
            )
        }
        return if (updated != null) UpdatedTodo(updated) else FailedToUpdateTodo(NotFound)
    }

    override fun delete(id: TodoId): Boolean {
        return storage.delete(id)
    }
}