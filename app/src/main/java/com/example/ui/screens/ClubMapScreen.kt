package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.data.ClubCatalog
import com.example.data.ClubFacility
import com.example.data.ClubMapPin
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.WarmIvory
import kotlin.math.roundToInt

enum class MapViewMode(val label: String) {
    NAIROBI_AREA("Nairobi Area"),
    CLUB_GROUNDS("Club Grounds")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClubMapScreen(
    onBack: () -> Unit,
    onBookFacility: (ClubFacility) -> Unit,
    onNotifyFeedback: (message: String, isError: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mapMode by remember { mutableStateOf(MapViewMode.NAIROBI_AREA) }
    val activePins = remember(mapMode) {
        if (mapMode == MapViewMode.NAIROBI_AREA) {
            ClubCatalog.areaMapPins
        } else {
            ClubCatalog.campusMapPins
        }
    }
    var selectedPin by remember(mapMode) { mutableStateOf(activePins.first()) }
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("club_map_lazy_column"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header Bar
        item {
            Surface(
                color = HeritageGreen,
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("map_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AntiqueGold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WAYFINDER & GROUNDS MAP",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold
                        )
                        Text(
                            text = "Locate The Goan Gymkhana",
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
                            text = "NGARA RD",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Mode Switcher & Quick GPS Pill
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MapViewMode.entries.forEach { mode ->
                        val selected = mapMode == mode
                        FilterChip(
                            selected = selected,
                            onClick = {
                                mapMode = mode
                                zoomLevel = 1f
                                panOffset = Offset.Zero
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (mode == MapViewMode.NAIROBI_AREA) {
                                        Icons.Filled.Map
                                    } else {
                                        Icons.Filled.Park
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(mode.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HeritageGreen,
                                selectedLabelColor = WarmIvory,
                                selectedLeadingIconColor = AntiqueGold
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("map_mode_${mode.name.lowercase()}")
                        )
                    }
                }
                Text(
                    text = if (mapMode == MapViewMode.NAIROBI_AREA) {
                        "Drag or pinch to pan & zoom around Ngara Road & Museum Hill. Tap any pin for landmark guidance."
                    } else {
                        "Explore the Ngara Road sports compound. Tap any facility pin to jump straight to booking."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Interactive Cartographic Map Surface
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.5.dp, AntiqueGold),
                colors = CardDefaults.cardColors(containerColor = MidnightGreen),
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .testTag("interactive_map_viewport")
                ) {
                    InteractiveClubCartography(
                        mode = mapMode,
                        pins = activePins,
                        selectedPin = selectedPin,
                        zoomLevel = zoomLevel,
                        panOffset = panOffset,
                        onGestureTransform = { panDelta, zoomDelta ->
                            val newZoom = (zoomLevel * zoomDelta).coerceIn(1f, 2.4f)
                            zoomLevel = newZoom
                            val maxOffset = (newZoom - 1f) * 260f
                            panOffset = Offset(
                                x = (panOffset.x + panDelta.x).coerceIn(-maxOffset, maxOffset),
                                y = (panOffset.y + panDelta.y).coerceIn(-maxOffset, maxOffset)
                            )
                        },
                        onPinSelected = { pin -> selectedPin = pin }
                    )

                    // Top-Left Coordinate Readout Badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MidnightGreen.copy(alpha = 0.88f),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.7f)),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.GpsFixed,
                                contentDescription = null,
                                tint = AntiqueGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${ClubCatalog.COORDINATES} · ${(zoomLevel * 100).roundToInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmIvory
                            )
                        }
                    }

                    // Bottom-Right Map Controls (+ / - / Recenter)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MapControlIconButton(
                            icon = Icons.Filled.Add,
                            contentDescription = "Zoom in",
                            testTag = "map_zoom_in_btn",
                            onClick = {
                                zoomLevel = (zoomLevel + 0.35f).coerceAtMost(2.4f)
                            }
                        )
                        MapControlIconButton(
                            icon = Icons.Filled.Remove,
                            contentDescription = "Zoom out",
                            testTag = "map_zoom_out_btn",
                            onClick = {
                                val next = (zoomLevel - 0.35f).coerceAtLeast(1f)
                                zoomLevel = next
                                if (next <= 1.05f) panOffset = Offset.Zero
                            }
                        )
                        MapControlIconButton(
                            icon = Icons.Filled.MyLocation,
                            contentDescription = "Recenter on Goan Gymkhana",
                            testTag = "map_recenter_btn",
                            onClick = {
                                zoomLevel = 1f
                                panOffset = Offset.Zero
                                selectedPin = activePins.first()
                            }
                        )
                    }
                }
            }
        }

        // Quick Select Pin Chips for Accessibility & Fast Switching
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (mapMode == MapViewMode.NAIROBI_AREA) {
                        "Landmarks & Approaches"
                    } else {
                        "Grounds Directory"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    activePins.forEach { pin ->
                        FilterChip(
                            selected = selectedPin.id == pin.id,
                            onClick = { selectedPin = pin },
                            label = { Text(pin.title) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold,
                                selectedLabelColor = MidnightGreen
                            ),
                            modifier = Modifier.testTag("map_pin_chip_${pin.id}")
                        )
                    }
                }
            }
        }

