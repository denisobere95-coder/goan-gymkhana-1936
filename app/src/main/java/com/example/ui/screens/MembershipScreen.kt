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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.ClubCatalog
import com.example.data.MembershipApplication
import com.example.data.MembershipStatus
import com.example.data.UiState
import com.example.data.formatClubTimestamp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.WarmIvory

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MembershipScreen(
    defaultName: String,
    defaultEmail: String,
    membershipsState: UiState<List<MembershipApplication>>,
    isSubmitting: Boolean,
    onSubmitMembership: (
        fullName: String,
        phone: String,
        email: String,
        category: String,
        interests: List<String>,
        notes: String?,
        onSuccess: () -> Unit
    ) -> Unit,
    onWithdrawMembership: (String) -> Unit,
    onOpenMembershipInquiry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var fullName by remember(defaultName) { mutableStateOf(defaultName) }
    var phone by remember { mutableStateOf("") }
    var email by remember(defaultEmail) { mutableStateOf(defaultEmail) }
    var selectedCategory by remember { mutableStateOf("Individual") }
    var selectedInterests by remember { mutableStateOf(setOf("Swimming", "Badminton")) }
    var notes by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = HeritageGreen,
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.Groups, contentDescription = null, tint = AntiqueGold)
                        Text(
                            text = "OPEN COMMUNITY MEMBERSHIP",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Become Part of Our Story",
                        style = MaterialTheme.typography.displaySmall,
                        color = WarmIvory
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Established in 1935–1936 and open to all communities across Nairobi. Submit your digital membership interest below; official fee schedules, proposer requirements, and eligibility rules are provided directly by the club committee.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarmIvory.copy(alpha = 0.88f)
                    )
                }
            }
        }

        // Quick Membership Inquiry Callout (Name, Email, Interest)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = AntiqueGold.copy(alpha = 0.16f)
                ),
                border = BorderStroke(1.dp, AntiqueGold),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MarkEmailRead,
                        contentDescription = null,
                        tint = HeritageGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Quick Membership Inquiry",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Just exploring? Express interest in seconds with your Name, Email & Interest.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onOpenMembershipInquiry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HeritageGreen,
                            contentColor = WarmIvory
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("open_membership_inquiry_btn")
                    ) {
                        Text("Inquire", style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.size(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Digital Membership Application Form
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Membership Application & Enquiry",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { if (it.length <= 100) fullName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("membership_name_input")
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 30) phone = it },
                        label = { Text("Telephone / WhatsApp (+254...)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("membership_phone_input")
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { if (it.length <= 120) email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("membership_email_input")
                    )

                    Text(
                        text = "Category Interest",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClubCatalog.membershipCategories.forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) }
                            )
                        }
                    }

                    Text(
                        text = "Sporting & Social Interests",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ClubCatalog.sportingInterests.forEach { sport ->
                            val isSelected = sport in selectedInterests
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedInterests = if (isSelected) {
                                        selectedInterests - sport
                                    } else if (selectedInterests.size < 8) {
                                        selectedInterests + sport
                                    } else {
                                        selectedInterests
                                    }
                                },
                                label = { Text(sport) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { if (it.length <= 500) notes = it },
                        label = { Text("Background / Notes for Committee (Optional)") },
                        minLines = 2,
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("membership_notes_input")
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
                            when {
                                fullName.trim().length < 2 ->
                                    localError = "Full name must be at least 2 characters."
                                phone.trim().length < 6 ->
                                    localError = "Please enter a valid telephone number."
                                !email.contains("@") || email.trim().length < 3 ->
                                    localError = "Please enter a valid email address."
                                else -> {
                                    localError = null
                                    onSubmitMembership(
                                        fullName,
                                        phone,
                                        email,
                                        selectedCategory,
                                        selectedInterests.toList(),
                                        notes
                                    ) {
                                        notes = ""
                                    }
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .minimumInteractiveComponentSize()
                            .testTag("submit_membership_btn")
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Filled.Badge, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("Submit Membership Application", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        // Application Tracker
        item {
            Column(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "My Application Status",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        when (membershipsState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item {
                Text(
                    text = membershipsState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is UiState.Success -> {
                if (membershipsState.data.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .widthIn(max = 720.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "You haven't submitted a membership application yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(membershipsState.data, key = { it.id }) { app ->
                        val status = MembershipStatus.fromWire(app.status)
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
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
                                        text = "${app.category} Membership",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    StatusPill(label = status.label, wireStatus = status.wireValue)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${app.fullName} · ${app.phone}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (app.interests.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Interests: ${app.interests.joinToString(", ")}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                if (!app.reviewNote.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Committee Note: ${app.reviewNote}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Submitted ${formatClubTimestamp(app.createdAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                if ((status == MembershipStatus.PENDING || status == MembershipStatus.UNDER_REVIEW) && app.id.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = { onWithdrawMembership(app.id) },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    ) {
                                        Icon(Icons.Filled.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.size(6.dp))
                                        Text("Withdraw Application")
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
private fun MembershipScreenPreview() {
    GoanGymkhanaTheme {
        MembershipScreen(
            defaultName = "Club Member",
            defaultEmail = "member@goangymkhana.com",
            membershipsState = UiState.Success(emptyList()),
            isSubmitting = false,
            onSubmitMembership = { _, _, _, _, _, _, _ -> },
            onWithdrawMembership = {}
        )
    }
}
