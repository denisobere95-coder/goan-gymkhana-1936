package com.example.data

import androidx.annotation.DrawableRes
import com.example.R

enum class FacilityVerification(val label: String) {
    CONFIRMED("Club & Screenshot Verified"),
    DIRECTORY("External Directory Listed"),
    HOSPITALITY("Enquiry & Hire")
}

data class ClubFacility(
    val id: String,
    val name: String,
    val subtitle: String,
    val category: String,
    val verification: FacilityVerification,
    val description: String,
    val bookingGuidance: String,
    val maxParticipants: Int,
    @param:DrawableRes val imageRes: Int
)

data class HeritageMilestone(
    val era: String,
    val title: String,
    val summary: String,
    val sourceNote: String
)

data class ClubEventHighlight(
    val id: String,
    val title: String,
    val category: String,
    val dateLabel: String,
    val location: String,
    val description: String,
    val actionType: String
)

data class VerificationItem(
    val title: String,
    val detail: String,
    val verified: Boolean = false
)

data class ClubMapPin(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val relX: Float,
    val relY: Float,
    val isPrimaryClub: Boolean = false,
    val facilityId: String? = null
)

data class ArrivalRoute(
    val origin: String,
    val distanceLabel: String,
    val guidance: String
)

data class MemberPrivilegeNotice(
    val id: String,
    val badge: String,
    val title: String,
    val summary: String,
    val guidance: String
)

object ClubCatalog {
    const val CLUB_NAME = "The Goan Gymkhana"
    const val CLUB_SUBTITLE = "Nairobi · Ngara Road"
    const val ADDRESS = "Ngara Road, opposite the National Museums of Kenya, Nairobi"
    const val COORDINATES = "-1.27246, 36.81645"
    const val LATITUDE = -1.27246
    const val LONGITUDE = 36.81645
    const val MUSEUM_NAME = "National Museum of Kenya"
    const val MUSEUM_LATITUDE = -1.27400
    const val MUSEUM_LONGITUDE = 36.81470
    const val MUSEUM_COORDINATES = "-1.27400, 36.81470"
    const val MUSEUM_DISTANCE_LABEL = "~250m across Museum Hill / Ngara Road"
    const val PHONE = "+254 720 526 523"
    const val EMAIL = "info@goangymkhana.com"
    const val LISTED_HOURS_WEEKDAY = "Mon–Sat: 9:00 AM – 10:00 PM (third-party listing)"
    const val LISTED_HOURS_SUNDAY = "Sunday: 9:00 AM – 10:00 AM (Waze listing · confirm with club)"
    const val RATING_SUMMARY = "4.3 / 5 across ~966 third-party Google reviews (pending live refresh)"
    const val ADMIN_BOOTSTRAP_EMAIL = "denisobere95@gmail.com"

    val timeSlots = listOf(
        "09:00 – 11:00",
        "11:00 – 13:00",
        "14:00 – 16:00",
        "16:00 – 18:00",
        "18:00 – 20:00",
        "20:00 – 22:00"
    )

    val membershipCategories = listOf("Individual", "Family", "Junior", "Corporate", "Social")
    val sportingInterests = listOf(
        "Swimming",
        "AstroTurf Football",
        "Badminton",
        "Table Tennis",
        "Padel",
        "Indoor Games",
        "Social & Dining"
    )

    val enquiryTypes = listOf(
        "Dining & Bar",
        "Private Celebration",
        "Corporate Event",
        "Sports & Coaching",
        "General"
    )

