package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.WarmIvory

@Composable
fun PillarBadge(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuickAccessFacilitiesCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HeritageGreen),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.75f)),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag("quick_access_facilities_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = AntiqueGold.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, AntiqueGold)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SportsTennis,
                            contentDescription = "Facilities",
                            tint = AntiqueGold,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = AntiqueGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "FACILITIES",
                    style = MaterialTheme.typography.labelSmall,
                    color = AntiqueGold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Sports & Grounds",
                    style = MaterialTheme.typography.titleLarge,
                    color = WarmIvory
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pool, AstroTurf pitch, badminton & table tennis.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmIvory.copy(alpha = 0.84f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Request a Slot →",
                style = MaterialTheme.typography.labelLarge,
                color = AntiqueGold,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuickAccessMembershipCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        border = BorderStroke(1.dp, AntiqueGold),
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag("quick_access_membership_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = HeritageGreen
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Badge,
                            contentDescription = "Membership",
                            tint = AntiqueGold,
                            modifier = Modifier
                                .padding(8.dp)
                                .size(20.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = HeritageGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "MEMBERSHIP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MidnightGreen.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Join the Club",
                    style = MaterialTheme.typography.titleLarge,
                    color = HeritageGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Individual, Family, Junior, Corporate & Social.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MidnightGreen.copy(alpha = 0.85f)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Apply Online →",
                style = MaterialTheme.typography.labelLarge,
                color = HeritageGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
