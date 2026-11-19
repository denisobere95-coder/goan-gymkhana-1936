package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ClubCatalog
import com.example.data.ClubFacility
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.WarmIvory

@OptIn(ExperimentalLayoutApi::class)

@Composable
fun ClubHomeScreen(
    onNavigateToAboutUs: () -> Unit,
    onNavigateToFacilities: (ClubFacility?) -> Unit,
    onNavigateToMembership: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToMap: () -> Unit = {},
    onNavigateToContact: () -> Unit = {},
    onNotifyFeedback: (String, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showChecklist by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_lazy_column"),
        contentPadding = PaddingValues(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                // Editorial Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 360.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_clubhouse_hero),
                        contentDescription = stringResource(R.string.cd_clubhouse_hero),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MidnightGreen.copy(alpha = 0.35f),
                                        MidnightGreen.copy(alpha = 0.55f),
                                        MidnightGreen.copy(alpha = 0.94f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .statusBarsPadding()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.club_crest_icon),
                                contentDescription = stringResource(R.string.cd_club_crest),
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                color = AntiqueGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, AntiqueGold)
                            ) {
                                Text(
                                    text = stringResource(R.string.club_est),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AntiqueGold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.club_tagline).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = AntiqueGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ClubCatalog.CLUB_NAME,
                            style = MaterialTheme.typography.displaySmall,
                            color = WarmIvory
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.club_hero_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = AntiqueGold.copy(alpha = 0.95f)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.club_hero_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmIvory.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onNavigateToFacilities(null) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AntiqueGold,
                                    contentColor = MidnightGreen
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("home_explore_facilities_btn")
                            ) {
                                Text(
                                    text = "Explore Facilities",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            OutlinedButton(
                                onClick = onNavigateToMembership,
                                border = BorderStroke(1.dp, WarmIvory.copy(alpha = 0.7f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmIvory),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("home_become_member_btn")
                            ) {
                                Text(
                                    text = "Become a Member",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }

        // Club Introduction ("Heritage. Sport. Community.")
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 20.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_intro_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(2.5.dp)
                                    .background(AntiqueGold, RoundedCornerShape(2.dp))
                            )
                            Text(
                                text = "WELCOME TO NGARA ROAD",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Where Heritage Meets Modern Sporting Life",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Founded in 1935–1936 opposite the National Museums of Kenya, The Goan Gymkhana pairs its 1943 Victorian stone clubhouse with swimming, AstroTurf football, badminton, table tennis, and warm social hospitality open to all communities.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PillarBadge(
                                icon = Icons.Filled.HistoryEdu,
                                title = "Heritage",
                                subtitle = "Since 1935",
                                modifier = Modifier.weight(1f)
                            )
                            PillarBadge(
                                icon = Icons.Filled.SportsTennis,
                                title = "Sport",
                                subtitle = "Pool & Courts",
                                modifier = Modifier.weight(1f)
                            )
                            PillarBadge(
                                icon = Icons.Filled.Groups,
                                title = "Community",
                                subtitle = "Open to All",
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = onNavigateToAboutUs,
                            border = BorderStroke(1.dp, AntiqueGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("home_about_us_btn")
                        ) {
                            Text(
                                text = "Read Our Full Story & Heritage (Est. 1935–1936)",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

        // Quick-Access Cards: Facilities & Membership
        item {
            BoxWithConstraints(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp)
            ) {
                val sideBySide = maxWidth >= 340.dp
                if (sideBySide) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickAccessFacilitiesCard(
                            onClick = { onNavigateToFacilities(null) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                        QuickAccessMembershipCard(
                            onClick = onNavigateToMembership,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickAccessFacilitiesCard(
                            onClick = { onNavigateToFacilities(null) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        QuickAccessMembershipCard(
                            onClick = onNavigateToMembership,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Quick Facility Strip
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Sporting Grounds & Club",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Tap a facility to view details or request a slot",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clickable { onNavigateToFacilities(null) }
                            .testTag("home_view_all_facilities")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "View all facilities",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ClubCatalog.facilities.take(4), key = { it.id }) { facility ->
                        Card(
                            onClick = { onNavigateToFacilities(facility) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .width(205.dp)
                                .testTag("home_facility_card_${facility.id}")
                        ) {
                            Column {
                                Image(
                                    painter = rememberFacilityPlaceholderPainter(facility),
                                    contentDescription = facility.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(112.dp)
                                )
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = facility.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = facility.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = facility.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Heritage & Distinct Identity Section
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        text = "Our Heritage (1935–1936 to Present)",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                ClubCatalog.heritageTimeline.forEach { milestone ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = milestone.era,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
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
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = milestone.sourceNote,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                // Distinction Notice (Goan Gymkhana vs Nairobi Gymkhana)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = HeritageGreen,
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.55f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Club identity note",
                            tint = AntiqueGold
                        )
                        Column {
                            Text(
                                text = "Historic Ngara Road Institution",
                                style = MaterialTheme.typography.titleSmall,
                                color = AntiqueGold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "The Goan Gymkhana on Ngara Road (opposite the National Museums of Kenya) is an independent club with its own heritage and management—distinct from Nairobi Gymkhana.",
                                style = MaterialTheme.typography.bodySmall,
                                color = WarmIvory.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Location, Contact & Directions
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "Visit & Contact the Club",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InfoLine(
                            icon = Icons.Filled.LocationOn,
                            title = "Ngara Road, Nairobi",
                            detail = "${ClubCatalog.ADDRESS}\nCoordinates: ${ClubCatalog.COORDINATES}"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        InfoLine(
                            icon = Icons.Filled.Schedule,
                            title = "Listed Operating Hours",
                            detail = "${ClubCatalog.LISTED_HOURS_WEEKDAY}\n${ClubCatalog.LISTED_HOURS_SUNDAY}"
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        InfoLine(
                            icon = Icons.Filled.Star,
                            title = "Public Community Rating",
                            detail = ClubCatalog.RATING_SUMMARY
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        InfoLine(
                            icon = Icons.Filled.Email,
                            title = "Club Contact Desk",
                            detail = "Tel: ${ClubCatalog.PHONE}\nEmail: ${ClubCatalog.EMAIL} (listed prior address; confirm active inbox)"
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        ClubLocationPreviewMapCard(onOpenFullMap = onNavigateToMap)

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!dialClub(context)) {
                                        copyToClipboard(context, "Goan Gymkhana Phone", ClubCatalog.PHONE)
                                        onNotifyFeedback("Copied ${ClubCatalog.PHONE} to clipboard.", false)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("home_call_club_btn")
                            ) {
                                Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Call", style = MaterialTheme.typography.labelLarge)
                            }
                            OutlinedButton(
                                onClick = {
                                    if (!openClubDirections(context)) {
                                        onNotifyFeedback("Unable to open maps app on this device.", true)
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("home_directions_btn")
                            ) {
                                Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Directions", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        OutlinedButton(
                            onClick = onNavigateToContact,
                            border = BorderStroke(1.dp, AntiqueGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("home_open_contact_screen_btn")
                        ) {
                            Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Contact & Maps Screen", style = MaterialTheme.typography.labelLarge)
                        }
                        OutlinedButton(
                            onClick = onNavigateToEvents,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("home_enquiry_btn")
                        ) {
                            Text("Send Dining or Event Enquiry", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        // Management Verification Checklist
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Card(
                    onClick = { showChecklist = !showChecklist },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_verification_toggle")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VerifiedUser,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        text = "Club Fact-Verification Register",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "0/11 items awaiting official management sign-off",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (showChecklist) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = if (showChecklist) "Collapse checklist" else "Expand checklist"
                            )
                        }

                        AnimatedVisibility(visible = showChecklist) {
                            Column(
                                modifier = Modifier.padding(top = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ClubCatalog.verificationChecklist.forEach { item ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircleOutline,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Column {
                                            Text(
                                                text = item.title,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = item.detail,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    }
}

@Composable
private fun InfoLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    detail: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 2.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ClubHomeScreenPreview() {
    GoanGymkhanaTheme {
        ClubHomeScreen(
            onNavigateToAboutUs = {},
            onNavigateToFacilities = {},
            onNavigateToMembership = {},
            onNavigateToEvents = {}
        )
    }
}
