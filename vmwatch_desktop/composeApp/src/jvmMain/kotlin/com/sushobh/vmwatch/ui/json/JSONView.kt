package com.sushobh.vmwatch.ui.json

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.google.gson.GsonBuilder

/**
 * Display JSON string in pretty format inside Compose.
 * @param json Raw JSON string
 */
@Composable
fun PrettyJsonView(json: String) {
    val gson = GsonBuilder().setPrettyPrinting().create()
    val prettyJson = try {
        gson.toJson(gson.fromJson(json, Any::class.java))
    } catch (e: Exception) {
        json // fallback if invalid JSON
    }

    val scrollStateV = rememberScrollState()
    val scrollStateH = rememberScrollState()

    Box(
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(scrollStateV)
            .horizontalScroll(scrollStateH)
            .fillMaxWidth()
    ) {
        BasicText(

            text = AnnotatedString(prettyJson),
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = MaterialTheme.colorScheme.onSurface,
            )
        )
    }
}
