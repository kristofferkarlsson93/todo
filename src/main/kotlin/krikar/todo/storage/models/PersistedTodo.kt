package krikar.todo.storage.models

import krikar.todo.TodoDescription
import krikar.todo.TodoId
import krikar.todo.TodoStatus
import krikar.todo.TodoTitle
import java.time.OffsetDateTime

data class PersistedTodo(
    val id: TodoId,
    val title: TodoTitle,
    val description: TodoDescription?,
    val status: TodoStatus,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)