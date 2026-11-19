package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ApplicationProvider
import com.example.data.ClubCatalog
import com.example.data.ClubFacility
import com.example.data.FacilityVerification
import com.example.data.MemberProfile
import com.example.data.MembershipInquiry
import com.example.data.MembershipInquiryStatus
import com.example.data.UiState
import com.example.ui.navigation.ClubDestination
import com.example.ui.navigation.ClubNavigationState
import com.example.ui.navigation.ClubTab
import com.example.ui.screens.AboutUsScreen
import com.example.ui.screens.BookingScreen
import com.example.ui.screens.ClubHomeScreen
import com.example.ui.screens.ClubMapScreen
import com.example.ui.screens.ContactScreen
import com.example.ui.screens.EventsDiningScreen
import com.example.ui.screens.FacilitiesScreen
import com.example.ui.screens.MembershipInquiryScreen
import com.example.ui.screens.MembershipScreen
import com.example.ui.screens.PortalScreen
import com.example.ui.screens.hasConfiguredMapsApiKey
import com.example.ui.screens.resolveFacilityImageRes
import com.example.ui.theme.GoanGymkhanaTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w411dp-h915dp-xxhdpi")
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Goan Gymkhana", appName)
    }

    @Test
    fun `navigation state handles transitions and back stack`() {
        val nav = ClubNavigationState()
        assertEquals(ClubDestination.Home, nav.currentDestination)
        assertEquals(ClubTab.HOME, nav.activeTab)

        nav.navigateTo(ClubDestination.AboutUs)
        assertEquals(ClubDestination.AboutUs, nav.currentDestination)
        assertTrue(nav.canNavigateBack)

        val pool = ClubCatalog.facilities.first { it.id == "swimming_pool" }
        nav.navigateTo(ClubDestination.Booking(pool))
        assertEquals(ClubDestination.Booking(pool), nav.currentDestination)
        assertEquals(ClubTab.FACILITIES, nav.activeTab)

        nav.navigateTo(ClubDestination.MembershipInquiry)
        assertEquals(ClubDestination.MembershipInquiry, nav.currentDestination)
        assertEquals(ClubTab.MEMBERSHIP, nav.activeTab)

        nav.navigateTo(ClubDestination.ClubMap)
        assertEquals(ClubDestination.ClubMap, nav.currentDestination)
        assertEquals(ClubTab.HOME, nav.activeTab)

        nav.navigateTo(ClubDestination.Contact)
        assertEquals(ClubDestination.Contact, nav.currentDestination)
        assertEquals(ClubTab.HOME, nav.activeTab)

        assertTrue(nav.navigateBack())
        assertEquals(ClubDestination.ClubMap, nav.currentDestination)
        assertTrue(nav.navigateBack())
        assertEquals(ClubDestination.MembershipInquiry, nav.currentDestination)
        assertTrue(nav.navigateBack())
        assertEquals(ClubDestination.Booking(pool), nav.currentDestination)
        assertTrue(nav.navigateBack())
        assertEquals(ClubDestination.AboutUs, nav.currentDestination)
        assertTrue(nav.navigateBack())
        assertEquals(ClubDestination.Home, nav.currentDestination)
    }

    @Test
    fun `home screen renders hero intro and quick access cards`() {
        composeTestRule.setContent {
            GoanGymkhanaTheme {
                ClubHomeScreen(
                    onNavigateToAboutUs = {},
                    onNavigateToFacilities = {},
                    onNavigateToMembership = {},
                    onNavigateToEvents = {}
                )
            }
        }

        composeTestRule.onNodeWithText("The Goan Gymkhana").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_explore_facilities_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("home_lazy_column").performScrollToIndex(2)
        composeTestRule.onNodeWithTag("quick_access_facilities_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("quick_access_membership_card").assertIsDisplayed()
    }

    @Test
    fun `facilities screen displays core sports grid and booking button`() {
        composeTestRule.setContent {
            GoanGymkhanaTheme {
                FacilitiesScreen(onOpenBookingScreen = {})
            }
        }

        composeTestRule.onNodeWithTag("open_booking_screen_btn").assertIsDisplayed()
        composeTestRule.onNodeWithTag("facilities_grid")
            .performScrollToNode(hasTestTag("facility_card_swimming_pool"))
        composeTestRule.onNodeWithTag("facility_card_swimming_pool").assertIsDisplayed()
        composeTestRule.onNodeWithTag("facilities_grid")
            .performScrollToNode(hasTestTag("facility_card_astroturf_football"))
        composeTestRule.onNodeWithTag("facility_card_astroturf_football").assertIsDisplayed()
    }

    @Test
    fun `facility placeholder image resolver maps known sports and falls back safely`() {
        val pool = ClubCatalog.facilities.first { it.id == "swimming_pool" }
        val pitch = ClubCatalog.facilities.first { it.id == "astroturf_football" }
        val badminton = ClubCatalog.facilities.first { it.id == "badminton" }
        val hall = ClubCatalog.facilities.first { it.id == "clubhouse_hall" }
        val unknownZero = ClubFacility(
            id = "custom_area",
            name = "Custom Space",
            subtitle = "Test",
            category = "General",
            verification = FacilityVerification.CONFIRMED,
            description = "Test",
            bookingGuidance = "Test",
            maxParticipants = 4,
            imageRes = 0
        )

        assertEquals(R.drawable.img_swimming_pool, resolveFacilityImageRes(pool))
        assertEquals(R.drawable.img_astroturf_pitch, resolveFacilityImageRes(pitch))
        assertEquals(R.drawable.img_racquet_sports, resolveFacilityImageRes(badminton))
        assertEquals(R.drawable.img_clubhouse_hero, resolveFacilityImageRes(hall))
        assertEquals(R.drawable.img_clubhouse_hero, resolveFacilityImageRes(unknownZero))
    }

    @Test
    fun `booking screen selects facility opens date picker and submits reservation`() {
        var submittedFacilityId: String? = null
        var submittedDate: String? = null

        composeTestRule.setContent {
            GoanGymkhanaTheme {
                BookingScreen(
                    initialFacility = null,
                    bookingsState = UiState.Success(emptyList()),
                    isSubmitting = false,
                    onBack = {},
                    onSubmitBooking = { facility, date, _, _, _, onSuccess ->
                        submittedFacilityId = facility.id
                        submittedDate = date
                        onSuccess()
                    }
                )
            }
        }

        composeTestRule.onNodeWithTag("booking_lazy_column").performScrollToIndex(1)
        composeTestRule.onNodeWithTag("select_facility_badminton").performClick()

        composeTestRule.onNodeWithTag("booking_lazy_column").performScrollToIndex(2)
        composeTestRule.onNodeWithTag("open_date_picker_btn").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag("confirm_date_picker_btn").assertIsDisplayed().performClick()

        composeTestRule.onNodeWithTag("booking_lazy_column").performScrollToIndex(2)
        composeTestRule.onNodeWithTag("submit_reservation_btn").assertIsDisplayed().performClick()

        assertEquals("badminton", submittedFacilityId)
        assertTrue(!submittedDate.isNullOrBlank() && submittedDate!!.length == 10)
    }

    @Test
    fun `about us screen renders heritage milestones`() {
        composeTestRule.setContent {
            GoanGymkhanaTheme {
                AboutUsScreen(
                    onBack = {},
                    onNavigateToFacilities = {},
                    onNavigateToMembership = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Nine Decades on Ngara Road").assertIsDisplayed()
    }

    @Test
    fun `events screen renders upcoming gatherings`() {
        composeTestRule.setContent {
            GoanGymkhanaTheme {
                EventsDiningScreen(
                    defaultName = "Tester",
                    enquiriesState = UiState.Success(emptyList()),
                    isSubmitting = false,
                    onSubmitEnquiry = { _, _, _, _, _, _, _ -> },
                    onCloseEnquiry = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Upcoming Club Gatherings").assertIsDisplayed()
    }

    @Test
    fun `membership screen renders application form`() {
        composeTestRule.setContent {
            GoanGymkhanaTheme {
                MembershipScreen(
                    defaultName = "Tester",
                    defaultEmail = "tester@goangymkhana.com",
                    membershipsState = UiState.Success(emptyList()),
                    isSubmitting = false,
                    onSubmitMembership = { _, _, _, _, _, _, _ -> },
                    onWithdrawMembership = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Become Part of Our Story").assertIsDisplayed()
    }

    @Test
    fun `portal screen renders member bookings`() {
        composeTestRule.setContent {
            GoanGymkhanaTheme {
                PortalScreen(
                    userName = "Tester",
                    userEmail = "tester@goangymkhana.com",
                    isAdmin = false,
                    bookingsState = UiState.Success(emptyList()),
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
        composeTestRule.onNodeWithText("My Facility Bookings").assertIsDisplayed()
    }

    @Test
    fun `membership inquiry screen submits name email and interest`() {
        var submittedName: String? = null
        var submittedEmail: String? = null
        var submittedInterest: String? = null

        composeTestRule.setContent {
            GoanGymkhanaTheme {
                MembershipInquiryScreen(
                    defaultName = "",
                    defaultEmail = "",
                    inquiriesState = UiState.Success(emptyList()),
                    isSubmitting = false,
                    onBack = {},
                    onSubmitInquiry = { name, email, interest, onSuccess ->
                        submittedName = name
                        submittedEmail = email
                        submittedInterest = interest
                        onSuccess()
                    }
                )
            }
        }

        composeTestRule.onNodeWithTag("membership_inquiry_lazy_column").performScrollToIndex(2)
        composeTestRule.onNodeWithTag("inquiry_name_input").performTextReplacement("Carlos Pinto")
        composeTestRule.onNodeWithTag("inquiry_email_input").performTextReplacement("carlos@example.com")
        composeTestRule.onNodeWithTag("inquiry_chip_swimming").performClick()
        composeTestRule.onNodeWithTag("submit_membership_inquiry_btn").performClick()

        assertEquals("Carlos Pinto", submittedName)
        assertEquals("carlos@example.com", submittedEmail)
        assertEquals("Swimming", submittedInterest)
    }

    @Test
    fun `club map screen supports zoom recenter and booking from campus grounds`() {
        var bookedFacilityId: String? = null

        composeTestRule.setContent {
            GoanGymkhanaTheme {
                ClubMapScreen(
                    onBack = {},
                    onBookFacility = { facility -> bookedFacilityId = facility.id },
                    onNotifyFeedback = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Locate The Goan Gymkhana").assertIsDisplayed()
        composeTestRule.onNodeWithText("${ClubCatalog.COORDINATES} · 100%").assertIsDisplayed()

        composeTestRule.onNodeWithTag("map_zoom_in_btn").performClick()
        composeTestRule.onNodeWithText("${ClubCatalog.COORDINATES} · 135%").assertIsDisplayed()

        composeTestRule.onNodeWithTag("map_recenter_btn").performClick()
        composeTestRule.onNodeWithText("${ClubCatalog.COORDINATES} · 100%").assertIsDisplayed()

        composeTestRule.onNodeWithTag("map_mode_club_grounds").performClick()
        composeTestRule.onNodeWithTag("club_map_lazy_column").performScrollToIndex(3)
        composeTestRule.onNodeWithTag("map_pin_chip_campus_pool").performClick()
        composeTestRule.onNodeWithTag("club_map_lazy_column").performScrollToIndex(4)
        composeTestRule.onNodeWithTag("map_book_facility_btn").assertIsDisplayed().performClick()

        assertEquals("swimming_pool", bookedFacilityId)
    }

    @Test
    fun `portal screen filters membership activity and supports admin inquiry review`() {
        var updatedId: String? = null
        var updatedStatus: MembershipInquiryStatus? = null
        val sampleInquiry = MembershipInquiry(
            id = "inq_1",
            userId = "user_1",
            name = "Asha Fernandes",
            email = "asha@example.com",
            interest = "Badminton",
            status = MembershipInquiryStatus.SUBMITTED.wireValue
        )

        composeTestRule.setContent {
            GoanGymkhanaTheme {
                PortalScreen(
                    userName = "Admin",
                    userEmail = "admin@goangymkhana.com",
                    isAdmin = true,
                    bookingsState = UiState.Success(emptyList()),
                    adminBookingsState = UiState.Success(emptyList()),
                    adminMembershipsState = UiState.Success(emptyList()),
                    adminEnquiriesState = UiState.Success(emptyList()),
                    membershipInquiriesState = UiState.Success(listOf(sampleInquiry)),
                    adminMembershipInquiriesState = UiState.Success(listOf(sampleInquiry)),
                    onCancelBooking = {},
                    onAdminReviewBooking = { _, _, _ -> },
                    onAdminReviewMembership = { _, _, _ -> },
                    onAdminUpdateEnquiry = { _, _ -> },
                    onAdminUpdateMembershipInquiry = { id, status ->
                        updatedId = id
                        updatedStatus = status
                    },
                    onSignOut = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("portal_filter_membership").performClick()
        composeTestRule.onNodeWithText("My Membership Inquiries").assertIsDisplayed()
        composeTestRule.onNodeWithTag("portal_lazy_column")
            .performScrollToNode(hasTestTag("admin_membership_inquiry_inq_1"))
        composeTestRule.onNodeWithText("Mark Reviewed").performClick()

        assertEquals("inq_1", updatedId)
        assertEquals(MembershipInquiryStatus.REVIEWED, updatedStatus)
    }

    @Test
    fun `contact screen shows official coordinates National Museum relation and submits message`() {
        var sentName: String? = null
        var sentPhone: String? = null
        var sentMessage: String? = null

        assertEquals(false, hasConfiguredMapsApiKey("MY_GOOGLE_MAPS_API_KEY"))
        assertEquals(true, hasConfiguredMapsApiKey("AIzaSyDemoRealKeyValue123"))

        composeTestRule.setContent {
            GoanGymkhanaTheme {
                ContactScreen(
                    defaultName = "Nadia Pereira",
                    isSubmitting = false,
                    onBack = {},
                    onOpenFullMap = {},
                    onSubmitContactMessage = { name, phone, _, _, _, msg, onSuccess ->
                        sentName = name
                        sentPhone = phone
                        sentMessage = msg
                        onSuccess()
                    },
                    onNotifyFeedback = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Find The Goan Gymkhana").assertIsDisplayed()
        composeTestRule.onNodeWithTag("contact_map_card").assertIsDisplayed()
        composeTestRule.onNodeWithTag("contact_chip_museum_pin").performClick()
        composeTestRule.onNodeWithTag("contact_recenter_both_btn").performClick()

        composeTestRule.onNodeWithTag("contact_lazy_column").performScrollToIndex(3)
        composeTestRule.onNodeWithTag("contact_phone_input").performTextReplacement("+254720526523")
        composeTestRule.onNodeWithTag("contact_message_input").performTextReplacement("Gate parking hours?")
        composeTestRule.onNodeWithTag("contact_send_message_btn").performClick()

        assertEquals("Nadia Pereira", sentName)
        assertEquals("+254720526523", sentPhone)
        assertEquals("Gate parking hours?", sentMessage)
    }

    @Test
    fun `portal screen renders member pass privileges and saves personal profile`() {
        var savedDisplayName: String? = null
        var savedPhone: String? = null
        var savedSport: String? = null

        val existingProfile = MemberProfile(
            userId = "member_90th_uid",
            displayName = "Xavier D'Souza",
            email = "xavier@goangymkhana.com",
            phone = "+254711000111",
            preferredSport = "Swimming",
            membershipTier = "Member Portal"
        )

        composeTestRule.setContent {
            GoanGymkhanaTheme {
                PortalScreen(
                    userName = "Xavier D'Souza",
                    userEmail = "xavier@goangymkhana.com",
                    isAdmin = false,
                    bookingsState = UiState.Success(emptyList()),
                    adminBookingsState = UiState.Success(emptyList()),
                    adminMembershipsState = UiState.Success(emptyList()),
                    adminEnquiriesState = UiState.Success(emptyList()),
                    onCancelBooking = {},
                    onAdminReviewBooking = { _, _, _ -> },
                    onAdminReviewMembership = { _, _, _ -> },
                    onAdminUpdateEnquiry = { _, _ -> },
                    onSignOut = {},
                    userId = "member_90th_uid",
                    memberProfileState = UiState.Success(existingProfile),
                    isSubmitting = false,
                    onSaveMemberProfile = { name, phone, sport, _, _, onSuccess ->
                        savedDisplayName = name
                        savedPhone = phone
                        savedSport = sport
                        onSuccess()
                    }
                )
            }
        }

        composeTestRule.onNodeWithTag("portal_filter_profile").performClick()
        composeTestRule.onNodeWithTag("portal_digital_pass_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("GG-MEMBER").assertIsDisplayed()

        composeTestRule.onNodeWithTag("portal_lazy_column")
            .performScrollToNode(hasTestTag("privilege_card_gate_parking"))
        composeTestRule.onNodeWithText("Ngara Road Member Gate & Parking").assertIsDisplayed()

        composeTestRule.onNodeWithTag("portal_lazy_column")
            .performScrollToNode(hasTestTag("portal_edit_profile_btn"))
        composeTestRule.onNodeWithTag("portal_edit_profile_btn").performClick()

        composeTestRule.onNodeWithTag("portal_lazy_column")
            .performScrollToNode(hasTestTag("portal_profile_editor_card"))
        composeTestRule.onNodeWithTag("portal_profile_name_input").performTextReplacement("Xavier M. D'Souza")
        composeTestRule.onNodeWithTag("portal_profile_phone_input").performTextReplacement("+254722334455")
        composeTestRule.onNodeWithTag("portal_sport_chip_badminton").performClick()
        composeTestRule.onNodeWithTag("portal_lazy_column")
            .performScrollToNode(hasTestTag("portal_save_profile_btn"))
        composeTestRule.onNodeWithTag("portal_save_profile_btn").performClick()

        assertEquals("Xavier M. D'Souza", savedDisplayName)
        assertEquals("+254722334455", savedPhone)
        assertEquals("Badminton", savedSport)
    }
}
