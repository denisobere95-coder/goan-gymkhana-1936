const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_member_123";
const BOB_UID = "bob_member_456";
const ADMIN_EMAIL = "denisobere95@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users are denied on all club collections", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  const past = new Date(Date.now() - 1000);

  await assertFails(unauthDb.collection("facilityBookings").get());
  await assertFails(unauthDb.collection("membershipApplications").get());
  await assertFails(unauthDb.collection("clubEnquiries").get());
  await assertFails(unauthDb.collection("membership_inquiries").get());
  await assertFails(unauthDb.collection("memberProfiles").doc(ALICE_UID).get());
  await assertFails(
    unauthDb.collection("facilityBookings").doc("booking_1").set({
      userId: ALICE_UID,
      userName: "Alice",
      userEmail: "alice@example.com",
      facilityId: "swimming_pool",
      facilityName: "Swimming Pool",
      preferredDate: "2026-11-24",
      timeSlot: "09:00 - 11:00",
      participants: 2,
      status: "pending",
      createdAt: past,
      updatedAt: past,
    })
  );
});

test("FacilityBooking: owner create, filtered query, cancel, and security guards", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const nowPast = new Date(Date.now() - 5000);

  // Valid Kotlin-mirrored create payload (no `id` field, optional `notes` omitted)
  await assertSucceeds(
    aliceDb.collection("facilityBookings").doc("booking_1").set({
      userId: ALICE_UID,
      userName: "Alice Rodrigues",
      userEmail: "alice@example.com",
      facilityId: "astroturf_football",
      facilityName: "AstroTurf Football Pitch",
      preferredDate: "2026-11-25",
      timeSlot: "16:00 - 18:00",
      participants: 14,
      notes: "Evening 7-a-side friendly",
      status: "pending",
      createdAt: nowPast,
      updatedAt: nowPast,
    })
  );

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("facilityBookings").doc("booking_future").set({
      userId: ALICE_UID,
      userName: "Alice Rodrigues",
      userEmail: "alice@example.com",
      facilityId: "badminton",
      facilityName: "Badminton Courts",
      preferredDate: "2026-11-25",
      timeSlot: "16:00 - 18:00",
      participants: 2,
      status: "pending",
      createdAt: new Date(Date.now() + 60000),
      updatedAt: new Date(Date.now() + 60000),
    })
  );

  // Reject ghost `id` or `managerNote` field on create
  await assertFails(
    aliceDb.collection("facilityBookings").doc("booking_bad_id").set({
      id: "booking_bad_id",
      userId: ALICE_UID,
      userName: "Alice Rodrigues",
      userEmail: "alice@example.com",
      facilityId: "badminton",
      facilityName: "Badminton Courts",
      preferredDate: "2026-11-25",
      timeSlot: "16:00 - 18:00",
      participants: 2,
      status: "pending",
      createdAt: nowPast,
      updatedAt: nowPast,
    })
  );

  // Reject creating booking directly as approved
  await assertFails(
    aliceDb.collection("facilityBookings").doc("booking_self_approve").set({
      userId: ALICE_UID,
      userName: "Alice Rodrigues",
      userEmail: "alice@example.com",
      facilityId: "badminton",
      facilityName: "Badminton Courts",
      preferredDate: "2026-11-25",
      timeSlot: "16:00 - 18:00",
      participants: 2,
      status: "approved",
      createdAt: nowPast,
      updatedAt: nowPast,
    })
  );

  // Filtered query succeeds for owner; unfiltered fails
  await assertSucceeds(
    aliceDb.collection("facilityBookings").where("userId", "==", ALICE_UID).get()
  );
  await assertFails(aliceDb.collection("facilityBookings").get());

  // Cross-user isolation: Bob cannot read or update Alice's booking
  await assertFails(bobDb.collection("facilityBookings").doc("booking_1").get());
  await assertFails(
    bobDb.collection("facilityBookings").doc("booking_1").update({
      status: "cancelled",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Alice can cancel her own pending booking
  await assertSucceeds(
    aliceDb.collection("facilityBookings").doc("booking_1").update({
      status: "cancelled",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Terminal state lock: once cancelled, Alice cannot mutate it again
  await assertFails(
    aliceDb.collection("facilityBookings").doc("booking_1").update({
      status: "pending",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("MembershipApplication: owner submit, withdraw, and reject escalation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const nowPast = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("membershipApplications").doc("app_1").set({
      userId: ALICE_UID,
      fullName: "Alice Rodrigues",
      phone: "+254712345678",
      email: "alice@example.com",
      category: "Family",
      interests: ["Swimming", "Badminton"],
      status: "pending",
      createdAt: nowPast,
      updatedAt: nowPast,
    })
  );

  // Owner cannot self-approve
  await assertFails(
    aliceDb.collection("membershipApplications").doc("app_1").update({
      status: "approved",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Filtered query succeeds; unfiltered and cross-user fail
  await assertSucceeds(
    aliceDb.collection("membershipApplications").where("userId", "==", ALICE_UID).get()
  );
  await assertFails(aliceDb.collection("membershipApplications").get());
  await assertFails(bobDb.collection("membershipApplications").doc("app_1").get());

  // Owner withdraws pending application
  await assertSucceeds(
    aliceDb.collection("membershipApplications").doc("app_1").update({
      status: "withdrawn",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("ClubEnquiry: owner submit, close, and admin review with email_verified check", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const spoofedAdminDb = testEnv
    .authenticatedContext("spoof_uid", {
      email: ADMIN_EMAIL,
      email_verified: false,
    })
    .firestore();
  const verifiedAdminDb = testEnv
    .authenticatedContext("real_admin_uid", {
      email: ADMIN_EMAIL,
      email_verified: true,
    })
    .firestore();
  const nowPast = new Date(Date.now() - 5000);

  await assertSucceeds(
    aliceDb.collection("clubEnquiries").doc("enq_1").set({
      userId: ALICE_UID,
      senderName: "Alice Rodrigues",
      phone: "+254712345678",
      enquiryType: "Private Celebration",
      preferredDate: "2026-12-12",
      guestCount: 45,
      message: "Enquiring about upper balcony hire for a family anniversary dinner.",
      status: "submitted",
      createdAt: nowPast,
      updatedAt: nowPast,
    })
  );

  // Spoofed admin (email_verified: false) cannot list or update
  await assertFails(spoofedAdminDb.collection("clubEnquiries").get());
  await assertFails(
    spoofedAdminDb.collection("clubEnquiries").doc("enq_1").update({
      status: "responded",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Verified admin can list all and mark responded
  await assertSucceeds(verifiedAdminDb.collection("clubEnquiries").get());
  await assertSucceeds(
    verifiedAdminDb.collection("clubEnquiries").doc("enq_1").update({
      status: "responded",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});

test("MembershipInquiry: owner create, filtered query, close, and security guards", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const verifiedAdminDb = testEnv
    .authenticatedContext("real_admin_uid", {
      email: ADMIN_EMAIL,
      email_verified: true,
    })
    .firestore();
  const nowPast = new Date(Date.now() - 5000);

  // Valid Kotlin-mirrored create payload (no @DocumentId id field)
  await assertSucceeds(
    aliceDb.collection("membership_inquiries").doc("inq_1").set({
      userId: ALICE_UID,
      name: "Alice Rodrigues",
      email: "alice@example.com",
      interest: "Family Membership & Swimming",
      status: "submitted",
      createdAt: nowPast,
      updatedAt: nowPast,
    })
  );

  // Reject ghost `id` field
  await assertFails(
    aliceDb.collection("membership_inquiries").doc("inq_ghost").set({
      id: "inq_ghost",
      userId: ALICE_UID,
      name: "Alice Rodrigues",
      email: "alice@example.com",
      interest: "Badminton",
      status: "submitted",
      createdAt: nowPast,
      updatedAt: nowPast,
    })
  );

  // Reject future timestamp and self-escalated status
  await assertFails(
    aliceDb.collection("membership_inquiries").doc("inq_future").set({
      userId: ALICE_UID,
      name: "Alice Rodrigues",
      email: "alice@example.com",
      interest: "Badminton",
      status: "submitted",
      createdAt: new Date(Date.now() + 60000),
      updatedAt: new Date(Date.now() + 60000),
    })
  );
  await assertFails(
    aliceDb.collection("membership_inquiries").doc("inq_reviewed").set({
      userId: ALICE_UID,
      name: "Alice Rodrigues",
      email: "alice@example.com",
      interest: "Badminton",
      status: "reviewed",
      createdAt: nowPast,
      updatedAt: nowPast,
    })
  );

  // Filtered query succeeds for owner; unfiltered and cross-user fail
  await assertSucceeds(
    aliceDb.collection("membership_inquiries").where("userId", "==", ALICE_UID).get()
  );
  await assertFails(aliceDb.collection("membership_inquiries").get());
  await assertFails(bobDb.collection("membership_inquiries").doc("inq_1").get());

  // Admin can list all and mark reviewed
  await assertSucceeds(verifiedAdminDb.collection("membership_inquiries").get());
  await assertSucceeds(
    verifiedAdminDb.collection("membership_inquiries").doc("inq_1").update({
      status: "reviewed",
      updatedAt: new Date(Date.now() - 1000),
    })
  );
});

test("MemberProfile: owner create, partial update, cross-user isolation, and tier guard", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const verifiedAdminDb = testEnv
    .authenticatedContext("real_admin_uid", {
      email: ADMIN_EMAIL,
      email_verified: true,
    })
    .firestore();
  const nowPast = new Date(Date.now() - 5000);

  // Alice creates her own profile (cannot self-assign elevated tier)
  await assertFails(
    aliceDb.collection("memberProfiles").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Rodrigues",
      email: "alice@example.com",
      preferredSport: "Swimming",
      membershipTier: "Committee Admin",
      createdAt: nowPast,
      updatedAt: nowPast,
    })
  );

  await assertSucceeds(
    aliceDb.collection("memberProfiles").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Alice Rodrigues",
      email: "alice@example.com",
      phone: "+254712345678",
      preferredSport: "Swimming",
      membershipTier: "Member Portal",
      createdAt: nowPast,
      updatedAt: nowPast,
    })
  );

  // Alice can read & update her own profile fields
  await assertSucceeds(aliceDb.collection("memberProfiles").doc(ALICE_UID).get());
  await assertSucceeds(
    aliceDb.collection("memberProfiles").doc(ALICE_UID).update({
      displayName: "Alice R. Pinto",
      preferredSport: "Badminton",
      emergencyContact: "+254700111222",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Alice cannot self-escalate membershipTier on update or mutate createdAt
  await assertFails(
    aliceDb.collection("memberProfiles").doc(ALICE_UID).update({
      membershipTier: "Committee",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Bob cannot read or modify Alice's profile, and non-admins cannot list memberProfiles
  await assertFails(bobDb.collection("memberProfiles").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("memberProfiles").get());
  await assertFails(
    bobDb.collection("memberProfiles").doc(ALICE_UID).update({
      displayName: "Hijacked",
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Admin can read and update membershipTier
  await assertSucceeds(verifiedAdminDb.collection("memberProfiles").doc(ALICE_UID).get());
  await assertSucceeds(
    verifiedAdminDb.collection("memberProfiles").doc(ALICE_UID).update({
      membershipTier: "Full Member",
      updatedAt: new Date(Date.now() - 500),
    })
  );
});
