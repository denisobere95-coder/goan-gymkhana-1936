package com.example.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ClubCatalog
import com.example.data.ClubFacility
import com.example.data.FacilityVerification
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.WarmIvory

private enum class FacilityFilter(val label: String) {
    ALL("All"),
    CONFIRMED("Verified"),
    DIRECTORY("Directory Listed"),
    HOSPITALITY("Hospitality")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FacilitiesScreen(
    onOpenBookingScreen: (ClubFacility?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(FacilityFilter.ALL) }

    val filteredFacilities = remember(selectedFilter) {
        when (selectedFilter) {
            FacilityFilter.ALL -> ClubCatalog.facilities
            FacilityFilter.CONFIRMED -> ClubCatalog.facilities.filter {
                it.verification == FacilityVerification.CONFIRMED
            }
            FacilityFilter.DIRECTORY -> ClubCatalog.facilities.filter {
                it.verification == FacilityVerification.DIRECTORY
            }
            FacilityFilter.HOSPITALITY -> ClubCatalog.facilities.filter {
                it.verification == FacilityVerification.HOSPITALITY
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val columns = when {
            maxWidth < 340.dp -> 1
            maxWidth < 680.dp -> 2
            else -> 3
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .widthIn(max = 900.dp)
                .fillMaxSize()
                .testTag("facilities_grid"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "FACILITIES & COURTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Sports & Recreation",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Explore our swimming pool, AstroTurf football pitch, badminton, table tennis, and clubhouse grounds on Ngara Road.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onOpenBookingScreen(null) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AntiqueGold,
                            contentColor = MidnightGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("open_booking_screen_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Facility Booking Screen",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FacilityFilter.entries.forEach { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { selectedFilter = filter },
                                label = { Text(filter.label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HeritageGreen,
                                    selectedLabelColor = WarmIvory
                                ),
                                modifier = Modifier.testTag("facility_filter_${filter.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            items(filteredFacilities, key = { it.id }) { facility ->
                FacilityGridCard(
                    facility = facility,
                    onEnquireClick = { onOpenBookingScreen(facility) }
                )
            }

            // Unconfirmed Sports Candidate Notice
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "Additional Facilities Under Verification",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "The following disciplines are candidates awaiting official confirmation from club management and are not advertised as active until verified:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ClubCatalog.unconfirmedFacilityCandidates.forEach { candidate ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = candidate,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FacilityGridCard(
    facility: ClubFacility,
    onEnquireClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onEnquireClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HeritageGreen),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("facility_card_${facility.id}")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            FacilityCardImage(
                facility = facility,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = facility.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = WarmIvory,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = facility.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmIvory.copy(alpha = 0.84f),
                    maxLines = 3,
                    minLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onEnquireClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiqueGold,
                        contentColor = MidnightGreen
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("book_${facility.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.EventAvailable,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Enquire",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@DrawableRes
fun resolveFacilityImageRes(facility: ClubFacility): Int {
    return when (facility.id) {
        "swimming_pool" -> R.drawable.img_swimming_pool
        "astroturf_football" -> R.drawable.img_astroturf_pitch
        "badminton", "table_tennis", "padel" -> R.drawable.img_racquet_sports
        "clubhouse_hall" -> R.drawable.img_clubhouse_hero
        else -> facility.imageRes.takeIf { it != 0 } ?: R.drawable.img_clubhouse_hero
    }
}

@Composable
fun rememberFacilityPlaceholderPainter(facility: ClubFacility): Painter {
    return painterResource(id = resolveFacilityImageRes(facility))
}

private fun resolveFacilityAccentIcon(facilityId: String): ImageVector {
    return when (facilityId) {
        "swimming_pool" -> Icons.Filled.Pool
        "astroturf_football" -> Icons.Filled.SportsSoccer
        "clubhouse_hall" -> Icons.Filled.AccountBalance
        else -> Icons.Filled.SportsTennis
    }
}

@Composable
fun FacilityCardImage(
    facility: ClubFacility,
    modifier: Modifier = Modifier
) {
    val placeholderPainter = rememberFacilityPlaceholderPainter(facility)
    val accentIcon = resolveFacilityAccentIcon(facility.id)

    Box(modifier = modifier) {
        Image(
            painter = placeholderPainter,
            contentDescription = facility.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MidnightGreen.copy(alpha = 0.28f),
                            Color.Transparent,
                            HeritageGreen.copy(alpha = 0.72f)
                        )
                    )
                )
        )
        Surface(
            shape = RoundedCornerShape(50),
            color = MidnightGreen.copy(alpha = 0.88f),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.8f)),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = null,
                    tint = AntiqueGold,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = facility.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = AntiqueGold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Surface(
            shape = CircleShape,
            color = MidnightGreen.copy(alpha = 0.84f),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.75f)),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = accentIcon,
                contentDescription = null,
                tint = AntiqueGold,
                modifier = Modifier
                    .padding(6.dp)
                    .size(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FacilitiesScreenPreview() {
    GoanGymkhanaTheme {
        FacilitiesScreen(onOpenBookingScreen = {})
    }
}
