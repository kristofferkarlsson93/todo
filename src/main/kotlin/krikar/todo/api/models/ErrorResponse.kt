package krikar.todo.api.models

data class ErrorResponse(
    val reason: String,
    val message: String,
)