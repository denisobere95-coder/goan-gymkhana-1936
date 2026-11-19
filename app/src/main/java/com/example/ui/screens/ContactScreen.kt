package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.data.ClubCatalog
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.WarmIvory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

private const val PLACEHOLDER_MAPS_KEY = "MY_GOOGLE_MAPS_API_KEY"

internal fun hasConfiguredMapsApiKey(apiKey: String = BuildConfig.MAPS_API_KEY): Boolean {
    val trimmed = apiKey.trim()
    return trimmed.isNotEmpty() && trimmed != PLACEHOLDER_MAPS_KEY
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContactScreen(
    defaultName: String,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onOpenFullMap: () -> Unit,
    onSubmitContactMessage: (
        senderName: String,
        phone: String,
        enquiryType: String,
        preferredDate: String?,
        guestCount: Int?,
        message: String,
        onSuccess: () -> Unit
    ) -> Unit,
    onNotifyFeedback: (message: String, isError: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hasMapsKey = remember { hasConfiguredMapsApiKey() }

    val clubLatLng = remember { LatLng(ClubCatalog.LATITUDE, ClubCatalog.LONGITUDE) }
    val museumLatLng = remember { LatLng(ClubCatalog.MUSEUM_LATITUDE, ClubCatalog.MUSEUM_LONGITUDE) }
    val midpointLatLng = remember {
        LatLng(
            (ClubCatalog.LATITUDE + ClubCatalog.MUSEUM_LATITUDE) / 2.0,
            (ClubCatalog.LONGITUDE + ClubCatalog.MUSEUM_LONGITUDE) / 2.0
        )
    }

    var mapType by remember { mutableStateOf(MapType.NORMAL) }
    var selectedLocatorPin by remember {
        mutableStateOf(ClubCatalog.areaMapPins.first())
    }
    var locatorZoom by remember { mutableFloatStateOf(1.15f) }
    var locatorPan by remember { mutableStateOf(Offset.Zero) }

    var senderName by remember(defaultName) { mutableStateOf(defaultName) }
    var phone by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf("General") }
    var message by remember { mutableStateOf("") }
    var messageSent by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(midpointLatLng, 16.5f)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("contact_lazy_column"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Editorial Heritage Header
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
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("contact_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AntiqueGold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "CONTACT & LOCATION",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold
                        )
                        Text(
                            text = "Find The Goan Gymkhana",
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
                            text = ClubCatalog.COORDINATES,
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Google Maps & Relative Position Visualizer Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.5.dp, AntiqueGold.copy(alpha = 0.75f)),
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .testTag("contact_map_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ngara Road & National Museum Locator",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = ClubCatalog.MUSEUM_DISTANCE_LABEL,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = HeritageGreen
                        ) {
                            Text(
                                text = if (hasMapsKey) "LIVE MAPS" else "VECTOR MAP",
                                style = MaterialTheme.typography.labelSmall,
                                color = AntiqueGold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Landmark Coordinate Pair Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedLocatorPin.id == "goan_gymkhana",
                            onClick = {
                                selectedLocatorPin = ClubCatalog.areaMapPins.first { it.id == "goan_gymkhana" }
                                if (hasMapsKey) {
                                    scope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(clubLatLng, 17f)
                                        )
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text("Club (${ClubCatalog.COORDINATES})") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = HeritageGreen,
                                selectedLabelColor = WarmIvory,
                                selectedLeadingIconColor = AntiqueGold
                            ),
                            modifier = Modifier.testTag("contact_chip_club_pin")
                        )
                        FilterChip(
                            selected = selectedLocatorPin.id == "national_museums",
                            onClick = {
                                selectedLocatorPin = ClubCatalog.areaMapPins.first { it.id == "national_museums" }
                                if (hasMapsKey) {
                                    scope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngZoom(museumLatLng, 17f)
                                        )
                                    }
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Museum,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text("National Museum of Kenya") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AntiqueGold,
                                selectedLabelColor = MidnightGreen,
                                selectedLeadingIconColor = MidnightGreen
                            ),
                            modifier = Modifier.testTag("contact_chip_museum_pin")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (hasMapsKey) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            listOf(
                                MapType.NORMAL to "Map",
                                MapType.SATELLITE to "Satellite",
                                MapType.HYBRID to "Hybrid"
                            ).forEach { (type, label) ->
                                FilterChip(
                                    selected = mapType == type,
                                    onClick = { mapType = type },
                                    label = { Text(label) },
                                    modifier = Modifier.testTag("contact_map_type_${label.lowercase()}")
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp)
                            .testTag("contact_map_viewport")
                    ) {
                        if (hasMapsKey) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                properties = MapProperties(mapType = mapType),
                                uiSettings = MapUiSettings(
                                    zoomControlsEnabled = true,
                                    compassEnabled = true,
                                    mapToolbarEnabled = true
                                )
                            ) {
                                Marker(
                                    state = remember { MarkerState(position = clubLatLng) },
                                    title = ClubCatalog.CLUB_NAME,
                                    snippet = "Ngara Road (${ClubCatalog.COORDINATES})",
                                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                )
                                Marker(
                                    state = remember { MarkerState(position = museumLatLng) },
                                    title = ClubCatalog.MUSEUM_NAME,
                                    snippet = "Directly opposite across Ngara Rd / Museum Hill",
                                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                                )
                                Circle(
                                    center = clubLatLng,
                                    radius = 55.0,
                                    strokeColor = AntiqueGold,
                                    fillColor = HeritageGreen.copy(alpha = 0.18f),
                                    strokeWidth = 3f
                                )
                                Polyline(
                                    points = listOf(museumLatLng, clubLatLng),
                                    color = AntiqueGold,
                                    width = 6f
                                )
                            }
                        } else {
                            InteractiveClubCartography(
                                mode = MapViewMode.NAIROBI_AREA,
                                pins = ClubCatalog.areaMapPins.take(4),
                                selectedPin = selectedLocatorPin,
                                zoomLevel = locatorZoom,
                                panOffset = locatorPan,
                                onGestureTransform = { panDelta, zoomDelta ->
                                    val nextZoom = (locatorZoom * zoomDelta).coerceIn(1f, 2.2f)
                                    locatorZoom = nextZoom
                                    val maxOffset = (nextZoom - 1f) * 200f
                                    locatorPan = Offset(
                                        x = (locatorPan.x + panDelta.x).coerceIn(-maxOffset, maxOffset),
                                        y = (locatorPan.y + panDelta.y).coerceIn(-maxOffset, maxOffset)
                                    )
                                },
                                onPinSelected = { pin -> selectedLocatorPin = pin }
                            )
                        }

                        // Coordinate & Relative Context Overlay Pill
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MidnightGreen.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.8f)),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
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
                                    text = "${ClubCatalog.LATITUDE}, ${ClubCatalog.LONGITUDE}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmIvory
                                )
                            }
                        }

                        // Fit Both Landmarks Button
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MidnightGreen.copy(alpha = 0.9f),
                            border = BorderStroke(1.dp, AntiqueGold),
                            onClick = {
                                locatorZoom = 1.15f
                                locatorPan = Offset.Zero
                                selectedLocatorPin = ClubCatalog.areaMapPins.first()
                                if (hasMapsKey) {
                                    val bounds = LatLngBounds.builder()
                                        .include(clubLatLng)
                                        .include(museumLatLng)
                                        .build()
                                    scope.launch {
                                        cameraPositionState.animate(
                                            CameraUpdateFactory.newLatLngBounds(bounds, 96)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                                .minimumInteractiveComponentSize()
                                .testTag("contact_recenter_both_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MyLocation,
                                    contentDescription = "Fit Club and Museum",
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "Show Both Pins",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = WarmIvory
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = HeritageGreen.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = selectedLocatorPin.title,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedLocatorPin.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!hasMapsKey) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Interactive vector locator active. Add MAPS_API_KEY in AI Studio Secrets to enable live Google Maps tiles.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                if (openClubDirections(context)) {
                                    onNotifyFeedback("Opening Google Maps at ${ClubCatalog.COORDINATES}.", false)
                                } else {
                                    onNotifyFeedback("Unable to open maps app on this device.", true)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AntiqueGold,
                                contentColor = MidnightGreen
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("contact_open_google_maps_btn")
                        ) {
                            Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Directions", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onOpenFullMap,
                            border = BorderStroke(1.dp, AntiqueGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("contact_open_grounds_map_btn")
                        ) {
                            Icon(Icons.Filled.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Grounds Map", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        // Club Contact Details & Actions Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Club Contact Channels",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    ContactDetailRow(
                        icon = Icons.Filled.LocationOn,
                        title = "Street Address & GPS",
                        detail = "${ClubCatalog.ADDRESS}\nOfficial Coordinates: ${ClubCatalog.COORDINATES}"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    ContactDetailRow(
                        icon = Icons.Filled.Schedule,
                        title = "Listed Visiting Hours",
                        detail = "${ClubCatalog.LISTED_HOURS_WEEKDAY}\n${ClubCatalog.LISTED_HOURS_SUNDAY}"
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    ContactDetailRow(
                        icon = Icons.Filled.Call,
                        title = "Telephone",
                        detail = ClubCatalog.PHONE
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                    ContactDetailRow(
                        icon = Icons.Filled.Email,
                        title = "Club Desk Email",
                        detail = "${ClubCatalog.EMAIL} (listed prior address; confirm active inbox)"
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (!dialClub(context)) {
                                    copyToClipboard(context, "Goan Gymkhana Phone", ClubCatalog.PHONE)
                                    onNotifyFeedback("Copied ${ClubCatalog.PHONE} to clipboard.", false)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HeritageGreen,
                                contentColor = WarmIvory
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("contact_call_btn")
                        ) {
                            Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call")
                        }
                        OutlinedButton(
                            onClick = {
                                if (!emailClub(context)) {
                                    copyToClipboard(context, "Goan Gymkhana Email", ClubCatalog.EMAIL)
                                    onNotifyFeedback("Copied ${ClubCatalog.EMAIL} to clipboard.", false)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("contact_email_btn")
                        ) {
                            Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Email")
                        }
                        OutlinedButton(
                            onClick = {
                                copyToClipboard(context, "Goan Gymkhana GPS", ClubCatalog.COORDINATES)
                                onNotifyFeedback("Copied GPS (${ClubCatalog.COORDINATES}) to clipboard.", false)
                            },
                            border = BorderStroke(1.dp, AntiqueGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("contact_copy_gps_btn")
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy GPS")
                        }
                    }
                }
            }
        }

        // Direct Message / Contact Desk Form -> Firestore club_enquiries
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f)),
                modifier = Modifier
                    .widthIn(max = 760.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Map, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Text(
                            text = "Message the Contact Desk",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Questions about visiting Ngara Road, directions, guest access, or club bookings are saved straight to our Firestore club desk queue.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClubCatalog.enquiryTypes.forEach { topic ->
                            FilterChip(
                                selected = selectedTopic == topic,
                                onClick = { selectedTopic = topic },
                                label = { Text(topic) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = senderName,
                        onValueChange = { senderName = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_sender_name_input")
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("+254 7XX XXX XXX") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_phone_input")
                    )
                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message / Visiting Question") },
                        placeholder = { Text("Ask about directions, gate parking, or visiting hours...") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_message_input")
                    )

                    AnimatedVisibility(visible = messageSent) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = HeritageGreen,
                            border = BorderStroke(1.dp, AntiqueGold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = AntiqueGold)
                                Text(
                                    text = "Message sent to Goan Gymkhana Contact Desk. Track updates in Portal.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WarmIvory
                                )
                            }
                        }
                    }

                    val canSend = senderName.isNotBlank() && phone.isNotBlank() && message.isNotBlank() && !isSubmitting
                    Button(
                        onClick = {
                            messageSent = false
                            onSubmitContactMessage(
                                senderName.trim(),
                                phone.trim(),
                                selectedTopic,
                                null,
                                null,
                                message.trim()
                            ) {
                                message = ""
                                messageSent = true
                            }
                        },
                        enabled = canSend,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AntiqueGold,
                            contentColor = MidnightGreen
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("contact_send_message_btn")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MidnightGreen
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Send Contact Message",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactDetailRow(
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

private fun emailClub(context: Context): Boolean {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:${ClubCatalog.EMAIL}")
        putExtra(Intent.EXTRA_SUBJECT, "Enquiry — The Goan Gymkhana Nairobi")
    }
    return runCatching { context.startActivity(intent) }.isSuccess
}

@Preview(showBackground = true)
@Composable
private fun ContactScreenPreview() {
    GoanGymkhanaTheme {
        ContactScreen(
            defaultName = "Anthony D'Souza",
            isSubmitting = false,
            onBack = {},
            onOpenFullMap = {},
            onSubmitContactMessage = { _, _, _, _, _, _, _ -> },
            onNotifyFeedback = { _, _ -> }
        )
    }
}
