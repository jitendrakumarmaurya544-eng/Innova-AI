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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val blocks = remember(text) { parseMarkdownBlocks(text) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        blocks.forEach { block ->
            when (block) {
                is MarkdownBlock.CodeBlock -> {
                    CodeBlockView(language = block.language, code = block.code)
                }
                is MarkdownBlock.Heading -> {
                    val style = when (block.level) {
                        1 -> MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan,
                            fontSize = 20.sp
                        )
                        2 -> MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ElectricPurple,
                            fontSize = 17.sp
                        )
                        else -> MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = textColor,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = buildFormattedText(block.text, textColor),
                        style = style,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }
                is MarkdownBlock.BulletItem -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "✦ ",
                            color = NeonCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = buildFormattedText(block.text, textColor),
                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                is MarkdownBlock.NumberedItem -> {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${block.number}. ",
                            color = ElectricPurple,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = buildFormattedText(block.text, textColor),
                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                            modifier = Modifier.weight(1f)
                        )
                    }
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

@Composable
fun CodeBlockView(
    language: String,
    code: String
) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF070510))
            .border(1.dp, Color(0xFF2A2050), RoundedCornerShape(12.dp))
    ) {
        Column {
            // Code header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF130F24))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(ElectricPurple)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language.isNotBlank()) language.uppercase() else "CODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

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
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                        contentDescription = "Copy code",
                        tint = if (copied) Color(0xFF00F0A8) else Color(0xFFA5A6C2),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Code content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(14.dp)
            ) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = Color(0xFFE2E8F0)
                )
            }
        }
    }
}

private sealed class MarkdownBlock {
    data class Paragraph(val text: String) : MarkdownBlock()
    data class Heading(val level: Int, val text: String) : MarkdownBlock()
    data class BulletItem(val text: String) : MarkdownBlock()
    data class NumberedItem(val number: Int, val text: String) : MarkdownBlock()
    data class CodeBlock(val language: String, val code: String) : MarkdownBlock()
}

private fun parseMarkdownBlocks(input: String): List<MarkdownBlock> {
    val blocks = mutableListOf<MarkdownBlock>()
    val lines = input.lines()
    var i = 0

    while (i < lines.size) {
        val line = lines[i]

        // Code block check
        if (line.trim().startsWith("```")) {
            val language = line.trim().removePrefix("```").trim()
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

        val trimmed = line.trim()
        when {
            trimmed.isEmpty() -> {
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
            trimmed.startsWith("* ") || trimmed.startsWith("- ") || trimmed.startsWith("• ") -> {
                val clean = trimmed.replace(Regex("^([*\\-•])\\s+"), "")
                blocks.add(MarkdownBlock.BulletItem(clean))
                i++
            }
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

private fun buildFormattedText(raw: String, defaultColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        val regex = Regex("(\\*\\*|\\*|`)(.*?)\\1")
        val matches = regex.findAll(raw)

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1

            if (start > cursor) {
                append(raw.substring(cursor, start))
            }

            val delimiter = match.groupValues[1]
            val innerText = match.groupValues[2]

            when (delimiter) {
                "**" -> {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = defaultColor)) {
                        append(innerText)
                    }
                }
                "*" -> {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = defaultColor)) {
                        append(innerText)
                    }
                }
                "`" -> {
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            background = Color(0xFF1E1738),
                            color = NeonCyan,
                            fontSize = 13.sp
                        )
                    ) {
                        append(" $innerText ")
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
