package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.SportsTennis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.ClubCatalog
import com.example.data.ClubFacility

enum class ClubTab(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Club", Icons.Filled.Home, Icons.Outlined.Home),
    FACILITIES("Facilities", Icons.Filled.SportsTennis, Icons.Outlined.SportsTennis),
    EVENTS("Events", Icons.Filled.Celebration, Icons.Outlined.Celebration),
    MEMBERSHIP("Membership", Icons.Filled.Badge, Icons.Outlined.Badge),
    PORTAL("Portal", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle)
}

sealed class ClubDestination(
    val tab: ClubTab,
    val routeKey: String
) {
    data object Home : ClubDestination(ClubTab.HOME, "HOME")
    data object AboutUs : ClubDestination(ClubTab.HOME, "ABOUT_US")
    data object ClubMap : ClubDestination(ClubTab.HOME, "CLUB_MAP")
    data object Contact : ClubDestination(ClubTab.HOME, "CONTACT")
    data class Facilities(val preselectedFacility: ClubFacility? = null) :
        ClubDestination(ClubTab.FACILITIES, "FACILITIES")
    data class Booking(val preselectedFacility: ClubFacility? = null) :
        ClubDestination(ClubTab.FACILITIES, "BOOKING")
    data object Events : ClubDestination(ClubTab.EVENTS, "EVENTS")
    data object Membership : ClubDestination(ClubTab.MEMBERSHIP, "MEMBERSHIP")
    data object MembershipInquiry : ClubDestination(ClubTab.MEMBERSHIP, "MEMBERSHIP_INQUIRY")
    data object Portal : ClubDestination(ClubTab.PORTAL, "PORTAL")

    internal fun encode(): String = when (this) {
        Home -> "HOME"
        AboutUs -> "ABOUT_US"
        ClubMap -> "CLUB_MAP"
        Contact -> "CONTACT"
        is Facilities -> if (preselectedFacility != null) {
            "FACILITIES:${preselectedFacility.id}"
        } else {
            "FACILITIES"
        }
        is Booking -> if (preselectedFacility != null) {
            "BOOKING:${preselectedFacility.id}"
        } else {
            "BOOKING"
        }
        Events -> "EVENTS"
        Membership -> "MEMBERSHIP"
        MembershipInquiry -> "MEMBERSHIP_INQUIRY"
        Portal -> "PORTAL"
    }

    companion object {
        internal fun decode(raw: String): ClubDestination {
            return when {
                raw == "HOME" -> Home
                raw == "ABOUT_US" -> AboutUs
                raw == "CLUB_MAP" -> ClubMap
                raw == "CONTACT" -> Contact
                raw.startsWith("FACILITIES:") -> {
                    val id = raw.removePrefix("FACILITIES:")
                    val facility = ClubCatalog.facilities.firstOrNull { it.id == id }
                    Facilities(facility)
                }
                raw == "FACILITIES" -> Facilities()
                raw.startsWith("BOOKING:") -> {
                    val id = raw.removePrefix("BOOKING:")
                    val facility = ClubCatalog.facilities.firstOrNull { it.id == id }
                    Booking(facility)
                }
                raw == "BOOKING" -> Booking()
                raw == "EVENTS" -> Events
                raw == "MEMBERSHIP" -> Membership
                raw == "MEMBERSHIP_INQUIRY" -> MembershipInquiry
                raw == "PORTAL" -> Portal
                else -> Home
            }
        }
    }
}

@Stable
class ClubNavigationState(
    initialStack: List<ClubDestination> = listOf(ClubDestination.Home)
) {
    private var backStack by mutableStateOf(
        initialStack.ifEmpty { listOf(ClubDestination.Home) }
    )

    val currentDestination: ClubDestination
        get() = backStack.last()

    val activeTab: ClubTab
        get() = currentDestination.tab

    val canNavigateBack: Boolean
        get() = backStack.size > 1 || currentDestination != ClubDestination.Home

    fun navigateTo(destination: ClubDestination) {
        if (destination == currentDestination) return
        backStack = if (destination == ClubDestination.Home) {
            listOf(ClubDestination.Home)
        } else {
            val filtered = backStack.filterNot { it.routeKey == destination.routeKey }
            (filtered.ifEmpty { listOf(ClubDestination.Home) }) + destination
        }
    }

    fun navigateToTab(tab: ClubTab) {
        val destination = when (tab) {
            ClubTab.HOME -> ClubDestination.Home
            ClubTab.FACILITIES -> ClubDestination.Facilities()
            ClubTab.EVENTS -> ClubDestination.Events
            ClubTab.MEMBERSHIP -> ClubDestination.Membership
            ClubTab.PORTAL -> ClubDestination.Portal
        }
        navigateTo(destination)
    }

    fun consumePreselectedFacility() {
        val current = currentDestination
        if (current is ClubDestination.Facilities && current.preselectedFacility != null) {
            backStack = backStack.dropLast(1) + ClubDestination.Facilities(null)
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.size > 1) {
            backStack = backStack.dropLast(1)
            return true
        }
        if (currentDestination != ClubDestination.Home) {
            backStack = listOf(ClubDestination.Home)
            return true
        }
        return false
    }

    companion object {
        val Saver: Saver<ClubNavigationState, List<String>> = Saver(
            save = { state -> state.backStack.map { it.encode() } },
            restore = { encoded ->
                ClubNavigationState(encoded.map { ClubDestination.decode(it) })
            }
        )
    }
}

@Composable
fun rememberClubNavigationState(
    initialDestination: ClubDestination = ClubDestination.Home
): ClubNavigationState {
    return rememberSaveable(saver = ClubNavigationState.Saver) {
        ClubNavigationState(listOf(initialDestination))
    }
}
