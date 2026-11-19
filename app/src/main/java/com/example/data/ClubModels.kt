package com.example.data

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class FeedbackEvent(
    val id: Long,
    val message: String,
    val isError: Boolean = false
)

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

enum class BookingStatus(val wireValue: String, val label: String) {
    PENDING("pending", "Pending Review"),
    APPROVED("approved", "Approved"),
    REJECTED("rejected", "Declined"),
    CANCELLED("cancelled", "Cancelled");

    companion object {
        fun fromWire(raw: String): BookingStatus =
            entries.firstOrNull { it.wireValue == raw } ?: PENDING
    }
}

enum class MembershipStatus(val wireValue: String, val label: String) {
    PENDING("pending", "Submitted"),
    UNDER_REVIEW("under_review", "Under Review"),
    APPROVED("approved", "Approved"),
    DECLINED("declined", "Declined"),
    WITHDRAWN("withdrawn", "Withdrawn");

    companion object {
        fun fromWire(raw: String): MembershipStatus =
            entries.firstOrNull { it.wireValue == raw } ?: PENDING
    }
}

enum class EnquiryStatus(val wireValue: String, val label: String) {
    SUBMITTED("submitted", "Submitted"),
    RESPONDED("responded", "Responded"),
    CLOSED("closed", "Closed");

    companion object {
        fun fromWire(raw: String): EnquiryStatus =
            entries.firstOrNull { it.wireValue == raw } ?: SUBMITTED
    }
}

enum class MembershipInquiryStatus(val wireValue: String, val label: String) {
    SUBMITTED("submitted", "Submitted"),
    REVIEWED("reviewed", "Reviewed"),
    CLOSED("closed", "Closed");

    companion object {
        fun fromWire(raw: String): MembershipInquiryStatus =
            entries.firstOrNull { it.wireValue == raw } ?: SUBMITTED
    }
}

