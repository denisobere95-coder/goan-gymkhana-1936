package com.example.data

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class ClubRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Please sign in with Google to continue.")
    }

    suspend fun checkIsAdmin(uid: String, email: String?, emailVerified: Boolean): Boolean {
        if (emailVerified && email == ClubCatalog.ADMIN_BOOTSTRAP_EMAIL) {
            return true
        }
        return try {
            val doc = db.collection("admins").document(uid).get().await()
            doc.exists()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            false
        }
    }

    fun observeUserBookings(userId: String): Flow<List<FacilityBooking>> =
        db.collection("facilityBookings")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    FacilityBooking::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }

    fun observeUserMemberships(userId: String): Flow<List<MembershipApplication>> =
        db.collection("membershipApplications")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    MembershipApplication::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }

    fun observeUserEnquiries(userId: String): Flow<List<ClubEnquiry>> =
        db.collection("clubEnquiries")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    ClubEnquiry::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }

    fun observeUserMembershipInquiries(userId: String): Flow<List<MembershipInquiry>> =
        db.collection("membership_inquiries")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    MembershipInquiry::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }

    fun observeMemberProfile(userId: String): Flow<MemberProfile?> =
        db.collection("memberProfiles")
            .document(userId)
            .snapshots()
            .map { snapshot ->
                if (snapshot.exists()) {
                    snapshot.toObject(
                        MemberProfile::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                } else {
                    null
                }
            }

    fun observeAllBookingsForAdmin(): Flow<List<FacilityBooking>> =
        db.collection("facilityBookings")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    FacilityBooking::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }

    fun observeAllMembershipsForAdmin(): Flow<List<MembershipApplication>> =
        db.collection("membershipApplications")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    MembershipApplication::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }

    fun observeAllEnquiriesForAdmin(): Flow<List<ClubEnquiry>> =
        db.collection("clubEnquiries")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    ClubEnquiry::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }

    fun observeAllMembershipInquiriesForAdmin(): Flow<List<MembershipInquiry>> =
        db.collection("membership_inquiries")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    MembershipInquiry::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                ).sortedByDescending { it.createdAt ?: Timestamp(0, 0) }
            }

    suspend fun createFacilityBooking(
        facility: ClubFacility,
        userName: String,
        userEmail: String,
        preferredDate: String,
        timeSlot: String,
        participants: Int,
        notes: String?
    ): Result<Unit> {
        var docPath = "facilityBookings"
        return try {
            val uid = requireUserId()
            require(preferredDate.trim().length in 4..30) { "Please enter a valid preferred date." }
            require(timeSlot.trim().length in 4..40) { "Please select a time slot." }
            require(participants in 1..60) { "Participants must be between 1 and 60." }
            val safeName = userName.trim().ifBlank { "Club Member" }.take(80)
            val safeEmail = userEmail.trim().ifBlank { "member@goangymkhana.com" }.take(120)

            val docRef = db.collection("facilityBookings").document()
            docPath = docRef.path
            val payload = buildBookingCreatePayload(
                userId = uid,
                userName = safeName,
                userEmail = safeEmail,
                facilityId = facility.id,
                facilityName = facility.name,
                preferredDate = preferredDate,
                timeSlot = timeSlot,
                participants = participants,
                notes = notes
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun cancelFacilityBooking(bookingId: String): Result<Unit> {
        val docPath = "facilityBookings/$bookingId"
        return try {
            requireUserId()
            require(bookingId.isNotBlank()) { "Booking ID is required." }
            db.collection("facilityBookings").document(bookingId)
                .update(
                    mapOf(
                        "status" to BookingStatus.CANCELLED.wireValue,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitMembershipApplication(
        fullName: String,
        phone: String,
        email: String,
        category: String,
        interests: List<String>,
        notes: String?
    ): Result<Unit> {
        var docPath = "membershipApplications"
        return try {
            val uid = requireUserId()
            require(fullName.trim().length in 2..100) { "Full name must be between 2 and 100 characters." }
            require(phone.trim().length in 6..30) { "Please provide a valid phone number (6–30 chars)." }
            require(email.trim().length in 3..120) { "Please provide a valid email address." }
            require(category in ClubCatalog.membershipCategories) { "Please select a valid category." }

            val docRef = db.collection("membershipApplications").document()
            docPath = docRef.path
            val payload = buildMembershipCreatePayload(
                userId = uid,
                fullName = fullName,
                phone = phone,
                email = email,
                category = category,
                interests = interests,
                notes = notes
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun withdrawMembershipApplication(applicationId: String): Result<Unit> {
        val docPath = "membershipApplications/$applicationId"
        return try {
            requireUserId()
            require(applicationId.isNotBlank()) { "Application ID is required." }
            db.collection("membershipApplications").document(applicationId)
                .update(
                    mapOf(
                        "status" to MembershipStatus.WITHDRAWN.wireValue,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitClubEnquiry(
        senderName: String,
        phone: String,
        enquiryType: String,
        preferredDate: String?,
        guestCount: Int?,
        message: String
    ): Result<Unit> {
        var docPath = "clubEnquiries"
        return try {
            val uid = requireUserId()
            require(senderName.trim().length in 2..80) { "Name must be between 2 and 80 characters." }
            require(phone.trim().length in 6..30) { "Please enter a valid contact number." }
            require(enquiryType in ClubCatalog.enquiryTypes) { "Select a valid enquiry category." }
            require(message.trim().length in 5..600) { "Message must be at least 5 characters." }
            if (guestCount != null) {
                require(guestCount in 1..300) { "Guest count must be between 1 and 300." }
            }

            val docRef = db.collection("clubEnquiries").document()
            docPath = docRef.path
            val payload = buildEnquiryCreatePayload(
                userId = uid,
                senderName = senderName,
                phone = phone,
                enquiryType = enquiryType,
                preferredDate = preferredDate,
                guestCount = guestCount,
                message = message
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun closeClubEnquiry(enquiryId: String): Result<Unit> {
        val docPath = "clubEnquiries/$enquiryId"
        return try {
            requireUserId()
            require(enquiryId.isNotBlank()) { "Enquiry ID is required." }
            db.collection("clubEnquiries").document(enquiryId)
                .update(
                    mapOf(
                        "status" to EnquiryStatus.CLOSED.wireValue,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun submitMembershipInquiry(
        name: String,
        email: String,
        interest: String
    ): Result<Unit> {
        var docPath = "membership_inquiries"
        return try {
            val uid = requireUserId()
            require(name.trim().length in 2..100) { "Name must be between 2 and 100 characters." }
            require(email.trim().length in 3..120 && email.contains("@")) {
                "Please enter a valid email address."
            }
            require(interest.trim().length in 2..120) {
                "Please specify your membership or sporting interest."
            }

            val docRef = db.collection("membership_inquiries").document()
            docPath = docRef.path
            val payload = buildMembershipInquiryCreatePayload(
                userId = uid,
                name = name,
                email = email,
                interest = interest
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun closeMembershipInquiry(inquiryId: String): Result<Unit> {
        val docPath = "membership_inquiries/$inquiryId"
        return try {
            requireUserId()
            require(inquiryId.isNotBlank()) { "Inquiry ID is required." }
            db.collection("membership_inquiries").document(inquiryId)
                .update(
                    mapOf(
                        "status" to MembershipInquiryStatus.CLOSED.wireValue,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun saveMemberProfile(
        displayName: String,
        email: String,
        phone: String?,
        preferredSport: String,
        emergencyContact: String?,
        dietaryNotes: String?
    ): Result<Unit> {
        var docPath = "memberProfiles"
        return try {
            val uid = requireUserId()
            require(displayName.trim().length in 2..80) {
                "Display name must be between 2 and 80 characters."
            }
            val safeEmail = email.trim().ifBlank { "member@goangymkhana.com" }.take(120)
            require(safeEmail.length in 3..120) { "A valid email address is required." }
            require(preferredSport.trim().length in 2..40) {
                "Please select your preferred sport or interest."
            }

            val docRef = db.collection("memberProfiles").document(uid)
            docPath = docRef.path
            val exists = docRef.get().await().exists()
            if (exists) {
                val payload = buildMemberProfileUpdatePayload(
                    displayName = displayName,
                    phone = phone,
                    preferredSport = preferredSport,
                    emergencyContact = emergencyContact,
                    dietaryNotes = dietaryNotes
                )
                docRef.update(payload).await()
            } else {
                val payload = buildMemberProfileCreatePayload(
                    userId = uid,
                    displayName = displayName,
                    email = safeEmail,
                    phone = phone,
                    preferredSport = preferredSport,
                    emergencyContact = emergencyContact,
                    dietaryNotes = dietaryNotes
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminReviewBooking(
        bookingId: String,
        newStatus: BookingStatus,
        managerNote: String?
    ): Result<Unit> {
        val docPath = "facilityBookings/$bookingId"
        return try {
            requireUserId()
            val updateMap = buildMap<String, Any> {
                put("status", newStatus.wireValue)
                val cleanNote = managerNote?.trim()?.takeIf { it.isNotEmpty() }?.take(300)
                if (cleanNote != null) put("managerNote", cleanNote)
                put("updatedAt", FieldValue.serverTimestamp())
            }
            db.collection("facilityBookings").document(bookingId).update(updateMap).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminReviewMembership(
        applicationId: String,
        newStatus: MembershipStatus,
        reviewNote: String?
    ): Result<Unit> {
        val docPath = "membershipApplications/$applicationId"
        return try {
            requireUserId()
            val updateMap = buildMap<String, Any> {
                put("status", newStatus.wireValue)
                val cleanNote = reviewNote?.trim()?.takeIf { it.isNotEmpty() }?.take(300)
                if (cleanNote != null) put("reviewNote", cleanNote)
                put("updatedAt", FieldValue.serverTimestamp())
            }
            db.collection("membershipApplications").document(applicationId).update(updateMap).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminUpdateEnquiryStatus(
        enquiryId: String,
        newStatus: EnquiryStatus
    ): Result<Unit> {
        val docPath = "clubEnquiries/$enquiryId"
        return try {
            requireUserId()
            db.collection("clubEnquiries").document(enquiryId)
                .update(
                    mapOf(
                        "status" to newStatus.wireValue,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }

    suspend fun adminUpdateMembershipInquiryStatus(
        inquiryId: String,
        newStatus: MembershipInquiryStatus
    ): Result<Unit> {
        val docPath = "membership_inquiries/$inquiryId"
        return try {
            requireUserId()
            db.collection("membership_inquiries").document(inquiryId)
                .update(
                    mapOf(
                        "status" to newStatus.wireValue,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docPath)
            Result.failure(e)
        }
    }
}
