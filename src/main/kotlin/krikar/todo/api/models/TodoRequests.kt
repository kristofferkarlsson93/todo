package krikar.todo.api.models

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import krikar.todo.TodoDescription
import krikar.todo.TodoStatus
import krikar.todo.TodoTitle

data class CreateTodoRequest(
    @field:NotBlank
    @field:Size(max = TodoTitle.MAX_LENGTH)
    val title: String,

    @field:Size(max = TodoDescription.MAX_LENGTH)
    val description: String?,
)

data class UpdateTodoRequest(
    @field:Size(max = TodoTitle.MAX_LENGTH)
    val title: String?,

    val description: Patch<String> = Patch.Absent,

    val status: TodoStatus?,
) {
    @AssertTrue(message = "title must not be blank")
    fun isTitleValid(): Boolean = title == null || title.isNotBlank()

    @AssertTrue(message = "description must not exceed ${TodoDescription.MAX_LENGTH} characters")
    fun isDescriptionValid(): Boolean =
        description !is Patch.Present || description.value.length <= TodoDescription.MAX_LENGTH

    @AssertTrue(message = "at least one field must be provided")
    fun isNotEmpty(): Boolean = title != null || description !is Patch.Absent || status != null
}
