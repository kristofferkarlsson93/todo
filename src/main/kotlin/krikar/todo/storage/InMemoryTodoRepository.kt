package krikar.todo.storage

import krikar.todo.Todo
import krikar.todo.TodoId
import krikar.todo.TodoStatus
import krikar.todo.storage.models.PersistedTodo
import krikar.todo.storage.models.toPersistedTodo
import krikar.todo.storage.models.toTodo
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryTodoRepository : TodoRepository {

    private val store = ConcurrentHashMap<TodoId, PersistedTodo>()

    override fun insert(todo: Todo): Todo {
        store[todo.id] = todo.toPersistedTodo()
        return todo
    }

    override fun update(id: TodoId, transform: (Todo) -> Todo): Todo? {
        var result: Todo? = null
        store.computeIfPresent(id) { _, current ->
            val updated = transform(current.toTodo())
            result = updated
            updated.toPersistedTodo()
        }
        return result
    }

    override fun delete(id: TodoId): Boolean {
        return store.remove(id) != null
    }

    override fun getById(id: TodoId): Todo? {
        return store[id]?.toTodo()
    }

    override fun listAll(statusFilter: TodoStatus?): List<Todo> =
        store.values
            .filter { statusFilter == null || it.status == statusFilter }
            .map { it.toTodo() }
}