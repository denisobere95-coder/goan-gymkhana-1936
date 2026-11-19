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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.LockOpen
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.BookingStatus
import com.example.data.ClubCatalog
import com.example.data.ClubEnquiry
import com.example.data.EnquiryStatus
import com.example.data.FacilityBooking
import com.example.data.MemberProfile
import com.example.data.MembershipApplication
import com.example.data.MembershipInquiry
import com.example.data.MembershipInquiryStatus
import com.example.data.MembershipStatus
import com.example.data.UiState
import com.example.data.formatClubTimestamp
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.MidnightGreen
import com.example.ui.theme.StatusApproved
import com.example.ui.theme.StatusRejected
import com.example.ui.theme.WarmIvory

private enum class PortalSectionFilter(val label: String) {
    ALL("All Activity"),
    PROFILE("Profile & Pass"),
    BOOKINGS("Bookings"),
    MEMBERSHIP("Membership"),
    EVENTS("Events")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PortalScreen(
    userName: String,
    userEmail: String,
    isAdmin: Boolean,
    bookingsState: UiState<List<FacilityBooking>>,
    adminBookingsState: UiState<List<FacilityBooking>>,
    adminMembershipsState: UiState<List<MembershipApplication>>,
    adminEnquiriesState: UiState<List<ClubEnquiry>>,
    onCancelBooking: (String) -> Unit,
    onAdminReviewBooking: (String, BookingStatus, String?) -> Unit,
    onAdminReviewMembership: (String, MembershipStatus, String?) -> Unit,
    onAdminUpdateEnquiry: (String, EnquiryStatus) -> Unit,
    onSignOut: () -> Unit,
    membershipsState: UiState<List<MembershipApplication>> = UiState.Success(emptyList()),
    membershipInquiriesState: UiState<List<MembershipInquiry>> = UiState.Success(emptyList()),
    enquiriesState: UiState<List<ClubEnquiry>> = UiState.Success(emptyList()),
    adminMembershipInquiriesState: UiState<List<MembershipInquiry>> = UiState.Success(emptyList()),
    onWithdrawMembership: (String) -> Unit = {},
    onCloseMembershipInquiry: (String) -> Unit = {},
    onCloseEnquiry: (String) -> Unit = {},
    onAdminUpdateMembershipInquiry: (String, MembershipInquiryStatus) -> Unit = { _, _ -> },
    userId: String = "",
    memberProfileState: UiState<MemberProfile?> = UiState.Success(null),
    isSubmitting: Boolean = false,
    onSaveMemberProfile: (
        displayName: String,
        phone: String?,
        preferredSport: String,
        emergencyContact: String?,
        dietaryNotes: String?,
        onSuccess: () -> Unit
    ) -> Unit = { _, _, _, _, _, _ -> },
    onNavigateToBooking: () -> Unit = {},
    onNavigateToMap: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(PortalSectionFilter.ALL) }
    var showProfileEditor by remember { mutableStateOf(false) }

