package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoIndigo
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceElevated
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.InnovaGlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimaryDark
import com.example.ui.theme.TextSecondaryDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Syntax color palette tailored for dark theme code blocks.
 */
private object SyntaxColors {
    val Background = Color(0xFF0D0B18)
    val HeaderBackground = Color(0xFF161228)
    val GutterBackground = Color(0xFF100D1F)
    val GutterText = Color(0xFF524F72)
    val GutterDivider = Color(0xFF251E44)
    val DefaultCode = Color(0xFFE2E8F0)

    val Keyword = Color(0xFFC084FC)        // Bright purple/magenta
    val StringLiteral = Color(0xFF34D399)  // Mint / emerald green
    val NumberLiteral = Color(0xFFFBBF24)  // Amber / gold
    val Comment = Color(0xFF818CF8)        // Soft muted slate / indigo italic
    val TypeOrClass = NeonCyan             // Cyan
    val Function = Color(0xFF60A5FA)       // Sky blue
    val Annotation = Color(0xFFF472B6)     // Rose pink
    val Operator = Color(0xFFA5B4FC)       // Lavender
    val BooleanLiteral = Color(0xFFFB923C) // Orange
}

/**
 * High-fidelity Markdown text renderer supporting:
 * - Code blocks with multi-language syntax highlighting and line numbers
 * - Tables with formatted headers, borders, and horizontal scrolling
 * - Blockquotes with accent borders
 * - Task list checkboxes
 * - Bullet and numbered lists
 * - Headings (H1–H4)
 * - Inline formatting: bold, italic, strikethrough, inline code, links
 */
