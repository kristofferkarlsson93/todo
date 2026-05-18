package krikar.todo.service

import krikar.todo.Todo

sealed class UpdateResult
data class UpdatedTodo(val todo: Todo) : UpdateResult()
data class FailedToUpdateTodo(val reason: FailureReason) : UpdateResult()

sealed class FailureReason
data object NotFound : FailureReason()

