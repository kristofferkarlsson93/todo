package krikar.todo.api.models

import krikar.todo.Todo
import krikar.todo.TodoStatus
import java.time.OffsetDateTime
import java.util.UUID

data class TodoResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val status: TodoStatus,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
) {
    companion object {
        fun from(todo: Todo): TodoResponse = TodoResponse(
            id = todo.id.value,
            title = todo.title.value,
            description = todo.description?.value,
            status = todo.status,
            createdAt = todo.createdAt,
            updatedAt = todo.updatedAt,
        )
    }
}