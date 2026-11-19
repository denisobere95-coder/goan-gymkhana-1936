package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.BookingStatus
import com.example.data.ClubCatalog
import com.example.data.ClubFacility
import com.example.data.FacilityBooking
import com.example.data.UiState
import com.example.data.formatClubTimestamp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.WarmIvory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingScreen(
    initialFacility: ClubFacility?,
    bookingsState: UiState<List<FacilityBooking>>,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSubmitBooking: (
        facility: ClubFacility,
        preferredDate: String,
        timeSlot: String,
        participants: Int,
        notes: String?,
        onSuccess: () -> Unit
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFacility by remember(initialFacility) {
        mutableStateOf(initialFacility ?: ClubCatalog.facilities.first())
    }

    val todayUtcMillis = remember {
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val tomorrowUtcMillis = remember(todayUtcMillis) {
        todayUtcMillis + 24L * 60L * 60L * 1000L
    }

    var selectedDateMillis by remember { mutableStateOf(tomorrowUtcMillis) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedSlot by remember { mutableStateOf(ClubCatalog.timeSlots[3]) }
    var participants by remember {
        mutableIntStateOf(2.coerceAtMost(selectedFacility.maxParticipants))
    }
    var notes by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedFacility.id) {
        if (participants > selectedFacility.maxParticipants) {
            participants = selectedFacility.maxParticipants
        }
    }

    val formattedDate = remember(selectedDateMillis) {
        formatUtcDate(selectedDateMillis)
    }
    val displayDate = remember(selectedDateMillis) {
        formatUtcDisplayDate(selectedDateMillis)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("booking_lazy_column"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Heritage Green Header
        item {
            Surface(
                color = HeritageGreen,
                modifier = Modifier
                    .widthIn(max = 720.dp)
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
                            .testTag("booking_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AntiqueGold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "FACILITY RESERVATIONS",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold
                        )
                        Text(
                            text = "Book a Club Facility",
                            style = MaterialTheme.typography.titleLarge,
                            color = WarmIvory
                        )
                    }
                }
            }
        }

        // Step 1: Select a Facility
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "1. Select a Facility",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Choose from Ngara Road's sports grounds, courts, or clubhouse spaces.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ClubCatalog.facilities.forEach { facility ->
                    val isSelected = selectedFacility.id == facility.id
                    Card(
                        onClick = { selectedFacility = facility },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                HeritageGreen
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) {
                                AntiqueGold
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("select_facility_${facility.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Image(
                                painter = rememberFacilityPlaceholderPainter(facility),
                                contentDescription = facility.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = facility.category.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) AntiqueGold else MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = facility.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isSelected) WarmIvory else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Up to ${facility.maxParticipants} participants · ${facility.verification.label}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) {
                                        WarmIvory.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                            Icon(
                                imageVector = if (isSelected) {
                                    Icons.Filled.CheckCircle
                                } else {
                                    Icons.Filled.RadioButtonUnchecked
                                },
                                contentDescription = if (isSelected) "Selected" else "Select",
                                tint = if (isSelected) AntiqueGold else MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        // Step 2: Date Picker & Session Details
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.6f)),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "2. Choose Date & Session",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = HeritageGreen.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.55f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = "Selected date",
                                    tint = HeritageGreen
                                )
                                Column {
                                    Text(
                                        text = "Selected Date",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = displayDate,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = formattedDate,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            OutlinedButton(
                                onClick = { showDatePicker = true },
                                border = BorderStroke(1.dp, HeritageGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("open_date_picker_btn")
                            ) {
                                Text("Change Date", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }

                    Text(
                        text = "Preferred Time Slot",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClubCatalog.timeSlots.forEach { slot ->
                            FilterChip(
                                selected = selectedSlot == slot,
                                onClick = { selectedSlot = slot },
                                label = { Text(slot) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HeritageGreen,
                                    selectedLabelColor = WarmIvory
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Group,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Column {
                                Text(
                                    text = "Participants",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "1 to ${selectedFacility.maxParticipants} guests",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (participants > 1) participants-- },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(Icons.Filled.Remove, contentDescription = "Decrease participants")
                            }
                            Text(
                                text = participants.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(
                                onClick = {
                                    if (participants < selectedFacility.maxParticipants) participants++
                                },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Increase participants")
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { if (it.length <= 400) notes = it },
                        label = { Text("Notes / Equipment / Group Details (Optional)") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("booking_screen_notes_input")
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
                            if (formattedDate.length < 4) {
                                validationError = "Please pick a valid reservation date."
                            } else {
                                validationError = null
                                onSubmitBooking(
                                    selectedFacility,
                                    formattedDate,
                                    selectedSlot,
                                    participants,
                                    notes
                                ) {
                                    notes = ""
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
                            .height(54.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("submit_reservation_btn")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = MidnightGreen,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.EventAvailable,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit Reservation Request",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Step 3: Recent Firestore Reservations Preview
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "My Submitted Reservations",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        when (bookingsState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item {
                Text(
                    text = bookingsState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            is UiState.Success -> {
                if (bookingsState.data.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .widthIn(max = 720.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "No reservation requests submitted yet. Select a facility and date above to submit.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(bookingsState.data.take(5), key = { it.id }) { booking ->
                        val status = BookingStatus.fromWire(booking.status)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .widthIn(max = 720.dp)
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
                                        text = booking.facilityName,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    StatusPill(label = status.label, wireStatus = status.wireValue)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${booking.preferredDate} · ${booking.timeSlot} · ${booking.participants} participants",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (!booking.notes.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Notes: ${booking.notes}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (!booking.managerNote.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Club Desk: ${booking.managerNote}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Submitted ${formatClubTimestamp(booking.createdAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return utcTimeMillis >= todayUtcMillis
                }
            }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDateMillis = millis
                        }
                        showDatePicker = false
                    },
                    modifier = Modifier.testTag("confirm_date_picker_btn")
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            Box(modifier = Modifier.verticalScroll(rememberScrollState())) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = HeritageGreen,
                        selectedDayContentColor = WarmIvory,
                        todayDateBorderColor = AntiqueGold,
                        todayContentColor = HeritageGreen
                    )
                )
            }
        }
    }
}

private fun formatUtcDate(utcMillis: Long): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return fmt.format(Date(utcMillis))
}

private fun formatUtcDisplayDate(utcMillis: Long): String {
    val fmt = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    return fmt.format(Date(utcMillis))
}

@Preview(showBackground = true)
@Composable
private fun BookingScreenPreview() {
    GoanGymkhanaTheme {
        BookingScreen(
            initialFacility = ClubCatalog.facilities.first(),
            bookingsState = UiState.Success(
                listOf(
                    FacilityBooking(
                        id = "preview_1",
                        facilityId = "swimming_pool",
                        facilityName = "Swimming Pool",
                        preferredDate = "2026-11-21",
                        timeSlot = "16:00 – 18:00",
                        participants = 2L,
                        notes = "Afternoon family lane session",
                        status = BookingStatus.PENDING.wireValue
                    )
                )
            ),
            isSubmitting = false,
            onBack = {},
            onSubmitBooking = { _, _, _, _, _, _ -> }
        )
    }
}
