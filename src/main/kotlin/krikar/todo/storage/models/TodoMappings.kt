package krikar.todo.storage.models

import krikar.todo.Todo

fun Todo.toPersistedTodo() = PersistedTodo(
    id = id,
    title = title,
    description = description,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun PersistedTodo.toTodo() = Todo(
    id = id,
    title = title,
    description = description,
    status = status,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