        // Selected Landmark / Facility Spotlight Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = HeritageGreen),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.75f)),
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("map_selected_pin_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = AntiqueGold
                        )
                        Text(
                            text = selectedPin.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedPin.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = WarmIvory
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = selectedPin.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmIvory.copy(alpha = 0.9f)
                    )

                    val linkedFacility = selectedPin.facilityId?.let { fId ->
                        ClubCatalog.facilities.firstOrNull { it.id == fId }
                    }
                    AnimatedVisibility(visible = linkedFacility != null) {
                        if (linkedFacility != null) {
                            Button(
                                onClick = { onBookFacility(linkedFacility) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AntiqueGold,
                                    contentColor = MidnightGreen
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                                    .minimumInteractiveComponentSize()
                                    .testTag("map_book_facility_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.EventAvailable,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Reserve ${linkedFacility.name}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Navigation Actions (Live Maps App / Browser Fallback, Copy GPS, Call)
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val launched = openClubDirections(context)
                        if (launched) {
                            onNotifyFeedback("Opening turn-by-turn route to Ngara Road.", false)
                        } else {
                            onNotifyFeedback("No map or browser app found on device.", true)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiqueGold,
                        contentColor = MidnightGreen
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .minimumInteractiveComponentSize()
                        .testTag("map_launch_directions_btn")
                ) {
                    Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Start Live Directions (Google Maps / Web)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            copyToClipboard(
                                context = context,
                                label = "Goan Gymkhana GPS",
                                text = ClubCatalog.COORDINATES
                            )
                            onNotifyFeedback(
                                "Copied GPS (${ClubCatalog.COORDINATES}) to clipboard.",
                                false
                            )
                        },
                        border = BorderStroke(1.dp, AntiqueGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("map_copy_gps_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy GPS", style = MaterialTheme.typography.labelLarge)
                    }

                    OutlinedButton(
                        onClick = {
                            val opened = dialClub(context)
                            if (!opened) {
                                copyToClipboard(context, "Goan Gymkhana Phone", ClubCatalog.PHONE)
                                onNotifyFeedback(
                                    "Copied phone ${ClubCatalog.PHONE} (no dialer available).",
                                    false
                                )
                            }
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("map_call_club_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call Gate", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        // Arrival Wayfinder Guides
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Route,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Nairobi Approach Routes",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        items(ClubCatalog.arrivalRoutes) { route ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = route.origin,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = route.distanceLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = route.guidance,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ClubLocationPreviewMapCard(
    onOpenFullMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onOpenFullMap,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightGreen),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .testTag("home_map_preview_card")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                InteractiveClubCartography(
                    mode = MapViewMode.NAIROBI_AREA,
                    pins = ClubCatalog.areaMapPins.take(3),
                    selectedPin = ClubCatalog.areaMapPins.first(),
                    zoomLevel = 1f,
                    panOffset = Offset.Zero,
                    onGestureTransform = { _, _ -> },
                    onPinSelected = { onOpenFullMap() },
                    enableGestures = false
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MidnightGreen.copy(alpha = 0.88f),
                    border = BorderStroke(1.dp, AntiqueGold),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Explore,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Open Interactive Map",
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmIvory,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            HorizontalDivider(color = AntiqueGold.copy(alpha = 0.35f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Ngara Road · Opposite National Museums of Kenya",
                        style = MaterialTheme.typography.labelMedium,
                        color = WarmIvory,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Tap for street guide, club grounds pins & turn-by-turn directions",
                        style = MaterialTheme.typography.bodySmall,
                        color = AntiqueGold.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
internal fun InteractiveClubCartography(
    mode: MapViewMode,
    pins: List<ClubMapPin>,
    selectedPin: ClubMapPin,
    zoomLevel: Float,
    panOffset: Offset,
    onGestureTransform: (panDelta: Offset, zoomDelta: Float) -> Unit,
    onPinSelected: (ClubMapPin) -> Unit,
    enableGestures: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ClubBeaconPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600),
            repeatMode = RepeatMode.Restart
        ),
        label = "BeaconPulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600),
            repeatMode = RepeatMode.Restart
        ),
        label = "BeaconPulseAlpha"
    )

    val gestureModifier = if (enableGestures) {
        Modifier.pointerInput(mode) {
            detectTransformGestures { _, pan, zoom, _ ->
                onGestureTransform(pan, zoom)
            }
        }
    } else {
        Modifier
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0C221A), Color(0xFF092F22), Color(0xFF071D15))
                )
            )
            .then(gestureModifier)
    ) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomLevel
                    scaleY = zoomLevel
                    translationX = panOffset.x
                    translationY = panOffset.y
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Subtle Topographic Map Grid
                val gridStepX = w / 8f
                val gridStepY = h / 6f
                for (i in 1..7) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.045f),
                        start = Offset(i * gridStepX, 0f),
                        end = Offset(i * gridStepX, h),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                for (j in 1..5) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.045f),
                        start = Offset(0f, j * gridStepY),
                        end = Offset(w, j * gridStepY),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (mode == MapViewMode.NAIROBI_AREA) {
                    // Nairobi River corridor
                    val riverPath = Path().apply {
                        moveTo(0f, h * 0.64f)
                        cubicTo(w * 0.30f, h * 0.56f, w * 0.62f, h * 0.70f, w, h * 0.55f)
                    }
                    drawPath(
                        path = riverPath,
                        color = Color(0xFF286F7A).copy(alpha = 0.48f),
                        style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // National Museums Botanical Park block (left of Kipande/Museum Hill)
                    drawRoundRect(
                        color = Color(0xFF174F3B).copy(alpha = 0.72f),
                        topLeft = Offset(w * 0.14f, h * 0.28f),
                        size = Size(w * 0.22f, h * 0.26f),
                        cornerRadius = CornerRadius(14.dp.toPx())
                    )

                    // Goan Gymkhana Sports Compound block
                    drawRoundRect(
                        color = HeritageGreen.copy(alpha = 0.92f),
                        topLeft = Offset(w * 0.47f, h * 0.33f),
                        size = Size(w * 0.24f, h * 0.23f),
                        cornerRadius = CornerRadius(14.dp.toPx())
                    )
                    drawRoundRect(
                        color = AntiqueGold.copy(alpha = 0.55f),
                        topLeft = Offset(w * 0.47f, h * 0.33f),
                        size = Size(w * 0.24f, h * 0.23f),
                        cornerRadius = CornerRadius(14.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Major Arterials (Uhuru Hwy / Museum Hill / Kipande / Ngara Rd)
                    val roadColor = Color(0xFF335549)
                    val highwayColor = Color(0xFF476D5F)

                    // Uhuru Highway -> Waiyaki Way diagonal
                    drawLine(
                        color = highwayColor,
                        start = Offset(w * 0.22f, 0f),
                        end = Offset(w * 0.50f, h),
                        strokeWidth = 9.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Museum Hill Interchange loop
                    drawCircle(
                        color = highwayColor,
                        radius = 18.dp.toPx(),
                        center = Offset(w * 0.35f, h * 0.22f),
                        style = Stroke(width = 6.dp.toPx())
                    )
                    // Kipande Road
                    drawLine(
                        color = roadColor,
                        start = Offset(w * 0.38f, h * 0.23f),
                        end = Offset(w * 0.67f, h * 0.77f),
                        strokeWidth = 6.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Ngara Road (East-West past Gymkhana)
                    drawLine(
                        color = roadColor,
                        start = Offset(w * 0.34f, h * 0.30f),
                        end = Offset(w * 0.92f, h * 0.60f),
                        strokeWidth = 7.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Murang'a Road / Globe Cinema link
                    drawLine(
                        color = roadColor,
                        start = Offset(w * 0.67f, h * 0.77f),
                        end = Offset(w * 0.95f, h * 0.46f),
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Highlighted Approach Path from CBD / Museum Hill to Gate
                    val approachPath = Path().apply {
                        moveTo(w * 0.67f, h * 0.77f)
                        lineTo(w * 0.61f, h * 0.52f)
                        lineTo(w * 0.56f, h * 0.44f)
                    }
                    drawPath(
                        path = approachPath,
                        color = AntiqueGold,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    )
                } else {
                    // CLUB GROUNDS CAMPUS PLAN
                    // Ngara Road along bottom
                    drawLine(
                        color = Color(0xFF476D5F),
                        start = Offset(0f, h * 0.88f),
                        end = Offset(w, h * 0.88f),
                        strokeWidth = 16.dp.toPx()
                    )
                    // Main Compound Lawn
                    drawRoundRect(
                        color = Color(0xFF124634),
                        topLeft = Offset(w * 0.08f, h * 0.12f),
                        size = Size(w * 0.84f, h * 0.70f),
                        cornerRadius = CornerRadius(18.dp.toPx())
                    )
                    // Swimming Pool Zone (top-left behind clubhouse)
                    drawRoundRect(
                        color = Color(0xFF1F7A8C).copy(alpha = 0.85f),
                        topLeft = Offset(w * 0.14f, h * 0.18f),
                        size = Size(w * 0.20f, h * 0.16f),
                        cornerRadius = CornerRadius(10.dp.toPx())
                    )
                    // 1943 Clubhouse Stone Footprint
                    drawRoundRect(
                        color = AntiqueGold.copy(alpha = 0.28f),
                        topLeft = Offset(w * 0.38f, h * 0.20f),
                        size = Size(w * 0.18f, h * 0.18f),
                        cornerRadius = CornerRadius(8.dp.toPx())
                    )
                    drawRoundRect(
                        color = AntiqueGold,
                        topLeft = Offset(w * 0.38f, h * 0.20f),
                        size = Size(w * 0.18f, h * 0.18f),
                        cornerRadius = CornerRadius(8.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    // AstroTurf Football Pitch
                    drawRoundRect(
                        color = Color(0xFF1E6E46),
                        topLeft = Offset(w * 0.46f, h * 0.44f),
                        size = Size(w * 0.36f, h * 0.28f),
                        cornerRadius = CornerRadius(10.dp.toPx())
                    )
                    drawRoundRect(
                        color = WarmIvory.copy(alpha = 0.55f),
                        topLeft = Offset(w * 0.48f, h * 0.46f),
                        size = Size(w * 0.32f, h * 0.24f),
                        cornerRadius = CornerRadius(4.dp.toPx()),
                        style = Stroke(width = 1.2.dp.toPx())
                    )
                    // Indoor Sports Hall
                    drawRoundRect(
                        color = Color(0xFF284B3E),
                        topLeft = Offset(w * 0.14f, h * 0.48f),
                        size = Size(w * 0.26f, h * 0.22f),
                        cornerRadius = CornerRadius(10.dp.toPx())
                    )
                }

                // Pulsing Beacon around primary pin
                pins.firstOrNull { it.isPrimaryClub }?.let { primary ->
                    val center = Offset(w * primary.relX, h * primary.relY)
                    drawCircle(
                        color = AntiqueGold.copy(alpha = pulseAlpha),
                        radius = 22.dp.toPx() * pulseScale,
                        center = center
                    )
                }
            }

            // Road / Landmark Watermark Labels inside Canvas
            if (mode == MapViewMode.NAIROBI_AREA) {
                MapStreetTag(
                    text = "MUSEUM HILL",
                    modifier = Modifier.offset {
                        IntOffset((widthPx * 0.12f).roundToInt(), (heightPx * 0.12f).roundToInt())
                    }
                )
                MapStreetTag(
                    text = "NGARA RD",
                    modifier = Modifier.offset {
                        IntOffset((widthPx * 0.68f).roundToInt(), (heightPx * 0.52f).roundToInt())
                    }
                )
            } else {
                MapStreetTag(
                    text = "NGARA RD GATE",
                    modifier = Modifier.offset {
                        IntOffset((widthPx * 0.38f).roundToInt(), (heightPx * 0.89f).roundToInt())
                    }
                )
            }

            // Interactive Pin Markers
            pins.forEach { pin ->
                val isSelected = pin.id == selectedPin.id
                val pinSizePx = with(density) { 36.dp.toPx() }
                val xPx = (widthPx * pin.relX - pinSizePx / 2f).roundToInt()
                val yPx = (heightPx * pin.relY - pinSizePx / 2f).roundToInt()

                Box(
                    modifier = Modifier
                        .offset { IntOffset(xPx, yPx) }
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isSelected -> AntiqueGold
                                pin.isPrimaryClub -> HeritageGreen
                                else -> MidnightGreen.copy(alpha = 0.92f)
                            }
                        )
                        .clickable { onPinSelected(pin) }
                        .testTag("map_pin_${pin.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (pin.isPrimaryClub) Icons.Filled.LocationOn else Icons.Filled.Park,
                        contentDescription = pin.title,
                        tint = if (isSelected) MidnightGreen else AntiqueGold,
                        modifier = Modifier.size(if (pin.isPrimaryClub) 22.dp else 18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MapStreetTag(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = WarmIvory.copy(alpha = 0.55f),
        modifier = modifier
    )
}

@Composable
private fun MapControlIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = CircleShape,
        color = MidnightGreen.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.8f)),
        modifier = Modifier
            .size(44.dp)
            .minimumInteractiveComponentSize()
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxSize()
                .testTag(testTag)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = AntiqueGold,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun dialClub(context: Context): Boolean {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+254720526523"))
    return runCatching { context.startActivity(intent) }.isSuccess
}

fun openClubDirections(context: Context): Boolean {
    val geoUri = Uri.parse(
        "geo:${ClubCatalog.LATITUDE},${ClubCatalog.LONGITUDE}?q=${ClubCatalog.LATITUDE},${ClubCatalog.LONGITUDE}(The+Goan+Gymkhana+Nairobi)"
    )
    val geoOpened = runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, geoUri))
    }.isSuccess
    if (geoOpened) return true

    val webMapsUri = Uri.parse(
        "https://www.google.com/maps/search/?api=1&query=${ClubCatalog.LATITUDE},${ClubCatalog.LONGITUDE}"
    )
    return runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, webMapsUri))
    }.isSuccess
}

fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
}

@Preview(showBackground = true)
@Composable
private fun ClubMapScreenPreview() {
    GoanGymkhanaTheme {
        ClubMapScreen(
            onBack = {},
            onBookFacility = {},
            onNotifyFeedback = { _, _ -> }
        )
    }
}
