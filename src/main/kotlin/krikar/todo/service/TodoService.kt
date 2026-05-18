package krikar.todo.service

import krikar.todo.Todo
import krikar.todo.TodoId
import krikar.todo.TodoStatus
import krikar.todo.api.models.CreateTodoRequest
import krikar.todo.api.models.UpdateTodoRequest

interface TodoService {
    fun listAll(status: TodoStatus? = null): List<Todo>

    fun getById(id: TodoId): Todo?

    fun create(request: CreateTodoRequest): Todo

    fun update(id: TodoId, request: UpdateTodoRequest): UpdateResult

    fun delete(id: TodoId): Boolean
}