@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val blocks = remember(text) { parseMarkdownBlocks(text) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.CodeBlock -> {
                    CodeBlockView(language = block.language, code = block.code)
                }
                is MarkdownBlock.Heading -> {
                    val style = when (block.level) {
                        1 -> MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = NeonCyan,
                            fontSize = 21.sp,
                            lineHeight = 26.sp
                        )
                        2 -> MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ElectricPurple,
                            fontSize = 18.sp,
                            lineHeight = 23.sp
                        )
                        3 -> MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFA5B4FC),
                            fontSize = 16.sp
                        )
                        else -> MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = textColor,
                            fontSize = 14.5.sp
                        )
                    }
                    Text(
                        text = buildFormattedText(block.text, textColor),
                        style = style,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }
                is MarkdownBlock.BulletItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "✦ ",
                            color = NeonCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = buildFormattedText(block.text, textColor),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = textColor,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is MarkdownBlock.NumberedItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${block.number}. ",
                            color = ElectricPurple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(24.dp)
                        )
                        Text(
                            text = buildFormattedText(block.text, textColor),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = textColor,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is MarkdownBlock.TaskItem -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (block.checked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                            contentDescription = if (block.checked) "Completed" else "Incomplete",
                            tint = if (block.checked) NeonCyan else TextSecondaryDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = buildFormattedText(block.text, textColor),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (block.checked) TextSecondaryDark else textColor,
                                textDecoration = if (block.checked) TextDecoration.LineThrough else TextDecoration.None,
                                lineHeight = 20.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is MarkdownBlock.Blockquote -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF130F24))
                            .border(
                                width = 1.dp,
                                color = Color(0xFF281E48),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .height(IntrinsicSize.Min)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .fillMaxHeight()
                                .background(NeonCyan)
                        )
                        Text(
                            text = buildFormattedText(block.text, textColor.copy(alpha = 0.9f)),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontStyle = FontStyle.Italic,
                                lineHeight = 21.sp
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                is MarkdownBlock.Table -> {
                    MarkdownTableView(headers = block.headers, rows = block.rows)
                }
                is MarkdownBlock.HorizontalRule -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0xFF818CF8),
                                        ElectricPurple.copy(alpha = 0.5f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
                is MarkdownBlock.Paragraph -> {
                    Text(
                        text = buildFormattedText(block.text, textColor),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textColor,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Polished Code Block component featuring:
 * - Language tag badge and code terminal icon
 * - Copy Code action button with success confirmation
 * - Line numbers gutter
 * - Accurate multi-language syntax highlighting
 * - Horizontal scrolling for wide expressions
 */
@Composable
fun CodeBlockView(
    language: String,
    code: String
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val codeLines = remember(code) { code.lines() }
    val highlightedCode = remember(code, language) {
        highlightSyntax(code, language)
    }

    val displayLang = remember(language) {
        when (language.trim().lowercase()) {
            "kt", "kotlin" -> "Kotlin"
            "py", "python" -> "Python"
            "js", "javascript" -> "JavaScript"
            "ts", "typescript" -> "TypeScript"
            "java" -> "Java"
            "json" -> "JSON"
            "sql" -> "SQL"
            "html" -> "HTML"
            "css" -> "CSS"
            "xml" -> "XML"
            "sh", "bash", "shell", "zsh" -> "Shell"
            "cpp", "c++" -> "C++"
            "c" -> "C"
            "rs", "rust" -> "Rust"
            "go", "golang" -> "Go"
            "swift" -> "Swift"
            "dart" -> "Dart"
            else -> if (language.isNotBlank()) language.uppercase() else "CODE"
        }
    }

    val langBadgeColor = remember(displayLang) {
        when (displayLang) {
            "Kotlin" -> Color(0xFFC084FC)
            "Python" -> Color(0xFF38BDF8)
            "JavaScript", "TypeScript" -> Color(0xFFFBBF24)
            "Java" -> Color(0xFFF97316)
            "JSON" -> Color(0xFF34D399)
            "SQL" -> NeonCyan
            "Rust" -> Color(0xFFFB923C)
            "Shell" -> Color(0xFF10B981)
            else -> NeonCyan
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SyntaxColors.Background)
            .border(1.dp, Color(0xFF281E48), RoundedCornerShape(12.dp))
            .testTag("code_block_container")
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SyntaxColors.HeaderBackground)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF1E1736),
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(langBadgeColor)
                    )

                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = langBadgeColor.copy(alpha = 0.8f),
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = displayLang,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFFF1F5F9),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Code", code)
                            clipboard.setPrimaryClip(clip)
                            copied = true
                            Toast.makeText(context, "Code copied to clipboard", Toast.LENGTH_SHORT).show()
                            scope.launch {
                                delay(2000)
                                copied = false
                            }
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("btn_copy_code_block")
                    ) {
                        Icon(
                            imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy code",
                            tint = if (copied) Color(0xFF00F0A8) else Color(0xFFA5A6C2),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Code and Line Numbers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                // Line Numbers Column
                if (codeLines.size > 1) {
                    Column(
                        modifier = Modifier
                            .background(SyntaxColors.GutterBackground)
                            .border(
                                width = 1.dp,
                                color = SyntaxColors.GutterDivider,
                                shape = RoundedCornerShape(bottomStart = 12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        codeLines.indices.forEach { index ->
                            Text(
                                text = "${index + 1}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 19.sp,
                                color = SyntaxColors.GutterText,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }

                // Highlighted Code Content
                Box(
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = highlightedCode,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.5.sp,
                        lineHeight = 19.sp,
                        color = SyntaxColors.DefaultCode
                    )
                }
            }
        }
    }
}

/**
 * Renders Markdown tables with headers, cell borders, and horizontal scrolling.
 */
@Composable
private fun MarkdownTableView(
    headers: List<String>,
    rows: List<List<String>>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF2E2458), RoundedCornerShape(8.dp))
            .horizontalScroll(rememberScrollState())
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier
                    .background(CyberDarkSurfaceElevated)
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                headers.forEach { header ->
                    Text(
                        text = header,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        ),
                        modifier = Modifier.width(IntrinsicSize.Min)
                    )
                }
            }

            HorizontalDivider(color = Color(0xFF2E2458), thickness = 1.dp)

            // Rows
            rows.forEachIndexed { rowIndex, rowCells ->
                val bg = if (rowIndex % 2 == 0) Color(0xFF0C0A17) else Color(0xFF130F24)
                Row(
                    modifier = Modifier
                        .background(bg)
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    rowCells.forEach { cell ->
                        Text(
                            text = cell,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPrimaryDark,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.width(IntrinsicSize.Min)
                        )
                    }
                }
                if (rowIndex < rows.size - 1) {
                    HorizontalDivider(color = Color(0xFF1E1736), thickness = 0.5.dp)
                }
            }
        }
    }
}

