package com.example.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.StatusApproved
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusRejected

@Composable
fun StatusPill(
    label: String,
    wireStatus: String,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (wireStatus) {
        "approved", "responded", "reviewed" -> StatusApproved.copy(alpha = 0.16f) to StatusApproved
        "under_review" -> AntiqueGold.copy(alpha = 0.24f) to HeritageGreen
        "rejected", "declined" -> StatusRejected.copy(alpha = 0.15f) to StatusRejected
        "cancelled", "withdrawn", "closed" ->
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        else -> StatusPending.copy(alpha = 0.18f) to StatusPending
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = fg,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