    val facilities = listOf(
        ClubFacility(
            id = "swimming_pool",
            name = "Swimming Pool",
            subtitle = "Poolside leisure behind the main clubhouse",
            category = "Aquatics",
            verification = FacilityVerification.CONFIRMED,
            description = "Set behind the historic main clubhouse, the swimming pool is a signature space for lap swimming, weekend family relaxation, and poolside gatherings.",
            bookingGuidance = "Submit a session request; lane schedules and guest fees are confirmed by management.",
            maxParticipants = 20,
            imageRes = R.drawable.img_swimming_pool
        ),
        ClubFacility(
            id = "astroturf_football",
            name = "AstroTurf Football Pitch",
            subtitle = "Team play & evening pitch sessions",
            category = "Field Sport",
            verification = FacilityVerification.CONFIRMED,
            description = "Advertised for football pitch bookings and organised community matches, continuing a pitch tradition documented since the club's early decades.",
            bookingGuidance = "Select your preferred evening or weekend slot and squad count for management approval.",
            maxParticipants = 30,
            imageRes = R.drawable.img_astroturf_pitch
        ),
        ClubFacility(
            id = "badminton",
            name = "Badminton Courts",
            subtitle = "Indoor court play & club socials",
            category = "Racquet Sport",
            verification = FacilityVerification.CONFIRMED,
            description = "Active indoor badminton courts supporting casual social rallies, regular member nights, and competitive court sessions.",
            bookingGuidance = "Ideal for singles or doubles reservations (2–8 players).",
            maxParticipants = 8,
            imageRes = R.drawable.img_racquet_sports
        ),
        ClubFacility(
            id = "table_tennis",
            name = "Table Tennis Hall",
            subtitle = "Recreational & competitive play",
            category = "Indoor Sport",
            verification = FacilityVerification.CONFIRMED,
            description = "One of the club's actively promoted indoor disciplines, welcoming recreational players and competitive Nairobi table tennis trials.",
            bookingGuidance = "Reserve a table session and note if equipment assistance is needed.",
            maxParticipants = 8,
            imageRes = R.drawable.img_racquet_sports
        ),
        ClubFacility(
            id = "clubhouse_hall",
            name = "Historic Clubhouse & Balcony",
            subtitle = "1943 Victorian stone clubhouse & indoor games",
            category = "Heritage & Events",
            verification = FacilityVerification.HOSPITALITY,
            description = "Featuring Victorian architectural details, an upper-floor social hall, viewing balcony, and indoor games area for community gatherings and celebrations.",
            bookingGuidance = "For private functions, anniversaries, or corporate meetings up to 60 guests.",
            maxParticipants = 60,
            imageRes = R.drawable.img_clubhouse_hero
        ),
        ClubFacility(
            id = "padel",
            name = "Padel Court",
            subtitle = "Listed via Padel254 / Playtomic directory",
            category = "Racquet Sport",
            verification = FacilityVerification.DIRECTORY,
            description = "Referenced alongside the club in external padel directories. Booking availability and whether the court is operated directly by the club or a neighbouring partner is confirmed upon enquiry.",
            bookingGuidance = "Submit an enquiry request so management can confirm court operation and slot availability.",
            maxParticipants = 4,
            imageRes = R.drawable.img_racquet_sports
        )
    )

    val unconfirmedFacilityCandidates = listOf(
        "Lawn Tennis Courts",
        "Squash Courts",
        "Cricket Ground",
        "Hockey",
        "Snooker & Billiards",
        "Fitness & Gym Room",
        "Children's Recreational Area"
    )

    val heritageTimeline = listOf(
        HeritageMilestone(
            era = "1935 – 1936",
            title = "Founding in Colonial Nairobi",
            summary = "Established by members of the Goan community serving in Kenya's civil service and railway sector as an inclusive social and sporting refuge when excluded from segregated colonial clubs.",
            sourceNote = "Business Daily (2016) & archival records note 1935–May 1936 as the founding period."
        ),
        HeritageMilestone(
            era = "1943",
            title = "The Victorian Stone Clubhouse",
            summary = "Opening of the landmark stone clubhouse on Ngara Road featuring Victorian architectural touches, an upper-floor social hall, and a signature viewing balcony overlooking the grounds.",
            sourceNote = "Historical accounts & architectural heritage records."
        ),
        HeritageMilestone(
            era = "Post-Independence Era",
            title = "Open Membership to All Communities",
            summary = "Originally formed by and for the Goan community, the club subsequently opened its membership doors to all races and communities across Nairobi.",
            sourceNote = "Business Daily historical feature."
        ),
        HeritageMilestone(
            era = "90th Anniversary",
            title = "Sport, Community & Celebration Today",
            summary = "Celebrating nine decades of sport and fellowship on Ngara Road—bringing together swimming, AstroTurf football, badminton, table tennis, and milestone social gatherings.",
            sourceNote = "Club souvenir & 90th-anniversary celebration archives."
        )
    )

