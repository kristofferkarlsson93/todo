package krikar.todo.storage

import krikar.todo.Todo
import krikar.todo.TodoId
import krikar.todo.TodoStatus

interface TodoRepository {
    fun insert(todo: Todo): Todo

    fun update(id: TodoId, transform: (Todo) -> Todo): Todo?

    fun delete(id: TodoId): Boolean

    fun getById(id: TodoId): Todo?

    fun listAll(statusFilter: TodoStatus?): List<Todo>
}