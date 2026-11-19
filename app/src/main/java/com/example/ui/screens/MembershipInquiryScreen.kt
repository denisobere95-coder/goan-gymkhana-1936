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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.MembershipInquiry
import com.example.data.MembershipInquiryStatus
import com.example.data.UiState
import com.example.data.formatClubTimestamp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.StatusApproved
import com.example.ui.theme.WarmIvory

private val quickInterestOptions = listOf(
    "Individual Membership",
    "Family Membership",
    "Swimming",
    "AstroTurf Football",
    "Badminton",
    "Table Tennis",
    "Social & Dining"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MembershipInquiryScreen(
    defaultName: String,
    defaultEmail: String,
    inquiriesState: UiState<List<MembershipInquiry>>,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onSubmitInquiry: (
        name: String,
        email: String,
        interest: String,
        onSuccess: () -> Unit
    ) -> Unit,
    onCloseInquiry: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var name by remember(defaultName) { mutableStateOf(defaultName) }
    var email by remember(defaultEmail) { mutableStateOf(defaultEmail) }
    var interest by remember { mutableStateOf("Individual Membership") }
    var validationError by remember { mutableStateOf<String?>(null) }
    var submittedConfirmation by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("membership_inquiry_lazy_column"),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header Bar
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
                            .testTag("inquiry_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AntiqueGold
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PROSPECTIVE MEMBERS",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold
                        )
                        Text(
                            text = "Membership Inquiry",
                            style = MaterialTheme.typography.titleLarge,
                            color = WarmIvory
                        )
                    }
                }
            }
        }

        // Welcome Heritage Banner
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = HeritageGreen,
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MarkEmailRead,
                            contentDescription = null,
                            tint = AntiqueGold
                        )
                        Text(
                            text = "EXPRESS YOUR INTEREST",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Join Our Ngara Road Community",
                        style = MaterialTheme.typography.headlineSmall,
                        color = WarmIvory
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Share your name, email, and primary interest below. Our membership desk will follow up with official club details, categories, and fee schedules.",
                        style = MaterialTheme.typography.bodySmall,
                        color = WarmIvory.copy(alpha = 0.88f)
                    )
                }
            }
        }

        // Simple Inquiry Form Card (Name, Email, Interest)
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Inquiry Form",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { if (it.length <= 100) name = it },
                        label = { Text("Name") },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inquiry_name_input")
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { if (it.length <= 120) email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Filled.Email, contentDescription = null)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inquiry_email_input")
                    )

                    Text(
                        text = "Quick Select Interest",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        quickInterestOptions.forEach { option ->
                            val selected = interest.trim().equals(option, ignoreCase = true)
                            FilterChip(
                                selected = selected,
                                onClick = { interest = option },
                                label = { Text(option) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HeritageGreen,
                                    selectedLabelColor = WarmIvory
                                ),
                                modifier = Modifier.testTag(
                                    "inquiry_chip_${option.lowercase().replace(" ", "_").replace("&", "and")}"
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = interest,
                        onValueChange = { if (it.length <= 120) interest = it },
                        label = { Text("Interest") },
                        placeholder = { Text("e.g. Family Membership, Swimming, Badminton") },
                        leadingIcon = {
                            Icon(Icons.Filled.Interests, contentDescription = null)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inquiry_interest_input")
                    )

                    if (validationError != null) {
                        Text(
                            text = validationError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.testTag("inquiry_error_text")
                        )
                    }

                    if (submittedConfirmation != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StatusApproved.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, StatusApproved.copy(alpha = 0.55f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusApproved,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = submittedConfirmation!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            when {
                                name.trim().length < 2 ->
                                    validationError = "Please enter your name (at least 2 characters)."
                                !email.contains("@") || email.trim().length < 3 ->
                                    validationError = "Please enter a valid email address."
                                interest.trim().length < 2 ->
                                    validationError = "Please enter or select your area of interest."
                                else -> {
                                    validationError = null
                                    val chosenInterest = interest.trim()
                                    onSubmitInquiry(
                                        name.trim(),
                                        email.trim(),
                                        chosenInterest
                                    ) {
                                        submittedConfirmation =
                                            "Saved your inquiry for \"$chosenInterest\" to Firestore."
                                        interest = quickInterestOptions.first()
                                    }
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
                            .testTag("submit_membership_inquiry_btn")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MidnightGreen,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit Membership Inquiry",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Submitted Inquiries Tracker
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = "My Submitted Inquiries",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        when (inquiriesState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item {
                Text(
                    text = inquiriesState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            is UiState.Success -> {
                if (inquiriesState.data.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .widthIn(max = 720.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "No membership inquiries submitted yet. Complete the form above to express interest.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(inquiriesState.data, key = { it.id }) { inquiry ->
                        val status = MembershipInquiryStatus.fromWire(inquiry.status)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
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
                                        text = inquiry.interest,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatusPill(label = status.label, wireStatus = status.wireValue)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${inquiry.name} · ${inquiry.email}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Recorded ${formatClubTimestamp(inquiry.createdAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                if (status == MembershipInquiryStatus.SUBMITTED && inquiry.id.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = { onCloseInquiry(inquiry.id) },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Close Inquiry")
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

@Preview(showBackground = true)
@Composable
private fun MembershipInquiryScreenPreview() {
    GoanGymkhanaTheme {
        MembershipInquiryScreen(
            defaultName = "Anita De Souza",
            defaultEmail = "anita@example.com",
            inquiriesState = UiState.Success(
                listOf(
                    MembershipInquiry(
                        id = "preview_inq_1",
                        name = "Anita De Souza",
                        email = "anita@example.com",
                        interest = "Family Membership",
                        status = MembershipInquiryStatus.SUBMITTED.wireValue
                    )
                )
            ),
            isSubmitting = false,
            onBack = {},
            onSubmitInquiry = { _, _, _, _ -> }
        )
    }
}