    val eventHighlights = listOf(
        ClubEventHighlight(
            id = "anniversary_90",
            title = "90th Anniversary Heritage Gathering",
            category = "Club Milestone",
            dateLabel = "Sat, 28 Nov 2026 · 4:00 PM (Provisional)",
            location = "1943 Clubhouse & Viewing Balcony",
            description = "Celebrate nine decades of Ngara Road sporting heritage with fellow members, archival displays, and community fellowship.",
            actionType = "General"
        ),
        ClubEventHighlight(
            id = "poolside_social",
            title = "Poolside Family Afternoon Social",
            category = "Family & Aquatics",
            dateLabel = "Sat, 5 Dec 2026 · 1:00 PM (Provisional)",
            location = "Clubhouse Swimming Pool",
            description = "Relaxed weekend poolside gathering behind the main clubhouse for families, leisure swimmers, and refreshments.",
            actionType = "Private Celebration"
        ),
        ClubEventHighlight(
            id = "astroturf_evening",
            title = "AstroTurf Community Football Evening",
            category = "Football Social",
            dateLabel = "Fri, 11 Dec 2026 · 6:00 PM (Provisional)",
            location = "Ngara Road AstroTurf Pitch",
            description = "Friendly floodlit 7-a-side community matches and supporter social on the club's artificial turf pitch.",
            actionType = "Sports & Coaching"
        ),
        ClubEventHighlight(
            id = "racquet_night",
            title = "Badminton & Table Tennis Club Night",
            category = "Indoor Sports",
            dateLabel = "Wed, 16 Dec 2026 · 6:30 PM (Provisional)",
            location = "Indoor Sports Hall",
            description = "Open-ladder social doubles and table tennis rallies bringing together recreational and competitive club players.",
            actionType = "Sports & Coaching"
        ),
        ClubEventHighlight(
            id = "balcony_dining",
            title = "Upper Balcony Sundowner & Dining Enquiry",
            category = "Hospitality & Dining",
            dateLabel = "Sat, 19 Dec 2026 · 5:30 PM (Provisional)",
            location = "Upper-Floor Social Area & Veranda",
            description = "Evening hospitality gathering on the historic balcony; dining arrangements and menus are confirmed directly with management.",
            actionType = "Dining & Bar"
        )
    )

    val verificationChecklist = listOf(
        VerificationItem("Official high-resolution club crest/logo", "Pending upload from club archive"),
        VerificationItem("Confirmed founding year (1935 vs May 1936)", "Displayed transparently as 1935–1936"),
        VerificationItem("Full current sports & facility inventory", "Padel & 7 other sports flagged for confirmation"),
        VerificationItem("Restaurant & bar menu and dining hours", "Enquiry-based until official menu supplied"),
        VerificationItem("Membership categories, eligibility & fee schedule", "Digital expression-of-interest active"),
        VerificationItem("Facility booking tariffs & slot schedules", "Request-and-approval workflow active"),
        VerificationItem("Event hall capacity & private hire rates", "Handled via hospitality enquiry"),
        VerificationItem("Confirmed official email (info@goangymkhana.com)", "Listed from prior records; verify active inbox"),
        VerificationItem("Confirmed Sunday & public holiday hours", "Third-party Waze listing (9–10 AM Sun) flagged"),
        VerificationItem("Permission for official club photography", "Curated editorial visuals used in preview"),
        VerificationItem("Authorised management administrator", "Bootstrapped review desk ready in Portal")
    )

    val areaMapPins = listOf(
        ClubMapPin(
            id = "goan_gymkhana",
            title = "The Goan Gymkhana",
            category = "Clubhouse & Grounds",
            description = "Ngara Road, Nairobi (-1.27246, 36.81645). Historic 1943 stone clubhouse, swimming pool, AstroTurf pitch & indoor courts.",
            relX = 0.56f,
            relY = 0.44f,
            isPrimaryClub = true
        ),
        ClubMapPin(
            id = "national_museums",
            title = "National Museums of Kenya",
            category = "Key Landmark (Opposite)",
            description = "Directly opposite The Goan Gymkhana across Museum Hill / Kipande Road—primary visual landmark for visitors.",
            relX = 0.30f,
            relY = 0.40f
        ),
        ClubMapPin(
            id = "museum_hill_interchange",
            title = "Museum Hill Interchange",
            category = "Major Approach",
            description = "Connects Uhuru Highway, Waiyaki Way (Westlands), and Forest Road onto Ngara Road in under 2 minutes.",
            relX = 0.35f,
            relY = 0.22f
        ),
        ClubMapPin(
            id = "ngara_gate",
            title = "Ngara Road Main Entrance",
            category = "Club Gate & Parking",
            description = "Vehicular and pedestrian entry along Ngara Road with member and guest parking inside the compound.",
            relX = 0.61f,
            relY = 0.52f
        ),
        ClubMapPin(
            id = "globe_roundabout",
            title = "Globe Cinema Roundabout",
            category = "CBD Approach",
            description = "Gateway from Moi Avenue & Nairobi CBD northbound toward Ngara Road (approx. 5 min drive).",
            relX = 0.67f,
            relY = 0.77f
        )
    )

