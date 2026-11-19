package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ClubCatalog
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.WarmIvory

@Composable
fun AboutUsScreen(
    onBack: () -> Unit,
    onNavigateToFacilities: () -> Unit,
    onNavigateToMembership: () -> Unit,
    onNavigateToMap: () -> Unit = {},
    onNavigateToContact: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Heritage Green Top Bar + Archival Hero
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                Surface(
                    color = HeritageGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("about_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Club Home",
                                tint = AntiqueGold
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ABOUT US & HERITAGE",
                                style = MaterialTheme.typography.labelSmall,
                                color = AntiqueGold
                            )
                            Text(
                                text = ClubCatalog.CLUB_NAME,
                                style = MaterialTheme.typography.titleLarge,
                                color = WarmIvory
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = AntiqueGold.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, AntiqueGold),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "EST. 1935–1936",
                                style = MaterialTheme.typography.labelSmall,
                                color = AntiqueGold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_clubhouse_hero),
                        contentDescription = stringResource(R.string.cd_clubhouse_hero),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        HeritageGreen.copy(alpha = 0.25f),
                                        MidnightGreen.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.club_crest_icon),
                            contentDescription = stringResource(R.string.cd_club_crest),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(58.dp)
                                .clip(CircleShape)
                        )
                        Column {
                            Text(
                                text = "Nine Decades on Ngara Road",
                                style = MaterialTheme.typography.headlineSmall,
                                color = WarmIvory
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Historic members' sports, recreation & social club",
                                style = MaterialTheme.typography.bodySmall,
                                color = AntiqueGold
                            )
                        }
                    }
                }
            }
        }

        // Founding & Origins Summary Card
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = HeritageGreen,
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.7f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountBalance,
                                contentDescription = null,
                                tint = AntiqueGold
                            )
                            Text(
                                text = "OUR ORIGINS (1935–1936)",
                                style = MaterialTheme.typography.labelSmall,
                                color = AntiqueGold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Built by Nairobi's Goan Pioneers",
                            style = MaterialTheme.typography.headlineMedium,
                            color = WarmIvory
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Established in 1935–1936 for members of the Goan community working in Kenya's civil service and railway sector, The Goan Gymkhana grew as a proud sporting and social refuge during an era when community members were excluded from segregated clubs.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WarmIvory.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = AntiqueGold.copy(alpha = 0.35f))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Historical accounts note both 1935 (Business Daily, 2016) and May 1936 (when founders separated from the Goan Institute, before opening the stone clubhouse in 1943). Both dates are preserved respectfully pending official committee confirmation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AntiqueGold.copy(alpha = 0.95f)
                        )
                    }
                }
            }
        }

        // Chronological Heritage Timeline
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp, bottom = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.HistoryEdu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Historical Milestones",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        itemsIndexed(ClubCatalog.heritageTimeline) { index, milestone ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.45f)),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .testTag("about_milestone_$index")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = HeritageGreen,
                            border = BorderStroke(1.dp, AntiqueGold)
                        ) {
                            Text(
                                text = milestone.era,
                                style = MaterialTheme.typography.labelSmall,
                                color = AntiqueGold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                            text = "Chapter 0${index + 1}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = milestone.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = milestone.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Source: ${milestone.sourceNote}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        // Architectural & Identity Highlights + Distinction Notice
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = AntiqueGold)
                            Text(
                                text = "The 1943 Clubhouse & Grounds",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "• Victorian architectural touches on Ngara Road, opposite the National Museums of Kenya near Museum Hill.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Upper-floor social area and signature viewing balcony overlooking the club grounds.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• Swimming pool nestled behind the main clubhouse alongside indoor games and football grounds.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToMap,
                                border = BorderStroke(1.dp, AntiqueGold),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("about_view_map_btn")
                            ) {
                                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Club Map", style = MaterialTheme.typography.labelLarge)
                            }
                            OutlinedButton(
                                onClick = onNavigateToContact,
                                border = BorderStroke(1.dp, HeritageGreen),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("about_contact_btn")
                            ) {
                                Text("Contact & Maps", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MidnightGreen,
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = AntiqueGold
                        )
                        Column {
                            Text(
                                text = "Distinct Historic Identity",
                                style = MaterialTheme.typography.titleSmall,
                                color = AntiqueGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "The Goan Gymkhana (Ngara Road) is an independent organisation with its own history, trusteeship, and community heritage—separate from Nairobi Gymkhana.",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmIvory.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                // Next-Step Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateToFacilities,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HeritageGreen,
                            contentColor = WarmIvory
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("about_facilities_btn")
                    ) {
                        Icon(Icons.Filled.SportsTennis, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Facilities", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onNavigateToMembership,
                        border = BorderStroke(1.dp, AntiqueGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("about_membership_btn")
                    ) {
                        Icon(Icons.Filled.Groups, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Membership", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutUsScreenPreview() {
    GoanGymkhanaTheme {
        AboutUsScreen(
            onBack = {},
            onNavigateToFacilities = {},
            onNavigateToMembership = {}
        )
    }
}
