package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.AuthScreen
import com.example.auth.signOut
import com.example.data.ClubRepository
import com.example.ui.ClubViewModel
import com.example.ui.navigation.ClubDestination
import com.example.ui.navigation.ClubTab
import com.example.ui.navigation.rememberClubNavigationState
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
import com.example.ui.theme.AntiqueGold
import com.example.ui.theme.GoanGymkhanaTheme
import com.example.ui.theme.HeritageGreen
import com.example.ui.theme.WarmIvory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun FirebaseAuth.authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
        trySend(auth.currentUser)
    }
    addAuthStateListener(listener)
    awaitClose { removeAuthStateListener(listener) }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoanGymkhanaTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(auth: FirebaseAuth = Firebase.auth) {
    val currentUser by auth.authStateFlow().collectAsStateWithLifecycle(initialValue = auth.currentUser)
    val user = currentUser

    if (user == null) {
        AuthScreen(onAuthSuccess = {})
    } else {
        key(user.uid) {
            val viewModel: ClubViewModel = viewModel(
                key = user.uid,
                factory = viewModelFactory {
                    initializer {
                        val app = checkNotNull(this[APPLICATION_KEY]) {
                            "APPLICATION_KEY missing from CreationExtras"
                        }
                        val databaseId = app.getString(R.string.firestore_database_id)
                        val db = FirebaseFirestore.getInstance(databaseId)
                        ClubViewModel(
                            repository = ClubRepository(db, auth),
                            currentUserId = user.uid,
                            userName = user.displayName ?: "Club Member",
                            userEmail = user.email ?: "",
                            emailVerified = user.isEmailVerified
                        )
                    }
                }
            )
            ClubAppShell(viewModel = viewModel)
        }
    }
}