/**
 * Syntax highlighter that tokenizes code and returns styled AnnotatedString.
 */
private fun highlightSyntax(code: String, language: String): AnnotatedString {
    val lang = language.trim().lowercase()

    // Regex Tokenizer:
    // Group 1: Comments (//, /* */, #, --)
    // Group 2: Strings ("...", '...', `...`)
    // Group 3: Annotations (@Annotation)
    // Group 4: Numbers (decimal, float, hex)
    // Group 5: Keywords
    // Group 6: Common Types
    // Group 7: Function Calls
    // Group 8: Booleans / Constants
    val tokenRegex = Regex(
        "(//[^\n]*|/\\*[\\s\\S]*?\\*/|#[^\n]*|--[^\n]*)" +
        "|(\"\"\"[\\s\\S]*?\"\"\"|\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^'\\\\])*'|`(\\\\.|[^`\\\\])*`)" +
        "|(@[a-zA-Z_][a-zA-Z0-9_]*)" +
        "|(\\b\\d+(\\.\\d+)?([fFLl])?\\b|0x[0-9a-fA-F]+)" +
        "|(\\b(?:fun|val|var|class|interface|object|enum|return|if|else|when|switch|case|default|for|while|do|try|catch|finally|throw|new|this|super|public|private|protected|internal|override|open|abstract|sealed|data|suspend|inline|function|def|const|let|async|await|import|from|export|as|in|is|package|struct|fn|pub|impl|type|trait|select|from|where|insert|into|update|delete|create|table|drop|alter|group|by|order|limit|join|inner|left|right|on|having)\\b)" +
        "|(\\b(?:String|Int|Long|Float|Double|Boolean|Char|Byte|Short|Unit|Any|List|Map|Set|Flow|StateFlow|Modifier|Color|Composable|View|Activity|Fragment|Context|void|int|float|double|bool|char|number|any|unknown|never|CoroutineScope|ViewModel)\\b)" +
        "|(\\b[a-zA-Z_][a-zA-Z0-9_]*(?=\\s*\\())" +
        "|(\\b(?:true|false|null|nil|None|True|False)\\b)",
        if (lang == "sql") setOf(RegexOption.IGNORE_CASE) else emptySet()
    )

    return buildAnnotatedString {
        var lastIndex = 0

        for (match in tokenRegex.findAll(code)) {
            val start = match.range.first
            val end = match.range.last + 1

            if (start > lastIndex) {
                append(code.substring(lastIndex, start))
            }

            when {
                // Comments
                match.groups[1] != null -> {
                    withStyle(SpanStyle(color = SyntaxColors.Comment, fontStyle = FontStyle.Italic)) {
                        append(match.value)
                    }
                }
                // Strings
                match.groups[2] != null -> {
                    withStyle(SpanStyle(color = SyntaxColors.StringLiteral)) {
                        append(match.value)
                    }
                }
                // Annotations
                match.groups[3] != null -> {
                    withStyle(SpanStyle(color = SyntaxColors.Annotation, fontWeight = FontWeight.SemiBold)) {
                        append(match.value)
                    }
                }
                // Numbers
                match.groups[4] != null -> {
                    withStyle(SpanStyle(color = SyntaxColors.NumberLiteral)) {
                        append(match.value)
                    }
                }
                // Keywords
                match.groups[5] != null -> {
                    withStyle(SpanStyle(color = SyntaxColors.Keyword, fontWeight = FontWeight.Bold)) {
                        append(match.value)
                    }
                }
                // Types
                match.groups[6] != null -> {
                    withStyle(SpanStyle(color = SyntaxColors.TypeOrClass, fontWeight = FontWeight.Medium)) {
                        append(match.value)
                    }
                }
                // Functions
                match.groups[7] != null -> {
                    withStyle(SpanStyle(color = SyntaxColors.Function)) {
                        append(match.value)
                    }
                }
                // Booleans / Constants
                match.groups[8] != null -> {
                    withStyle(SpanStyle(color = SyntaxColors.BooleanLiteral, fontWeight = FontWeight.SemiBold)) {
                        append(match.value)
                    }
                }
                else -> {
                    append(match.value)
                }
            }

            lastIndex = end
        }

        if (lastIndex < code.length) {
            append(code.substring(lastIndex))
        }
    }
}

