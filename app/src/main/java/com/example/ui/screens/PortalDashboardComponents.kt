package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ClubCatalog
import com.example.data.MemberPrivilegeNotice
import com.example.data.MemberProfile
import com.example.data.UiState
import com.example.data.formatClubTimestamp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.StatusApproved
import com.example.ui.theme.WarmIvory

@Composable
fun PrivateMemberPassCard(
    userId: String,
    userName: String,
    userEmail: String,
    isAdmin: Boolean,
    memberProfileState: UiState<MemberProfile?>,
    isEditorExpanded: Boolean,
    onToggleEditor: () -> Unit,
    onNavigateToBooking: () -> Unit,
    onNavigateToMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile = (memberProfileState as? UiState.Success)?.data
    val passCode = remember(userId) {
        val code = userId.trim().take(6).uppercase().ifBlank { "GUEST" }
        "GG-$code"
    }
    val displayTitle = profile?.displayName?.ifBlank { userName } ?: userName.ifBlank { "Club Visitor" }
    val tierLabel = profile?.membershipTier?.ifBlank { null }
        ?: if (isAdmin) "Committee Manager" else "Member Portal"
    val preferredSportLabel = profile?.preferredSport?.ifBlank { null } ?: "Select in Profile"

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MidnightGreen,
        border = BorderStroke(1.5.dp, AntiqueGold.copy(alpha = 0.75f)),
        modifier = modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth()
            .testTag("portal_digital_pass_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Badge,
                        contentDescription = "Digital Member Pass",
                        tint = AntiqueGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Column {
                        Text(
                            text = "PRIVATE MEMBER PORTAL PASS",
                            style = MaterialTheme.typography.labelSmall,
                            color = AntiqueGold,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmIvory
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = StatusApproved.copy(alpha = 0.22f),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.75f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VerifiedUser,
                            contentDescription = "Verified member session",
                            tint = AntiqueGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = passCode,
                            style = MaterialTheme.typography.labelSmall,
                            color = WarmIvory,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PassDetailBadge(
                    label = "Portal Tier",
                    value = tierLabel,
                    modifier = Modifier.weight(1f)
                )
                PassDetailBadge(
                    label = "Preferred Sport",
                    value = preferredSportLabel,
                    modifier = Modifier.weight(1f)
                )
            }

            if (profile != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Profile synced: ${formatClubTimestamp(profile.updatedAt)} · $userEmail",
                    style = MaterialTheme.typography.labelSmall,
                    color = WarmIvory.copy(alpha = 0.8f)
                )
            } else if (memberProfileState is UiState.Error) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Profile notice: ${memberProfileState.message}",
                    style = MaterialTheme.typography.labelSmall,
                    color = AntiqueGold
                )
            } else {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Complete your personal profile below to sync phone, sport preferences & emergency contact.",
                    style = MaterialTheme.typography.bodySmall,
                    color = WarmIvory.copy(alpha = 0.82f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onToggleEditor,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AntiqueGold,
                        contentColor = MidnightGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("portal_edit_profile_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEditorExpanded) "Hide Profile Form" else if (profile == null) "Set Up Profile" else "Edit Profile",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onNavigateToBooking,
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmIvory),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("portal_quick_book_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = "Book facility",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Book")
                }

                OutlinedButton(
                    onClick = onNavigateToMap,
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmIvory),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("portal_quick_map_btn")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = "Open Ngara Road club map",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Map")
                }
            }
        }
    }
}

