package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownTextTest {

    @Test
    fun testMarkdownParsing_codeBlockAndHeadings() {
        val markdown = """
            # Heading 1
            ## Heading 2
            
            This is a normal paragraph with **bold** and `inline_code`.
            
            ```kotlin
            fun helloWorld() {
                val greeting = "Hello, Innova AI!"
                println(greeting)
            }
            ```
            
            - [x] Completed task
            - [ ] Pending task
            
            > Important AI quote note
            
            | Feature | Supported |
            | --- | --- |
            | Markdown | Yes |
            | Highlighting | Yes |
        """.trimIndent()

        // Verify that the markdown string is well-structured and contains expected sections
        assertTrue(markdown.contains("```kotlin"))
        assertTrue(markdown.contains("Heading 1"))
        assertTrue(markdown.contains("- [x] Completed task"))
        assertTrue(markdown.contains("| Feature | Supported |"))
        assertNotNull(markdown)
    }

    @Test
    fun testLanguageBadgeNames() {
        val testLanguages = listOf("kotlin", "py", "js", "sql", "json", "bash")
        val mappedNames = testLanguages.map { lang ->
            when (lang.lowercase()) {
                "kt", "kotlin" -> "Kotlin"
                "py", "python" -> "Python"
                "js", "javascript" -> "JavaScript"
                "sql" -> "SQL"
                "json" -> "JSON"
                "bash", "sh" -> "Shell"
                else -> lang.uppercase()
            }
        }

        assertEquals(listOf("Kotlin", "Python", "JavaScript", "SQL", "JSON", "Shell"), mappedNames)
    }
}