    val campusMapPins = listOf(
        ClubMapPin(
            id = "campus_clubhouse",
            title = "1943 Stone Clubhouse & Balcony",
            category = "Heritage & Dining",
            description = "Victorian stone veranda, upper-floor social hall, bar, and viewing balcony overlooking the pitch.",
            relX = 0.46f,
            relY = 0.30f,
            isPrimaryClub = true,
            facilityId = "clubhouse_hall"
        ),
        ClubMapPin(
            id = "campus_pool",
            title = "Swimming Pool",
            category = "Aquatics",
            description = "Located directly behind the main clubhouse for lap swimming and family leisure.",
            relX = 0.24f,
            relY = 0.26f,
            facilityId = "swimming_pool"
        ),
        ClubMapPin(
            id = "campus_pitch",
            title = "AstroTurf Football Pitch",
            category = "Field Sport",
            description = "Floodlit artificial turf football pitch adjacent to the main veranda viewing deck.",
            relX = 0.64f,
            relY = 0.58f,
            facilityId = "astroturf_football"
        ),
        ClubMapPin(
            id = "campus_indoor_hall",
            title = "Badminton & Table Tennis Hall",
            category = "Indoor Sports",
            description = "Indoor court hall hosting social rallies, badminton ladders, and table tennis play.",
            relX = 0.28f,
            relY = 0.60f,
            facilityId = "badminton"
        ),
        ClubMapPin(
            id = "campus_padel",
            title = "Racquet & Padel Wing",
            category = "Directory Court",
            description = "External directory-listed padel zone; confirm access and court operation with management.",
            relX = 0.76f,
            relY = 0.32f,
            facilityId = "padel"
        ),
        ClubMapPin(
            id = "campus_gate",
            title = "Ngara Road Gate & Parking",
            category = "Arrival Point",
            description = "Main security gate off Ngara Road opposite the National Museums grounds.",
            relX = 0.48f,
            relY = 0.84f
        )
    )

    val arrivalRoutes = listOf(
        ArrivalRoute(
            origin = "From Nairobi CBD (Moi Ave / Tom Mboya St)",
            distanceLabel = "~2.0 km · 5–8 min",
            guidance = "Head north past Globe Cinema Roundabout onto Kipande Road / Ngara Road. Continue opposite the National Museums of Kenya to the club gate."
        ),
        ArrivalRoute(
            origin = "From Westlands / Waiyaki Way",
            distanceLabel = "~3.2 km · 6–10 min",
            guidance = "Take Museum Hill exit off Nairobi Expressway or Uhuru Highway, loop onto Museum Hill Road toward Ngara Road opposite the Museum entrance."
        ),
        ArrivalRoute(
            origin = "From Thika Road / Parklands",
            distanceLabel = "~2.5 km · 5–9 min",
            guidance = "Exit at Muthaiga / Forest Road toward Museum Hill and turn onto Ngara Road just before Kipande Road junction."
        )
    )

    val memberPrivileges = listOf(
        MemberPrivilegeNotice(
            id = "gate_parking",
            badge = "GATE & COMPOUND",
            title = "Ngara Road Member Gate & Parking",
            summary = "Priority vehicular entry opposite National Museums of Kenya for signed-in visitors & registered members.",
            guidance = "Present your Digital Member Pass ID from this Portal at the Ngara Road security desk during peak evening AstroTurf & social hours."
        ),
        MemberPrivilegeNotice(
            id = "balcony_guest_rules",
            badge = "CLUBHOUSE ACCESS",
            title = "1943 Clubhouse & Balcony Privileges",
            summary = "Private access to upper-floor veranda seating, indoor games lounge, and member guest sign-in.",
            guidance = "Members may sign in up to 4 accompanied guests per visit at the front desk; smart-casual attire applies for evening hospitality."
        ),
        MemberPrivilegeNotice(
            id = "sports_ladders",
            badge = "SPORTS SECTIONS",
            title = "Weekly Club Night Ladders & Pool Hours",
            summary = "Priority court reservation windows for Badminton, Table Tennis, and morning lap swimming.",
            guidance = "Select your Preferred Sport in your Personal Profile below so the Sports Desk can match ladder & coaching updates."
        ),
        MemberPrivilegeNotice(
            id = "archive_90",
            badge = "HERITAGE VAULT",
            title = "90th Anniversary Heritage Archive",
            summary = "Curated milestones from the 1935–1936 founding era and the 1943 Victorian stone clubhouse completion.",
            guidance = "Physical souvenir albums and historical committee records are available for viewing on request via the Club Desk."
        )
    )
}
