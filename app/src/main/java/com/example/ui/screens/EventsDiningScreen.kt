package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.ClubCatalog
import com.example.data.ClubEnquiry
import com.example.data.ClubEventHighlight
import com.example.data.EnquiryStatus
import com.example.data.UiState
import com.example.data.formatClubTimestamp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.WarmIvory

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EventsDiningScreen(
    defaultName: String,
    enquiriesState: UiState<List<ClubEnquiry>>,
    isSubmitting: Boolean,
    onSubmitEnquiry: (
        senderName: String,
        phone: String,
        enquiryType: String,
        preferredDate: String?,
        guestCount: Int?,
        message: String,
        onSuccess: () -> Unit
    ) -> Unit,
    onCloseEnquiry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var senderName by remember(defaultName) { mutableStateOf(defaultName) }
    var phone by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ClubCatalog.enquiryTypes.first()) }
    var preferredDate by remember { mutableStateOf("") }
    var guestCountText by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    var eventToReserve by remember { mutableStateOf<ClubEventHighlight?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "CLUB CALENDAR & HOSPITALITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Upcoming Club Gatherings",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Join fellow members for 90th-anniversary milestones, sporting socials, and veranda hospitality. Provisional dates are confirmed by management upon reservation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Upcoming Club Gatherings in Heritage Green & Antique Gold
        items(ClubCatalog.eventHighlights, key = { it.id }) { gathering ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = HeritageGreen),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.72f)),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .testTag("event_card_${gathering.id}")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = AntiqueGold.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.8f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = if (gathering.actionType == "Dining & Bar") {
                                        Icons.Filled.Restaurant
                                    } else {
                                        Icons.Filled.Celebration
                                    },
                                    contentDescription = null,
                                    tint = AntiqueGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = gathering.category.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AntiqueGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = gathering.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = WarmIvory
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Event date",
                            tint = AntiqueGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = gathering.dateLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = AntiqueGold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Event location",
                            tint = WarmIvory.copy(alpha = 0.75f),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = gathering.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmIvory.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = gathering.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmIvory.copy(alpha = 0.9f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { eventToReserve = gathering },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AntiqueGold,
                            contentColor = MidnightGreen
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("reserve_spot_${gathering.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EventAvailable,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reserve Spot",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Custom Private Event & Hospitality Enquiry Form
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = HeritageGreen,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "PRIVATE FUNCTIONS & DINING DESK",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = "Private Event or Dining Enquiry",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Planning a birthday, anniversary, corporate meeting, or table booking? Official menus, capacities, and hire terms are confirmed by management.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClubCatalog.enquiryTypes.forEach { type ->
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = senderName,
                        onValueChange = { if (it.length <= 80) senderName = it },
                        label = { Text("Your Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("enquiry_name_input")
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 30) phone = it },
                        label = { Text("Phone / WhatsApp (+254...)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("enquiry_phone_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = preferredDate,
                            onValueChange = { if (it.length <= 30) preferredDate = it },
                            label = { Text("Preferred Date (Opt)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = guestCountText,
                            onValueChange = { input ->
                                if (input.length <= 3 && input.all { it.isDigit() }) {
                                    guestCountText = input
                                }
                            },
                            label = { Text("Guests (Opt)") },
                            singleLine = true,
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    OutlinedTextField(
                        value = message,
                        onValueChange = { if (it.length <= 600) message = it },
                        label = { Text("Enquiry Details") },
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("enquiry_message_input")
                    )

                    if (localError != null) {
                        Text(
                            text = localError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            val guests = guestCountText.trim().toIntOrNull()
                            when {
                                senderName.trim().length < 2 ->
                                    localError = "Please enter your name (at least 2 characters)."
                                phone.trim().length < 6 ->
                                    localError = "Please enter a valid contact phone number."
                                guestCountText.isNotBlank() && (guests == null || guests !in 1..300) ->
                                    localError = "Guest count must be between 1 and 300."
                                message.trim().length < 5 ->
                                    localError = "Please include at least a brief message (5+ characters)."
                                else -> {
                                    localError = null
                                    onSubmitEnquiry(
                                        senderName,
                                        phone,
                                        selectedType,
                                        preferredDate.takeIf { it.isNotBlank() },
                                        guests,
                                        message
                                    ) {
                                        preferredDate = ""
                                        guestCountText = ""
                                        message = ""
                                    }
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HeritageGreen,
                            contentColor = WarmIvory
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("submit_enquiry_btn")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = WarmIvory,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Submit Enquiry", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        // User's Event Reservations & Enquiries Tracker
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "My Event Reservations & Enquiries",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        when (enquiriesState) {
            UiState.Loading -> {
                item { CircularProgressIndicator() }
            }
            is UiState.Error -> {
                item {
                    Text(
                        text = enquiriesState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is UiState.Success -> {
                if (enquiriesState.data.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .widthIn(max = 720.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "No gathering spots reserved or hospitality enquiries sent yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(enquiriesState.data, key = { it.id }) { enquiry ->
                        val status = EnquiryStatus.fromWire(enquiry.status)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .widthIn(max = 720.dp)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = enquiry.enquiryType,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    StatusPill(label = status.label, wireStatus = status.wireValue)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = enquiry.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (!enquiry.preferredDate.isNullOrBlank() || enquiry.guestCount != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = buildString {
                                            if (!enquiry.preferredDate.isNullOrBlank()) {
                                                append("Date: ${enquiry.preferredDate}")
                                            }
                                            if (enquiry.guestCount != null) {
                                                if (isNotEmpty()) append(" · ")
                                                append("${enquiry.guestCount} guests")
                                            }
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Submitted ${formatClubTimestamp(enquiry.createdAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                if (status == EnquiryStatus.SUBMITTED && enquiry.id.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = { onCloseEnquiry(enquiry.id) },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.size(6.dp))
                                        Text("Close Request")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val reserveTarget = eventToReserve
    if (reserveTarget != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { eventToReserve = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ReserveGatheringSheetContent(
                gathering = reserveTarget,
                defaultName = defaultName,
                isSubmitting = isSubmitting,
                onConfirm = { name, contactPhone, guests, note ->
                    val combinedMessage = buildString {
                        append("Spot reservation for: ${reserveTarget.title}")
                        if (note.isNotBlank()) {
                            append(" — ${note.trim()}")
                        }
                    }.take(600)
                    onSubmitEnquiry(
                        name,
                        contactPhone,
                        reserveTarget.actionType,
                        reserveTarget.dateLabel.substringBefore(" (").take(30),
                        guests,
                        combinedMessage
                    ) {
                        eventToReserve = null
                    }
                }
            )
        }
    }
}

@Composable
private fun ReserveGatheringSheetContent(
    gathering: ClubEventHighlight,
    defaultName: String,
    isSubmitting: Boolean,
    onConfirm: (name: String, phone: String, guests: Int, note: String) -> Unit
) {
    var name by remember(defaultName) { mutableStateOf(defaultName) }
    var contactPhone by remember { mutableStateOf("") }
    var guests by remember { mutableIntStateOf(2) }
    var note by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = HeritageGreen,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = gathering.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = AntiqueGold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = gathering.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = WarmIvory
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${gathering.dateLabel} · ${gathering.location}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AntiqueGold.copy(alpha = 0.95f)
                )
            }
        }

        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 80) name = it },
            label = { Text("Your Name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reserve_name_input")
        )

        OutlinedTextField(
            value = contactPhone,
            onValueChange = { if (it.length <= 30) contactPhone = it },
            label = { Text("Phone / WhatsApp (+254...)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("reserve_phone_input")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Filled.Group, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Column {
                    Text(
                        text = "Seats / Guests",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "1 to 20 attendees",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { if (guests > 1) guests-- },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrease guests")
                }
                Text(
                    text = guests.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = { if (guests < 20) guests++ },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Increase guests")
                }
            }
        }

        OutlinedTextField(
            value = note,
            onValueChange = { if (it.length <= 300) note = it },
            label = { Text("Dietary / Seating Notes (Optional)") },
            minLines = 2,
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        if (validationError != null) {
            Text(
                text = validationError!!,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                when {
                    name.trim().length < 2 ->
                        validationError = "Please enter your name (at least 2 characters)."
                    contactPhone.trim().length < 6 ->
                        validationError = "Please enter a valid contact number."
                    else -> {
                        validationError = null
                        onConfirm(name, contactPhone, guests, note)
                    }
                }
            },
            enabled = !isSubmitting,
            colors = ButtonDefaults.buttonColors(
                containerColor = AntiqueGold,
                contentColor = MidnightGreen
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .minimumInteractiveComponentSize()
                .testTag("confirm_reserve_spot_btn")
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MidnightGreen,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Confirm Spot Reservation",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EventsDiningScreenPreview() {
    GoanGymkhanaTheme {
        EventsDiningScreen(
            defaultName = "Club Member",
            enquiriesState = UiState.Success(emptyList()),
            isSubmitting = false,
            onSubmitEnquiry = { _, _, _, _, _, _, _ -> },
            onCloseEnquiry = {}
        )
    }
}
