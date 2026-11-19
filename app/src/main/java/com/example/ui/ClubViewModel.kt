package com.example.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BookingStatus
import com.example.data.ClubEnquiry
import com.example.data.ClubFacility
import com.example.data.ClubRepository
import com.example.data.EnquiryStatus
import com.example.data.FacilityBooking
import com.example.data.FeedbackEvent
import com.example.data.MemberProfile
import com.example.data.MembershipApplication
import com.example.data.MembershipInquiry
import com.example.data.MembershipInquiryStatus
import com.example.data.MembershipStatus
import com.example.data.UiState
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ClubViewModel(
    private val repository: ClubRepository,
    private val currentUserId: String,
    val userName: String,
    val userEmail: String,
    private val emailVerified: Boolean
) : ViewModel() {

    val currentMemberId: String
        get() = currentUserId

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _feedbackEvent = MutableStateFlow<FeedbackEvent?>(null)
    val feedbackEvent: StateFlow<FeedbackEvent?> = _feedbackEvent.asStateFlow()

    private var nextFeedbackId = 1L

    private fun emitFeedback(message: String, isError: Boolean) {
        _feedbackEvent.value = FeedbackEvent(
            id = nextFeedbackId++,
            message = message,
            isError = isError
        )
    }

    fun showInfoFeedback(message: String, isError: Boolean = false) {
        emitFeedback(message, isError)
    }

    init {
        viewModelScope.launch {
            _isAdmin.value = repository.checkIsAdmin(currentUserId, userEmail, emailVerified)
        }
    }

    val bookingsState: StateFlow<UiState<List<FacilityBooking>>> =
        repository.observeUserBookings(currentUserId)
            .toUiStateFlow("bookings")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val membershipsState: StateFlow<UiState<List<MembershipApplication>>> =
        repository.observeUserMemberships(currentUserId)
            .toUiStateFlow("membership applications")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val enquiriesState: StateFlow<UiState<List<ClubEnquiry>>> =
        repository.observeUserEnquiries(currentUserId)
            .toUiStateFlow("club enquiries")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val membershipInquiriesState: StateFlow<UiState<List<MembershipInquiry>>> =
        repository.observeUserMembershipInquiries(currentUserId)
            .toUiStateFlow("membership inquiries")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val memberProfileState: StateFlow<UiState<MemberProfile?>> =
        repository.observeMemberProfile(currentUserId)
            .toUiStateFlow("your member profile")
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

    val adminBookingsState: StateFlow<UiState<List<FacilityBooking>>> =
        _isAdmin.flatMapLatest { admin ->
            if (!admin) flowOf(UiState.Success(emptyList()))
            else repository.observeAllBookingsForAdmin().toUiStateFlow("all bookings")
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val adminMembershipsState: StateFlow<UiState<List<MembershipApplication>>> =
        _isAdmin.flatMapLatest { admin ->
            if (!admin) flowOf(UiState.Success(emptyList()))
            else repository.observeAllMembershipsForAdmin().toUiStateFlow("all memberships")
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val adminEnquiriesState: StateFlow<UiState<List<ClubEnquiry>>> =
        _isAdmin.flatMapLatest { admin ->
            if (!admin) flowOf(UiState.Success(emptyList()))
            else repository.observeAllEnquiriesForAdmin().toUiStateFlow("all enquiries")
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val adminMembershipInquiriesState: StateFlow<UiState<List<MembershipInquiry>>> =
        _isAdmin.flatMapLatest { admin ->
            if (!admin) flowOf(UiState.Success(emptyList()))
            else repository.observeAllMembershipInquiriesForAdmin().toUiStateFlow("all membership inquiries")
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    private fun <T> Flow<T>.toUiStateFlow(label: String): Flow<UiState<T>> =
        map<T, UiState<T>> { UiState.Success(it) }
            .catch { error ->
                val firestoreError = generateSequence(error) { it.cause }
                    .filterIsInstance<FirebaseFirestoreException>()
                    .firstOrNull()
                if (firestoreError == null && error is CancellationException) throw error
                val rootError = firestoreError ?: error
                Log.w(TAG, "Error observing $label", rootError)
                emit(UiState.Error(rootError.localizedMessage ?: "Failed to load $label"))
            }

    fun clearFeedback() {
        _feedbackEvent.value = null
    }

    fun submitBooking(
        facility: ClubFacility,
        preferredDate: String,
        timeSlot: String,
        participants: Int,
        notes: String?,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.createFacilityBooking(
                facility = facility,
                userName = userName,
                userEmail = userEmail,
                preferredDate = preferredDate,
                timeSlot = timeSlot,
                participants = participants,
                notes = notes
            )
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Booking request saved for ${facility.name}. Track status in Portal.", false)
                onSuccess()
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Unable to submit booking request.", true)
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.cancelFacilityBooking(bookingId)
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Booking request cancelled.", false)
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Unable to cancel booking.", true)
            }
        }
    }

    fun submitMembership(
        fullName: String,
        phone: String,
        email: String,
        category: String,
        interests: List<String>,
        notes: String?,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.submitMembershipApplication(
                fullName = fullName,
                phone = phone,
                email = email,
                category = category,
                interests = interests,
                notes = notes
            )
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Membership application saved for committee review.", false)
                onSuccess()
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Unable to submit membership application.", true)
            }
        }
    }

    fun withdrawMembership(applicationId: String) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.withdrawMembershipApplication(applicationId)
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Membership application withdrawn.", false)
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Unable to withdraw application.", true)
            }
        }
    }

    fun submitEnquiry(
        senderName: String,
        phone: String,
        enquiryType: String,
        preferredDate: String?,
        guestCount: Int?,
        message: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.submitClubEnquiry(
                senderName = senderName,
                phone = phone,
                enquiryType = enquiryType,
                preferredDate = preferredDate,
                guestCount = guestCount,
                message = message
            )
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Enquiry saved & sent to The Goan Gymkhana desk.", false)
                onSuccess()
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Unable to send enquiry.", true)
            }
        }
    }

    fun closeEnquiry(enquiryId: String) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.closeClubEnquiry(enquiryId)
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Enquiry closed.", false)
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Unable to close enquiry.", true)
            }
        }
    }

    fun submitMembershipInquiry(
        name: String,
        email: String,
        interest: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.submitMembershipInquiry(
                name = name,
                email = email,
                interest = interest
            )
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Membership inquiry saved to The Goan Gymkhana database.", false)
                onSuccess()
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Unable to submit membership inquiry.", true)
            }
        }
    }

    fun closeMembershipInquiry(inquiryId: String) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.closeMembershipInquiry(inquiryId)
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Membership inquiry closed.", false)
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Unable to close inquiry.", true)
            }
        }
    }

    fun saveMemberProfile(
        displayName: String,
        phone: String?,
        preferredSport: String,
        emergencyContact: String?,
        dietaryNotes: String?,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.saveMemberProfile(
                displayName = displayName,
                email = userEmail,
                phone = phone,
                preferredSport = preferredSport,
                emergencyContact = emergencyContact,
                dietaryNotes = dietaryNotes
            )
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Personal member profile saved to Firestore.", false)
                onSuccess()
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Unable to save member profile.", true)
            }
        }
    }

    fun reviewBookingAsAdmin(bookingId: String, status: BookingStatus, managerNote: String?) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.adminReviewBooking(bookingId, status, managerNote)
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Booking updated to ${status.label}.", false)
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Admin update failed.", true)
            }
        }
    }

    fun reviewMembershipAsAdmin(
        applicationId: String,
        status: MembershipStatus,
        reviewNote: String?
    ) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.adminReviewMembership(applicationId, status, reviewNote)
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Application marked ${status.label}.", false)
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Admin review failed.", true)
            }
        }
    }

    fun updateEnquiryAsAdmin(enquiryId: String, status: EnquiryStatus) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.adminUpdateEnquiryStatus(enquiryId, status)
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Enquiry marked ${status.label}.", false)
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Admin update failed.", true)
            }
        }
    }

    fun updateMembershipInquiryAsAdmin(inquiryId: String, status: MembershipInquiryStatus) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val result = repository.adminUpdateMembershipInquiryStatus(inquiryId, status)
            _isSubmitting.value = false
            result.onSuccess {
                emitFeedback("Membership inquiry marked ${status.label}.", false)
            }.onFailure { err ->
                emitFeedback(err.localizedMessage ?: "Admin update failed.", true)
            }
        }
    }

    private companion object {
        const val TAG = "ClubViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