/**
 * Internal AST blocks for parsed Markdown content.
 */
private sealed class MarkdownBlock {
    data class Paragraph(val text: String) : MarkdownBlock()
    data class Heading(val level: Int, val text: String) : MarkdownBlock()
    data class BulletItem(val text: String) : MarkdownBlock()
    data class NumberedItem(val number: Int, val text: String) : MarkdownBlock()
    data class TaskItem(val checked: Boolean, val text: String) : MarkdownBlock()
    data class Blockquote(val text: String) : MarkdownBlock()
    data class Table(val headers: List<String>, val rows: List<List<String>>) : MarkdownBlock()
    data object HorizontalRule : MarkdownBlock()
    data class CodeBlock(val language: String, val code: String) : MarkdownBlock()
}

/**
 * Parses raw text into a sequence of Markdown AST blocks.
 */
private fun parseMarkdownBlocks(input: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = input.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]
        val trimmed = line.trim()

        // 1. Code block fenced with ```
        if (trimmed.startsWith("```")) {
            val language = trimmed.removePrefix("```").trim()
            val codeLines = mutableListOf<String>()
            i++
            while (i < lines.size && !lines[i].trim().startsWith("```")) {
                codeLines.add(lines[i])
                i++
            }
            blocks.add(MarkdownBlock.CodeBlock(language, codeLines.joinToString("\n")))
            i++
            continue
        }

        // 2. Table parsing (| col 1 | col 2 |)
        if (trimmed.startsWith("|") && trimmed.endsWith("|") && i + 1 < lines.size && lines[i + 1].trim().startsWith("|")) {
            val tableLines = mutableListOf<String>()
            while (i < lines.size && lines[i].trim().startsWith("|") && lines[i].trim().endsWith("|")) {
                tableLines.add(lines[i].trim())
                i++
            }
            if (tableLines.size >= 2) {
                val headers = tableLines[0].split("|").map { it.trim() }.filter { it.isNotEmpty() }
                val rows = tableLines.drop(2).map { rowLine ->
                    rowLine.split("|").map { it.trim() }.filter { it.isNotEmpty() }
                }
                blocks.add(MarkdownBlock.Table(headers, rows))
                continue
            }
        }

        when {
            trimmed.isEmpty() -> {
                i++
            }
            // Horizontal rule
            trimmed == "---" || trimmed == "***" || trimmed == "___" -> {
                blocks.add(MarkdownBlock.HorizontalRule)
                i++
            }
            // Headings
            trimmed.startsWith("#### ") -> {
                blocks.add(MarkdownBlock.Heading(4, trimmed.removePrefix("#### ")))
                i++
            }
            trimmed.startsWith("### ") -> {
                blocks.add(MarkdownBlock.Heading(3, trimmed.removePrefix("### ")))
                i++
            }
            trimmed.startsWith("## ") -> {
                blocks.add(MarkdownBlock.Heading(2, trimmed.removePrefix("## ")))
                i++
            }
            trimmed.startsWith("# ") -> {
                blocks.add(MarkdownBlock.Heading(1, trimmed.removePrefix("# ")))
                i++
            }
            // Blockquote
            trimmed.startsWith("> ") -> {
                val quoteLines = mutableListOf<String>()
                while (i < lines.size && lines[i].trim().startsWith(">")) {
                    quoteLines.add(lines[i].trim().removePrefix(">").trim())
                    i++
                }
                blocks.add(MarkdownBlock.Blockquote(quoteLines.joinToString(" ")))
            }
            // Task lists: - [x] or - [ ]
            trimmed.startsWith("- [x] ") || trimmed.startsWith("* [x] ") -> {
                blocks.add(MarkdownBlock.TaskItem(checked = true, trimmed.substring(6).trim()))
                i++
            }
            trimmed.startsWith("- [ ] ") || trimmed.startsWith("* [ ] ") -> {
                blocks.add(MarkdownBlock.TaskItem(checked = false, trimmed.substring(6).trim()))
                i++
            }
            // Bullets
            trimmed.startsWith("* ") || trimmed.startsWith("- ") || trimmed.startsWith("• ") -> {
                val clean = trimmed.replace(Regex("^([*\\-•])\\s+"), "")
                blocks.add(MarkdownBlock.BulletItem(clean))
                i++
            }
            // Numbered items
            Regex("^(\\d+)[.)]\\s+(.+)").matches(trimmed) -> {
                val match = Regex("^(\\d+)[.)]\\s+(.+)").find(trimmed)
                val num = match?.groupValues?.get(1)?.toIntOrNull() ?: 1
                val content = match?.groupValues?.get(2) ?: trimmed
                blocks.add(MarkdownBlock.NumberedItem(num, content))
                i++
            }
            else -> {
                blocks.add(MarkdownBlock.Paragraph(trimmed))
                i++
            }
        }
    }

    return blocks
}

