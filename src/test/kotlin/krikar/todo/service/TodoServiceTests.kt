package krikar.todo.service

import krikar.todo.Todo
import krikar.todo.TodoDescription
import krikar.todo.TodoId
import krikar.todo.TodoStatus
import krikar.todo.TodoTitle
import krikar.todo.api.models.Patch
import krikar.todo.api.models.UpdateTodoRequest
import krikar.todo.api.models.CreateTodoRequest
import krikar.todo.storage.TodoRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class TodoServiceTests {

    private val repository = mock<TodoRepository>()
    private val service = TodoServiceImpl(repository)

    private fun aTodo(
        title: String = "Buy milk",
        description: String? = null,
        status: TodoStatus = TodoStatus.OPEN,
    ) = Todo(
        id = TodoId(UUID.randomUUID()),
        title = TodoTitle(title),
        description = description?.let { TodoDescription(it) },
        status = status,
        createdAt = OffsetDateTime.now(),
        updatedAt = OffsetDateTime.now(),
    )

    @Nested
    inner class `update` {
        private val existingTodo = aTodo("Original title", "Original description")

        @BeforeEach
        fun setUp() {
            whenever(repository.update(any(), any())).thenAnswer { invocation ->
                val transform = invocation.getArgument<(Todo) -> Todo>(1)
                transform(existingTodo)
            }
        }

        @Test
        fun `should return UpdatedTodo with new title`() {
            val request = UpdateTodoRequest(title = "New title", description = Patch.Absent, status = null)
            val result = service.update(existingTodo.id, request)
            assertIs<UpdatedTodo>(result)
            assertEquals("New title", result.todo.title.value)
        }

        @Test
        fun `should keep existing title when title is null in request`() {
            val request = UpdateTodoRequest(title = null, description = Patch.Absent, status = null)
            val result = service.update(existingTodo.id, request)
            assertIs<UpdatedTodo>(result)
            assertEquals("Original title", result.todo.title.value)
        }

        @Test
        fun `should keep existing description when Patch is Absent`() {
            val request = UpdateTodoRequest(title = null, description = Patch.Absent, status = null)
            val result = service.update(existingTodo.id, request)
            assertIs<UpdatedTodo>(result)
            assertEquals("Original description", result.todo.description?.value)
        }

        @Test
        fun `should clear description when Patch is Nulled`() {
            val request = UpdateTodoRequest(title = null, description = Patch.Nulled, status = null)
            val result = service.update(existingTodo.id, request)
            assertIs<UpdatedTodo>(result)
            assertNull(result.todo.description)
        }

        @Test
        fun `should update description when Patch is Present`() {
            val request = UpdateTodoRequest(title = null, description = Patch.Present("New description"), status = null)
            val result = service.update(existingTodo.id, request)
            assertIs<UpdatedTodo>(result)
            assertEquals("New description", result.todo.description?.value)
        }

        @Test
        fun `should update status to COMPLETED`() {
            val request = UpdateTodoRequest(title = null, description = Patch.Absent, status = TodoStatus.COMPLETED)
            val result = service.update(existingTodo.id, request)
            assertIs<UpdatedTodo>(result)
            assertEquals(TodoStatus.COMPLETED, result.todo.status)
        }

        @Test
        fun `should keep existing status when status is null in request`() {
            val request = UpdateTodoRequest(title = null, description = Patch.Absent, status = null)
            val result = service.update(existingTodo.id, request)
            assertIs<UpdatedTodo>(result)
            assertEquals(TodoStatus.OPEN, result.todo.status)
        }

        @Test
        fun `should return FailedToUpdateTodo with NotFound when todo does not exist`() {
            whenever(repository.update(any(), any())).thenReturn(null)
            val request = UpdateTodoRequest(title = "New title", description = Patch.Absent, status = null)
            val result = service.update(existingTodo.id, request)
            assertIs<FailedToUpdateTodo>(result)
            assertIs<NotFound>(result.reason)
        }
    }

    @Nested
    inner class `create` {
        @Test
        fun `should create todo with OPEN status`() {
            whenever(repository.insert(any())).thenAnswer { it.getArgument(0) }
            val request = CreateTodoRequest(title = "New todo", description = "A description")
            val result = service.create(request)
            assertEquals("New todo", result.title.value)
            assertEquals("A description", result.description?.value)
            assertEquals(TodoStatus.OPEN, result.status)
        }

        @Test
        fun `should create todo without description`() {
            whenever(repository.insert(any())).thenAnswer { it.getArgument(0) }
            val request = CreateTodoRequest(title = "No description", description = null)
            val result = service.create(request)
            assertNull(result.description)
        }
    }
}