    val activeProfile = (memberProfileState as? UiState.Success)?.data
    val bookingCount = (bookingsState as? UiState.Success)?.data?.size ?: 0
    val membershipCount = (membershipsState as? UiState.Success)?.data?.size ?: 0
    val inquiryCount = (membershipInquiriesState as? UiState.Success)?.data?.size ?: 0
    val eventCount = (enquiriesState as? UiState.Success)?.data?.size ?: 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("portal_lazy_column"),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile & Live Cloud Sync Summary Header
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = HeritageGreen,
                border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = AntiqueGold,
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    text = userName.ifBlank { "Club Visitor" },
                                    style = MaterialTheme.typography.titleLarge,
                                    color = WarmIvory
                                )
                                Text(
                                    text = userEmail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WarmIvory.copy(alpha = 0.8f)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isAdmin) AntiqueGold else AntiqueGold.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.8f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CloudDone,
                                    contentDescription = null,
                                    tint = if (isAdmin) MidnightGreen else AntiqueGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = if (isAdmin) "MANAGER" else "SYNCED",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isAdmin) MidnightGreen else AntiqueGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PortalStatTile("Bookings", bookingCount, Modifier.weight(1f))
                        PortalStatTile("Inquiries", inquiryCount, Modifier.weight(1f))
                        PortalStatTile("Apps", membershipCount, Modifier.weight(1f))
                        PortalStatTile("Events", eventCount, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = onSignOut,
                        border = BorderStroke(1.dp, WarmIvory.copy(alpha = 0.65f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarmIvory),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("sign_out_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(R.string.sign_out), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }

        // Section Filter Chips
        item {
            FlowRow(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PortalSectionFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HeritageGreen,
                            selectedLabelColor = WarmIvory
                        ),
                        modifier = Modifier.testTag("portal_filter_${filter.name.lowercase()}")
                    )
                }
            }
        }

        // 0. Private Member Portal Pass, Personal Profile & Restricted Member Privileges
        if (selectedFilter == PortalSectionFilter.ALL || selectedFilter == PortalSectionFilter.PROFILE) {
            item {
                PrivateMemberPassCard(
                    userId = userId,
                    userName = userName,
                    userEmail = userEmail,
                    isAdmin = isAdmin,
                    memberProfileState = memberProfileState,
                    isEditorExpanded = showProfileEditor,
                    onToggleEditor = { showProfileEditor = !showProfileEditor },
                    onNavigateToBooking = onNavigateToBooking,
                    onNavigateToMap = onNavigateToMap
                )
            }

            if (showProfileEditor) {
                item {
                    MemberProfileEditorCard(
                        userName = userName,
                        userEmail = userEmail,
                        profile = activeProfile,
                        isSubmitting = isSubmitting,
                        onSaveMemberProfile = onSaveMemberProfile,
                        onCollapse = { showProfileEditor = false }
                    )
                }
            }

            item {
                PortalSectionHeading(
                    icon = Icons.Filled.LockOpen,
                    title = "Restricted Member Privileges"
                )
            }

            items(ClubCatalog.memberPrivileges, key = { "priv_${it.id}" }) { privilege ->
                RestrictedMemberPrivilegeCard(privilege = privilege)
            }
        }

        // 1. My Facility Bookings Section
        if (selectedFilter == PortalSectionFilter.ALL || selectedFilter == PortalSectionFilter.BOOKINGS) {
            item {
                PortalSectionHeading(
                    icon = Icons.AutoMirrored.Filled.EventNote,
                    title = "My Facility Bookings"
                )
            }

            when (bookingsState) {
                UiState.Loading -> item { CircularProgressIndicator() }
                is UiState.Error -> item { PortalErrorText(bookingsState.message) }
                is UiState.Success -> {
                    if (bookingsState.data.isEmpty()) {
                        item {
                            PortalEmptyNotice("No facility bookings requested yet. Visit Facilities to reserve the pool, AstroTurf pitch, badminton, or table tennis.")
                        }
                    } else {
                        items(bookingsState.data, key = { "b_${it.id}" }) { booking ->
                            val status = BookingStatus.fromWire(booking.status)
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
                                            text = "Club Desk Feedback: ${booking.managerNote}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Recorded ${formatClubTimestamp(booking.createdAt)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    if (status == BookingStatus.PENDING && booking.id.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { onCancelBooking(booking.id) },
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Icon(Icons.Filled.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.size(6.dp))
                                            Text("Cancel Request")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. My Membership Inquiries & Applications Section
        if (selectedFilter == PortalSectionFilter.ALL || selectedFilter == PortalSectionFilter.MEMBERSHIP) {
            item {
                PortalSectionHeading(
                    icon = Icons.Filled.MarkEmailRead,
                    title = "My Membership Inquiries"
                )
            }

            when (membershipInquiriesState) {
                UiState.Loading -> item { CircularProgressIndicator() }
                is UiState.Error -> item { PortalErrorText(membershipInquiriesState.message) }
                is UiState.Success -> {
                    if (membershipInquiriesState.data.isEmpty()) {
                        item {
                            PortalEmptyNotice("No quick membership inquiries yet. Use Quick Membership Inquiry under Membership to express interest.")
                        }
                    } else {
                        items(membershipInquiriesState.data, key = { "mi_${it.id}" }) { inq ->
                            val status = MembershipInquiryStatus.fromWire(inq.status)
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
                                            text = inq.interest,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        StatusPill(label = status.label, wireStatus = status.wireValue)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${inq.name} · ${inq.email}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Recorded ${formatClubTimestamp(inq.createdAt)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    if (status == MembershipInquiryStatus.SUBMITTED && inq.id.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { onCloseMembershipInquiry(inq.id) },
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
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

            item {
                PortalSectionHeading(
                    icon = Icons.Filled.Badge,
                    title = "My Membership Applications"
                )
            }

            when (membershipsState) {
                UiState.Loading -> item { CircularProgressIndicator() }
                is UiState.Error -> item { PortalErrorText(membershipsState.message) }
                is UiState.Success -> {
                    if (membershipsState.data.isEmpty()) {
                        item {
                            PortalEmptyNotice("No formal membership applications submitted yet.")
                        }
                    } else {
                        items(membershipsState.data, key = { "ma_${it.id}" }) { app ->
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
                                    if (!app.reviewNote.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Committee Feedback: ${app.reviewNote}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
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
                                            Spacer(modifier = Modifier.width(6.dp))
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

        // 3. My Events & Hospitality Enquiries Section
        if (selectedFilter == PortalSectionFilter.ALL || selectedFilter == PortalSectionFilter.EVENTS) {
            item {
                PortalSectionHeading(
                    icon = Icons.Filled.Celebration,
                    title = "My Events & Dining Enquiries"
                )
            }

            when (enquiriesState) {
                UiState.Loading -> item { CircularProgressIndicator() }
                is UiState.Error -> item { PortalErrorText(enquiriesState.message) }
                is UiState.Success -> {
                    if (enquiriesState.data.isEmpty()) {
                        item {
                            PortalEmptyNotice("No event spot reservations or dining enquiries submitted yet.")
                        }
                    } else {
                        items(enquiriesState.data, key = { "ce_${it.id}" }) { enq ->
                            val status = EnquiryStatus.fromWire(enq.status)
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
                                            text = enq.enquiryType,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        StatusPill(label = status.label, wireStatus = status.wireValue)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = enq.message,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Submitted ${formatClubTimestamp(enq.createdAt)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    if (status == EnquiryStatus.SUBMITTED && enq.id.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { onCloseEnquiry(enq.id) },
                                            modifier = Modifier.minimumInteractiveComponentSize()
                                        ) {
                                            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
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

        // Club Management Review Desk (Only rendered for verified club admin)
        if (isAdmin) {
            item {
                PortalSectionHeading(
                    icon = Icons.Filled.AdminPanelSettings,
                    title = "Management Review Desk",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Admin Bookings
            if (adminBookingsState is UiState.Success) {
                val pendingBookings = adminBookingsState.data.filter {
                    it.status == BookingStatus.PENDING.wireValue
                }
                items(pendingBookings, key = { "admin_b_${it.id}" }) { booking ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Booking: ${booking.facilityName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${booking.userName} (${booking.userEmail})\n${booking.preferredDate} · ${booking.timeSlot} · ${booking.participants} guests",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        onAdminReviewBooking(
                                            booking.id,
                                            BookingStatus.APPROVED,
                                            "Slot confirmed by Ngara Road desk."
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusApproved),
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text("Approve")
                                }
                                OutlinedButton(
                                    onClick = {
                                        onAdminReviewBooking(
                                            booking.id,
                                            BookingStatus.REJECTED,
                                            "Requested slot unavailable; please pick another window."
                                        )
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusRejected),
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Text("Decline")
                                }
                            }
                        }
                    }
                }
            }

            // Admin Quick Membership Inquiries (membership_inquiries)
            if (adminMembershipInquiriesState is UiState.Success) {
                val openInquiries = adminMembershipInquiriesState.data.filter {
                    it.status == MembershipInquiryStatus.SUBMITTED.wireValue
                }
                items(openInquiries, key = { "admin_mi_${it.id}" }) { inq ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxWidth()
                            .testTag("admin_membership_inquiry_${inq.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Quick Inquiry: ${inq.interest}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${inq.name} · ${inq.email}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        onAdminUpdateMembershipInquiry(inq.id, MembershipInquiryStatus.REVIEWED)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusApproved),
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Text("Mark Reviewed")
                                }
                                OutlinedButton(
                                    onClick = {
                                        onAdminUpdateMembershipInquiry(inq.id, MembershipInquiryStatus.CLOSED)
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Text("Close")
                                }
                            }
                        }
                    }
                }
            }

            // Admin Membership Applications
            if (adminMembershipsState is UiState.Success) {
                val openApps = adminMembershipsState.data.filter {
                    it.status == MembershipStatus.PENDING.wireValue ||
                        it.status == MembershipStatus.UNDER_REVIEW.wireValue
                }
                items(openApps, key = { "admin_m_${it.id}" }) { app ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Membership (${app.category}): ${app.fullName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${app.phone} · ${app.email}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        onAdminReviewMembership(
                                            app.id,
                                            MembershipStatus.APPROVED,
                                            "Committee review approved; welcome to Goan Gymkhana."
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusApproved),
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Text("Approve")
                                }
                                OutlinedButton(
                                    onClick = {
                                        onAdminReviewMembership(
                                            app.id,
                                            MembershipStatus.UNDER_REVIEW,
                                            "Application queued for committee review."
                                        )
                                    },
                                    modifier = Modifier.minimumInteractiveComponentSize()
                                ) {
                                    Text("Review")
                                }
                            }
                        }
                    }
                }
            }

            // Admin Club & Event Enquiries
            if (adminEnquiriesState is UiState.Success) {
                val openEnquiries = adminEnquiriesState.data.filter {
                    it.status == EnquiryStatus.SUBMITTED.wireValue
                }
                items(openEnquiries, key = { "admin_e_${it.id}" }) { enq ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.65f)),
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Enquiry (${enq.enquiryType}): ${enq.senderName}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${enq.phone}\n${enq.message}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onAdminUpdateEnquiry(enq.id, EnquiryStatus.RESPONDED)
                                },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Text("Mark Responded")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PortalStatTile(
    label: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = WarmIvory.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = AntiqueGold,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = WarmIvory.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun PortalSectionHeading(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth()
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun PortalEmptyNotice(text: String) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun PortalErrorText(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier
            .widthIn(max = 720.dp)
            .fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun PortalScreenPreview() {
    GoanGymkhanaTheme {
        PortalScreen(
            userName = "Club Member",
            userEmail = "member@goangymkhana.com",
            isAdmin = false,
            bookingsState = UiState.Success(
                listOf(
                    FacilityBooking(
                        id = "booking_1",
                        facilityId = "badminton",
                        facilityName = "Badminton Courts",
                        preferredDate = "2026-11-22",
                        timeSlot = "18:00 – 20:00",
                        participants = 4L,
                        status = BookingStatus.PENDING.wireValue
                    )
                )
            ),
            adminBookingsState = UiState.Success(emptyList()),
            adminMembershipsState = UiState.Success(emptyList()),
            adminEnquiriesState = UiState.Success(emptyList()),
            onCancelBooking = {},
            onAdminReviewBooking = { _, _, _ -> },
            onAdminReviewMembership = { _, _, _ -> },
            onAdminUpdateEnquiry = { _, _ -> },
            onSignOut = {}
        )
    }
}