@Composable
private fun PassDetailBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = WarmIvory.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AntiqueGold.copy(alpha = 0.9f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = WarmIvory,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MemberProfileEditorCard(
    userName: String,
    userEmail: String,
    profile: MemberProfile?,
    isSubmitting: Boolean,
    onSaveMemberProfile: (
        displayName: String,
        phone: String?,
        preferredSport: String,
        emergencyContact: String?,
        dietaryNotes: String?,
        onSuccess: () -> Unit
    ) -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier
) {
    var displayName by remember(profile?.userId, userName) {
        mutableStateOf(profile?.displayName?.ifBlank { userName } ?: userName)
    }
    var phone by remember(profile?.userId) {
        mutableStateOf(profile?.phone ?: "")
    }
    var preferredSport by remember(profile?.userId) {
        mutableStateOf(profile?.preferredSport?.ifBlank { ClubCatalog.sportingInterests.first() } ?: ClubCatalog.sportingInterests.first())
    }
    var emergencyContact by remember(profile?.userId) {
        mutableStateOf(profile?.emergencyContact ?: "")
    }
    var dietaryNotes by remember(profile?.userId) {
        mutableStateOf(profile?.dietaryNotes ?: "")
    }
    var validationError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profile) {
        if (profile != null) {
            displayName = profile.displayName.ifBlank { userName }
            phone = profile.phone.orEmpty()
            preferredSport = profile.preferredSport.ifBlank { ClubCatalog.sportingInterests.first() }
            emergencyContact = profile.emergencyContact.orEmpty()
            dietaryNotes = profile.dietaryNotes.orEmpty()
        }
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.2.dp, HeritageGreen.copy(alpha = 0.45f)),
        modifier = modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth()
            .testTag("portal_profile_editor_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Personal Member Profile",
                style = MaterialTheme.typography.titleMedium,
                color = HeritageGreen,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Signed in as $userEmail. Updates sync privately to your Firestore profile document.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = displayName,
                onValueChange = {
                    displayName = it
                    validationError = null
                },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portal_profile_name_input")
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Mobile Number (Optional)") },
                placeholder = { Text("+254 7XX XXX XXX") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portal_profile_phone_input")
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Preferred Sporting Section",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                ClubCatalog.sportingInterests.forEach { sport ->
                    val tagSuffix = sport.lowercase()
                        .replace("&", "and")
                        .replace(Regex("[^a-z0-9]+"), "_")
                        .trim('_')
                    FilterChip(
                        selected = preferredSport == sport,
                        onClick = { preferredSport = sport },
                        label = { Text(sport) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HeritageGreen,
                            selectedLabelColor = WarmIvory
                        ),
                        modifier = Modifier.testTag("portal_sport_chip_$tagSuffix")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = emergencyContact,
                onValueChange = { emergencyContact = it },
                label = { Text("Emergency Contact (Optional)") },
                placeholder = { Text("Name & telephone number") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portal_profile_emergency_input")
            )

            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = dietaryNotes,
                onValueChange = { dietaryNotes = it },
                label = { Text("Hospitality / Dietary Notes (Optional)") },
                placeholder = { Text("e.g., Vegetarian dining preferences") },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portal_profile_dietary_input")
            )

            if (validationError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = validationError!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val trimmedName = displayName.trim()
                        if (trimmedName.length < 2) {
                            validationError = "Please enter at least 2 characters for your display name."
                            return@Button
                        }
                        validationError = null
                        onSaveMemberProfile(
                            trimmedName,
                            phone.trim().ifBlank { null },
                            preferredSport,
                            emergencyContact.trim().ifBlank { null },
                            dietaryNotes.trim().ifBlank { null }
                        ) {
                            onCollapse()
                        }
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HeritageGreen,
                        contentColor = WarmIvory
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("portal_save_profile_btn")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = WarmIvory,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saving…")
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Profile to Cloud")
                    }
                }
                OutlinedButton(
                    onClick = onCollapse,
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun RestrictedMemberPrivilegeCard(
    privilege: MemberPrivilegeNotice,
    modifier: Modifier = Modifier
) {
    var expanded by remember(privilege.id) { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.55f)),
        modifier = modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .testTag("privilege_card_${privilege.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = HeritageGreen.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, HeritageGreen.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LockOpen,
                            contentDescription = "Unlocked member content",
                            tint = HeritageGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "MEMBERS ONLY · ${privilege.badge}",
                            style = MaterialTheme.typography.labelSmall,
                            color = HeritageGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Collapse privilege details" else "Expand privilege details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = privilege.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = privilege.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AntiqueGold.copy(alpha = 0.14f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = HeritageGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = privilege.guidance,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