@Composable
fun ClubAppShell(viewModel: ClubViewModel) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val navState = rememberClubNavigationState()

    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()
    val feedbackEvent by viewModel.feedbackEvent.collectAsStateWithLifecycle()
    val bookingsState by viewModel.bookingsState.collectAsStateWithLifecycle()
    val membershipsState by viewModel.membershipsState.collectAsStateWithLifecycle()
    val membershipInquiriesState by viewModel.membershipInquiriesState.collectAsStateWithLifecycle()
    val enquiriesState by viewModel.enquiriesState.collectAsStateWithLifecycle()
    val memberProfileState by viewModel.memberProfileState.collectAsStateWithLifecycle()
    val adminBookingsState by viewModel.adminBookingsState.collectAsStateWithLifecycle()
    val adminMembershipsState by viewModel.adminMembershipsState.collectAsStateWithLifecycle()
    val adminEnquiriesState by viewModel.adminEnquiriesState.collectAsStateWithLifecycle()
    val adminMembershipInquiriesState by viewModel.adminMembershipInquiriesState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackEvent?.id) {
        val event = feedbackEvent
        if (event != null) {
            if (!event.isError) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
            snackbarHostState.showSnackbar(
                message = event.message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearFeedback()
        }
    }

    BackHandler(enabled = navState.canNavigateBack) {
        navState.navigateBack()
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp
        val activeTab = navState.activeTab
        val currentIsError = feedbackEvent?.isError == true

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        containerColor = if (currentIsError) {
                            MaterialTheme.colorScheme.error
                        } else {
                            HeritageGreen
                        },
                        contentColor = WarmIvory,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (currentIsError) {
                                    Icons.Filled.ErrorOutline
                                } else {
                                    Icons.Filled.CheckCircle
                                },
                                contentDescription = null,
                                tint = if (currentIsError) WarmIvory else AntiqueGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = data.visuals.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = WarmIvory
                            )
                        }
                    }
                }
            },
            bottomBar = {
                if (!useNavigationRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        ClubTab.entries.forEach { tab ->
                            val selected = activeTab == tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navState.navigateToTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = HeritageGreen,
                                    selectedTextColor = HeritageGreen,
                                    indicatorColor = AntiqueGold.copy(alpha = 0.28f)
                                ),
                                modifier = Modifier.testTag("nav_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.statusBarsPadding()
                    ) {
                        ClubTab.entries.forEach { tab ->
                            val selected = activeTab == tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navState.navigateToTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = { Text(tab.label) },
                                modifier = Modifier.testTag("rail_${tab.name.lowercase()}")
                            )
                        }
                    }
                }

                val currentDestination = navState.currentDestination
                val contentTopModifier = if (currentDestination is ClubDestination.Home) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                }

                Box(modifier = contentTopModifier) {
                    AnimatedContent(
                        targetState = currentDestination,
                        contentKey = { it.routeKey },
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) togetherWith
                                fadeOut(animationSpec = tween(180))
                        },
                        label = "ClubDestinationTransition"
                    ) { destination ->
                        when (destination) {
                            ClubDestination.Home -> ClubHomeScreen(
                                onNavigateToAboutUs = {
                                    navState.navigateTo(ClubDestination.AboutUs)
                                },
                                onNavigateToFacilities = { facility ->
                                    if (facility != null) {
                                        navState.navigateTo(ClubDestination.Booking(facility))
                                    } else {
                                        navState.navigateTo(ClubDestination.Facilities())
                                    }
                                },
                                onNavigateToMembership = {
                                    navState.navigateTo(ClubDestination.Membership)
                                },
                                onNavigateToEvents = {
                                    navState.navigateTo(ClubDestination.Events)
                                },
                                onNavigateToMap = {
                                    navState.navigateTo(ClubDestination.ClubMap)
                                },
                                onNavigateToContact = {
                                    navState.navigateTo(ClubDestination.Contact)
                                },
                                onNotifyFeedback = viewModel::showInfoFeedback
                            )

                            ClubDestination.AboutUs -> AboutUsScreen(
                                onBack = { navState.navigateBack() },
                                onNavigateToFacilities = {
                                    navState.navigateTo(ClubDestination.Facilities())
                                },
                                onNavigateToMembership = {
                                    navState.navigateTo(ClubDestination.Membership)
                                },
                                onNavigateToMap = {
                                    navState.navigateTo(ClubDestination.ClubMap)
                                },
                                onNavigateToContact = {
                                    navState.navigateTo(ClubDestination.Contact)
                                }
                            )

                            ClubDestination.ClubMap -> ClubMapScreen(
                                onBack = { navState.navigateBack() },
                                onBookFacility = { facility ->
                                    navState.navigateTo(ClubDestination.Booking(facility))
                                },
                                onNotifyFeedback = viewModel::showInfoFeedback
                            )

                            ClubDestination.Contact -> ContactScreen(
                                defaultName = viewModel.userName,
                                isSubmitting = isSubmitting,
                                onBack = { navState.navigateBack() },
                                onOpenFullMap = {
                                    navState.navigateTo(ClubDestination.ClubMap)
                                },
                                onSubmitContactMessage = viewModel::submitEnquiry,
                                onNotifyFeedback = viewModel::showInfoFeedback
                            )

                            is ClubDestination.Facilities -> FacilitiesScreen(
                                onOpenBookingScreen = { facility ->
                                    navState.navigateTo(ClubDestination.Booking(facility))
                                }
                            )

                            is ClubDestination.Booking -> BookingScreen(
                                initialFacility = destination.preselectedFacility,
                                bookingsState = bookingsState,
                                isSubmitting = isSubmitting,
                                onBack = { navState.navigateBack() },
                                onSubmitBooking = viewModel::submitBooking
                            )

                            ClubDestination.Events -> EventsDiningScreen(
                                defaultName = viewModel.userName,
                                enquiriesState = enquiriesState,
                                isSubmitting = isSubmitting,
                                onSubmitEnquiry = viewModel::submitEnquiry,
                                onCloseEnquiry = viewModel::closeEnquiry
                            )

                            ClubDestination.Membership -> MembershipScreen(
                                defaultName = viewModel.userName,
                                defaultEmail = viewModel.userEmail,
                                membershipsState = membershipsState,
                                isSubmitting = isSubmitting,
                                onSubmitMembership = viewModel::submitMembership,
                                onWithdrawMembership = viewModel::withdrawMembership,
                                onOpenMembershipInquiry = {
                                    navState.navigateTo(ClubDestination.MembershipInquiry)
                                }
                            )

                            ClubDestination.MembershipInquiry -> MembershipInquiryScreen(
                                defaultName = viewModel.userName,
                                defaultEmail = viewModel.userEmail,
                                inquiriesState = membershipInquiriesState,
                                isSubmitting = isSubmitting,
                                onBack = { navState.navigateBack() },
                                onSubmitInquiry = viewModel::submitMembershipInquiry,
                                onCloseInquiry = viewModel::closeMembershipInquiry
                            )

                            ClubDestination.Portal -> PortalScreen(
                                userName = viewModel.userName,
                                userEmail = viewModel.userEmail,
                                isAdmin = isAdmin,
                                bookingsState = bookingsState,
                                adminBookingsState = adminBookingsState,
                                adminMembershipsState = adminMembershipsState,
                                adminEnquiriesState = adminEnquiriesState,
                                onCancelBooking = viewModel::cancelBooking,
                                onAdminReviewBooking = viewModel::reviewBookingAsAdmin,
                                onAdminReviewMembership = viewModel::reviewMembershipAsAdmin,
                                onAdminUpdateEnquiry = viewModel::updateEnquiryAsAdmin,
                                onSignOut = {
                                    signOut(credentialManager = credentialManager, scope = scope)
                                },
                                membershipsState = membershipsState,
                                membershipInquiriesState = membershipInquiriesState,
                                enquiriesState = enquiriesState,
                                adminMembershipInquiriesState = adminMembershipInquiriesState,
                                onWithdrawMembership = viewModel::withdrawMembership,
                                onCloseMembershipInquiry = viewModel::closeMembershipInquiry,
                                onCloseEnquiry = viewModel::closeEnquiry,
                                onAdminUpdateMembershipInquiry = viewModel::updateMembershipInquiryAsAdmin,
                                userId = viewModel.currentMemberId,
                                memberProfileState = memberProfileState,
                                isSubmitting = isSubmitting,
                                onSaveMemberProfile = viewModel::saveMemberProfile,
                                onNavigateToBooking = {
                                    navState.navigateTo(ClubDestination.Booking())
                                },
                                onNavigateToMap = {
                                    navState.navigateTo(ClubDestination.ClubMap)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
