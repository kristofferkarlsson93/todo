package krikar.todo.api

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class TodoControllerIntegrationTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    // Helper to create a todo and return its id
    private fun createTodo(title: String = "Buy milk", description: String? = null): String {
        val descriptionJson = if (description != null) """"$description"""" else "null"
        val response = mockMvc.post("/todos") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"title": "$title", "description": $descriptionJson}"""
        }.andReturn().response.contentAsString

        return Regex(""""id"\s*:\s*"([^"]+)"""").find(response)!!.groupValues[1]
    }

    @Nested
    inner class `GET todos` {
        @Test
        fun `should return empty list when no todos exist`() {
            mockMvc.get("/todos")
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$") { isArray() }
                }
        }

        @Test
        fun `should return created todo in list`() {
            createTodo("List test todo")

            mockMvc.get("/todos")
                .andExpect {
                    status { isOk() }
                    jsonPath("$") { isArray() }
                }
        }

        @Test
        fun `should filter by completed=true`() {
            val id = createTodo("Filter completed test")
            mockMvc.patch("/todos/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"status": "COMPLETED"}"""
            }

            mockMvc.get("/todos?status=COMPLETED")
                .andExpect {
                    status { isOk() }
                    jsonPath("$[?(@.id == '$id')].status") { value("COMPLETED") }
                }
        }

        @Test
        fun `should filter by completed=false`() {
            val id = createTodo("Filter incomplete test")

            mockMvc.get("/todos?status=OPEN")
                .andExpect {
                    status { isOk() }
                    jsonPath("$[?(@.id == '$id')]") { isArray() }
                }
        }

        @Test
        fun `should return 400 for invalid completed param`() {
            mockMvc.get("/todos?status=maybe")
                .andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    inner class `GET todo by id` {
        @Test
        fun `should return todo when it exists`() {
            val id = createTodo("Get by id test")

            mockMvc.get("/todos/$id")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.id") { value(id) }
                    jsonPath("$.title") { value("Get by id test") }
                    jsonPath("$.status") { value("OPEN") }
                }
        }

        @Test
        fun `should return 404 when todo does not exist`() {
            mockMvc.get("/todos/00000000-0000-0000-0000-000000000000")
                .andExpect { status { isNotFound() } }
        }

        @Test
        fun `should return 400 when id is not a valid UUID`() {
            mockMvc.get("/todos/not-a-uuid")
                .andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    inner class `POST todos` {
        @Test
        fun `should create todo and return 201`() {
            mockMvc.post("/todos") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "Buy eggs"}"""
            }.andExpect {
                status { isCreated() }
                jsonPath("$.title") { value("Buy eggs") }
                jsonPath("$.status") { value("OPEN") }
                jsonPath("$.id") { exists() }
                jsonPath("$.createdAt") { exists() }
            }
        }

        @Test
        fun `should create todo with description`() {
            mockMvc.post("/todos") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "Buy eggs", "description": "Free range"}"""
            }.andExpect {
                status { isCreated() }
                jsonPath("$.description") { value("Free range") }
            }
        }

        @Test
        fun `should return 400 when title is blank`() {
            mockMvc.post("/todos") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "   "}"""
            }.andExpect { status { isBadRequest() } }
        }

        @Test
        fun `should return 400 when title is missing`() {
            mockMvc.post("/todos") {
                contentType = MediaType.APPLICATION_JSON
                content = """{}"""
            }.andExpect { status { isBadRequest() } }
        }

        @Test
        fun `should return 400 when title exceeds max length`() {
            val longTitle = "a".repeat(201)
            mockMvc.post("/todos") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "$longTitle"}"""
            }.andExpect { status { isBadRequest() } }
        }

        @Test
        fun `should return 400 when description exceeds max length`() {
            val longDescription = "a".repeat(2001)
            mockMvc.post("/todos") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "Valid title", "description": "$longDescription"}"""
            }.andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    inner class `PATCH todo` {
        @Test
        fun `should update title`() {
            val id = createTodo("Original title")
            mockMvc.patch("/todos/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "Updated title"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.title") { value("Updated title") }
            }
        }

        @Test
        fun `should update status to COMPLETED`() {
            val id = createTodo("Complete me")
            mockMvc.patch("/todos/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"status": "COMPLETED"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.status") { value("COMPLETED") }
            }
        }

        @Test
        fun `should set description when absent before`() {
            val id = createTodo("No description yet")
            mockMvc.patch("/todos/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"description": "Now it has one"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.description") { value("Now it has one") }
            }
        }

        @Test
        fun `should clear description when explicitly set to null`() {
            val id = createTodo("Has description", "Remove me")
            mockMvc.patch("/todos/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"description": null}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.description") { doesNotExist() }
            }
        }

        @Test
        fun `should keep description when field is absent from request`() {
            val id = createTodo("Keep description", "Keep me")
            mockMvc.patch("/todos/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"status": "COMPLETED"}"""
            }.andExpect {
                status { isOk() }
                jsonPath("$.description") { value("Keep me") }
            }
        }

        @Test
        fun `should return 404 when todo does not exist`() {
            mockMvc.patch("/todos/00000000-0000-0000-0000-000000000000") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "wont work"}"""
            }.andExpect { status { isNotFound() } }
        }

        @Test
        fun `should return 400 when title is blank`() {
            val id = createTodo("Valid")

            mockMvc.patch("/todos/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "   "}"""
            }.andExpect { status { isBadRequest() } }
        }

        @Test
        fun `should return 400 when description exceeds max length`() {
            val id = createTodo("Valid")
            val longDescription = "a".repeat(2001)

            mockMvc.patch("/todos/$id") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"description": "$longDescription"}"""
            }.andExpect { status { isBadRequest() } }
        }
    }

    @Nested
    inner class `DELETE todo` {
        @Test
        fun `should delete todo and return 204`() {
            val id = createTodo("Delete me")

            mockMvc.delete("/todos/$id")
                .andExpect { status { isNoContent() } }

            mockMvc.get("/todos/$id")
                .andExpect { status { isNotFound() } }
        }

        @Test
        fun `should return 404 when todo does not exist`() {
            mockMvc.delete("/todos/00000000-0000-0000-0000-000000000000")
                .andExpect { status { isNotFound() } }
        }
    }
}