/**
 * Builds rich styled AnnotatedString for inline markdown elements:
 * - Bold (**text** or __text__)
 * - Italic (*text* or _text_)
 * - Inline code (`code`)
 * - Strikethrough (~~text~~)
 * - Links ([label](url))
 */
private fun buildFormattedText(raw: String, defaultColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        // Regex matches bold (** or __), strikethrough (~~), italic (* or _), inline code (`), and markdown links ([text](url))
        val regex = Regex(
            "(\\*\\*|__|~~|`)(.*?)\\1" +
            "|(\\*|_)(.*?)\\3" +
            "|\\[([^\\]]+)\\]\\(([^\\)]+)\\)"
        )

        val matches = regex.findAll(raw)

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1

            if (start > cursor) {
                append(raw.substring(cursor, start))
            }

            when {
                // Delimiter matches: **, __, ~~, `
                match.groups[1] != null -> {
                    val delimiter = match.groups[1]!!.value
                    val innerText = match.groups[2]!!.value

                    when (delimiter) {
                        "**", "__" -> {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = defaultColor)) {
                                append(innerText)
                            }
                        }
                        "~~" -> {
                            withStyle(
                                SpanStyle(
                                    textDecoration = TextDecoration.LineThrough,
                                    color = defaultColor.copy(alpha = 0.6f)
                                )
                            ) {
                                append(innerText)
                            }
                        }
                        "`" -> {
                            withStyle(
                                SpanStyle(
                                    fontFamily = FontFamily.Monospace,
                                    background = Color(0xFF1E1738),
                                    color = NeonCyan,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append(" $innerText ")
                            }
                        }
                    }
                }
                // Single delimiter: *, _ (Italic)
                match.groups[3] != null -> {
                    val innerText = match.groups[4]!!.value
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = defaultColor)) {
                        append(innerText)
                    }
                }
                // Link: [label](url)
                match.groups[5] != null -> {
                    val label = match.groups[5]!!.value
                    withStyle(
                        SpanStyle(
                            color = NeonCyan,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(label)
                    }
                }
            }
            cursor = end
        }

        if (cursor < raw.length) {
            append(raw.substring(cursor))
        }
    }
}
