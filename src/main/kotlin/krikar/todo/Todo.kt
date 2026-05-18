package krikar.todo

import java.time.OffsetDateTime
import java.util.UUID

enum class TodoStatus {
    OPEN, COMPLETED
}

data class Todo(
    val id: TodoId,
    val title: TodoTitle,
    val description: TodoDescription?,
    val status: TodoStatus,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)

@JvmInline
value class TodoId(val value: UUID) {
    companion object {
        fun random(): TodoId {
            return TodoId(UUID.randomUUID())
        }
    }
}

@JvmInline
value class TodoTitle(val value: String) {
    companion object {
        const val MAX_LENGTH = 200
    }

    init {
        require(value.isNotBlank()) { "Title must not be blank" }
        require(value.length <= MAX_LENGTH) { "Title must not exceed $MAX_LENGTH characters" }
    }
}

@JvmInline
value class TodoDescription(val value: String) {
    companion object {
        const val MAX_LENGTH = 2000
    }

    init {
        require(value.length <= MAX_LENGTH) { "Description must not exceed $MAX_LENGTH characters" }
    }
}