@Keep
@IgnoreExtraProperties
data class FacilityBooking(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val facilityId: String = "",
    val facilityName: String = "",
    val preferredDate: String = "",
    val timeSlot: String = "",
    val participants: Long = 1L,
    val notes: String? = null,
    val status: String = BookingStatus.PENDING.wireValue,
    val managerNote: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class MembershipApplication(
    @DocumentId val id: String = "",
    val userId: String = "",
    val fullName: String = "",
    val phone: String = "",
    val email: String = "",
    val category: String = "Individual",
    val interests: List<String> = emptyList(),
    val notes: String? = null,
    val status: String = MembershipStatus.PENDING.wireValue,
    val reviewNote: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class ClubEnquiry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val senderName: String = "",
    val phone: String = "",
    val enquiryType: String = "General",
    val preferredDate: String? = null,
    val guestCount: Long? = null,
    val message: String = "",
    val status: String = EnquiryStatus.SUBMITTED.wireValue,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class MembershipInquiry(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val interest: String = "",
    val status: String = MembershipInquiryStatus.SUBMITTED.wireValue,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class MemberProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val phone: String? = null,
    val preferredSport: String = "Swimming",
    val membershipTier: String = "Member Portal",
    val emergencyContact: String? = null,
    val dietaryNotes: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

fun buildBookingCreatePayload(
    userId: String,
    userName: String,
    userEmail: String,
    facilityId: String,
    facilityName: String,
    preferredDate: String,
    timeSlot: String,
    participants: Int,
    notes: String?
): Map<String, Any> {
    val cleanNotes = notes?.trim()?.takeIf { it.isNotEmpty() }
    return buildMap {
        put("userId", userId)
        put("userName", userName.trim().take(80))
        put("userEmail", userEmail.trim().take(120))
        put("facilityId", facilityId)
        put("facilityName", facilityName.trim().take(80))
        put("preferredDate", preferredDate.trim().take(30))
        put("timeSlot", timeSlot.trim().take(40))
        put("participants", participants.coerceIn(1, 60).toLong())
        if (cleanNotes != null) put("notes", cleanNotes.take(400))
        put("status", BookingStatus.PENDING.wireValue)
        put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }
}

fun buildMembershipCreatePayload(
    userId: String,
    fullName: String,
    phone: String,
    email: String,
    category: String,
    interests: List<String>,
    notes: String?
): Map<String, Any> {
    val cleanNotes = notes?.trim()?.takeIf { it.isNotEmpty() }
    val boundedInterests = interests.map { it.trim().take(40) }.filter { it.isNotEmpty() }.take(8)
    return buildMap {
        put("userId", userId)
        put("fullName", fullName.trim().take(100))
        put("phone", phone.trim().take(30))
        put("email", email.trim().take(120))
        put("category", category)
        put("interests", boundedInterests)
        if (cleanNotes != null) put("notes", cleanNotes.take(500))
        put("status", MembershipStatus.PENDING.wireValue)
        put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }
}

fun buildEnquiryCreatePayload(
    userId: String,
    senderName: String,
    phone: String,
    enquiryType: String,
    preferredDate: String?,
    guestCount: Int?,
    message: String
): Map<String, Any> {
    val cleanDate = preferredDate?.trim()?.takeIf { it.isNotEmpty() }
    return buildMap {
        put("userId", userId)
        put("senderName", senderName.trim().take(80))
        put("phone", phone.trim().take(30))
        put("enquiryType", enquiryType)
        if (cleanDate != null) put("preferredDate", cleanDate.take(30))
        if (guestCount != null && guestCount in 1..300) put("guestCount", guestCount.toLong())
        put("message", message.trim().take(600))
        put("status", EnquiryStatus.SUBMITTED.wireValue)
        put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }
}

fun buildMembershipInquiryCreatePayload(
    userId: String,
    name: String,
    email: String,
    interest: String
): Map<String, Any> {
    return buildMap {
        put("userId", userId)
        put("name", name.trim().take(100))
        put("email", email.trim().take(120))
        put("interest", interest.trim().take(120))
        put("status", MembershipInquiryStatus.SUBMITTED.wireValue)
        put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }
}

fun buildMemberProfileCreatePayload(
    userId: String,
    displayName: String,
    email: String,
    phone: String?,
    preferredSport: String,
    emergencyContact: String?,
    dietaryNotes: String?
): Map<String, Any> {
    val cleanPhone = phone?.trim()?.takeIf { it.isNotEmpty() }
    val cleanEmergency = emergencyContact?.trim()?.takeIf { it.isNotEmpty() }
    val cleanDietary = dietaryNotes?.trim()?.takeIf { it.isNotEmpty() }
    return buildMap {
        put("userId", userId)
        put("displayName", displayName.trim().take(80))
        put("email", email.trim().take(120))
        if (cleanPhone != null) put("phone", cleanPhone.take(30))
        put("preferredSport", preferredSport.trim().take(40))
        put("membershipTier", "Member Portal")
        if (cleanEmergency != null) put("emergencyContact", cleanEmergency.take(80))
        if (cleanDietary != null) put("dietaryNotes", cleanDietary.take(200))
        put("createdAt", FieldValue.serverTimestamp())
        put("updatedAt", FieldValue.serverTimestamp())
    }
}

fun buildMemberProfileUpdatePayload(
    displayName: String,
    phone: String?,
    preferredSport: String,
    emergencyContact: String?,
    dietaryNotes: String?
): Map<String, Any> {
    return buildMap {
        put("displayName", displayName.trim().take(80))
        put("phone", (phone ?: "").trim().take(30))
        put("preferredSport", preferredSport.trim().take(40))
        put("emergencyContact", (emergencyContact ?: "").trim().take(80))
        put("dietaryNotes", (dietaryNotes ?: "").trim().take(200))
        put("updatedAt", FieldValue.serverTimestamp())
    }
}

fun formatClubTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return "Syncing…"
    val formatter = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.getDefault())
    return formatter.format(timestamp.toDate())
}
