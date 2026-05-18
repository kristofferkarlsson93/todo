package krikar.todo

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class TodoValueClassTests {

    @Nested
    inner class `TodoTitle` {
        @Test
        fun `should create valid title`() {
            val title = TodoTitle("Buy milk")
            assertEquals("Buy milk", title.value)
        }

        @Test
        fun `should accept title at max length`() {
            val title = TodoTitle("a".repeat(200))
            assertEquals(200, title.value.length)
        }

        @Test
        fun `should throw when title is blank`() {
            assertFailsWith<IllegalArgumentException> {
                TodoTitle("   ")
            }
        }

        @Test
        fun `should throw when title is empty`() {
            assertFailsWith<IllegalArgumentException> {
                TodoTitle("")
            }
        }

        @Test
        fun `should throw when title exceeds max length`() {
            assertFailsWith<IllegalArgumentException> {
                TodoTitle("a".repeat(201))
            }
        }
    }

    @Nested
    inner class `TodoDescription` {
        @Test
        fun `should create valid description`() {
            val description = TodoDescription("A helpful description")
            assertEquals("A helpful description", description.value)
        }

        @Test
        fun `should accept description at max length`() {
            val description = TodoDescription("a".repeat(2000))
            assertEquals(2000, description.value.length)
        }

        @Test
        fun `should throw when description exceeds max length`() {
            assertFailsWith<IllegalArgumentException> {
                TodoDescription("a".repeat(2001))
            }
        }
    }
}
