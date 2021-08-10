const functions = require("firebase-functions");
const admin = require("firebase-admin");

// Initialize Firebase Functions
admin.initializeApp()
const db = admin.firestore();

const TIME_HOUR = 1000 * 60 * 60;
const TIME_DAY = TIME_HOUR * 24;
const TIME_WEEK = TIME_DAY * 7;

const MESSAGE_INTERNAL = "Something weird has occurred on our end! Please contact the developers to report a bug."

const {
    verifyUid,
    getUserShifts,
    getMemberFromOrgDoc,
    getShift,
    getUserMemberDocs
} = require("./helpers");

exports.createUser = functions.auth.user().onCreate(async (user) => {
    try {
        const userDoc = await db.collection('users').add({
            auth_id: user.uid,
            time_created: new Date(Date.now()),
            time_lastused: new Date(Date.now()),
            // TODO: Change back to proper reference
            name_first: user.displayName,
            name_middle: "",
            name_last: "",
            dob: new Date(0),
            phones: {
                home: "",
                work: "",
                cell: ""
            }
        });
        return `User for ${user.email} has been created with id=${userDoc.id}.`
    } catch (e) {
        console.error(e)
    }
})

exports.shifts = functions.https.onCall((async (data, context) => {

    const uid = verifyUid(context);

    // TODO: Make time validation modular
    // TODO: Validate time so time_start < time_end
    let {time_start, time_end} = data;
    time_start = new Date(time_start);
    time_end = new Date(time_end);

    try {
        let results = [];

        await getUserShifts(uid, (shift) => {
            results = results.concat(shift.data());
        }, time_start, time_end);

        return results;
    } catch (e) {
        if (e instanceof functions.https.HttpsError) {
            throw e;
        } else {
            console.error(e);
            throw new functions.https.HttpsError('internal', MESSAGE_INTERNAL);
        }
    }
}))

exports.hoursAccumulated = functions.https.onCall((async (data, context) => {

    const uid = verifyUid(context);

    let {time_start, time_end} = data;
    time_start = new Date(time_start);
    time_end = new Date(time_end);

    try {
        let sum = 0;

        await getUserShifts(uid, (shift) => {
            const data = shift.data();
            sum = sum + new Date(data.time_end - data.time_start).getTime() / (60 * 60);
        }, time_start, time_end);

        return {total_hours: sum};
    } catch (e) {
        if (e instanceof functions.https.HttpsError) {
            throw e;
        } else {
            console.error(e);
            throw new functions.https.HttpsError('internal', MESSAGE_INTERNAL);
        }
    }
}))

exports.getOrganizations = functions.https.onCall((async (data, context) => {

    // context.auth = {
    //     uid: "V8KoG7x5BhanVcENuzWujolzvoE2"
    // }

    const uid = verifyUid(context);

    // Get all member docs attached to user
    let results = [];
    const {members} = await getUserMemberDocs(uid);

    for (let m in members) {
        results = results.concat({
            org: (await members[m].ref.parent.parent.get()).data(),
            member: (await members[m].ref.get()).data()
        })
    }

    return results;

}))

exports.punch = functions.https.onCall((async (data, context) => {

    const {organization_id, message = ""} = data;
    const uid = verifyUid(context);

    // Validators
    if (organization_id == null || organization_id.length <= 0) {
        throw new functions.https.HttpsError("invalid-argument", "The function 'organization_id' is required but not provided.");
    }

    try {

        const {member} = await getMemberFromOrgDoc(uid, organization_id);

        // Punch the user depending on current status
        const status = (await member.get()).data().status;
        if (status == "active") {
            await member.update({status: "working"})
            return {isWorking: true}
        } else if (status == "working" || status == "break") {
            await member.update({status: "active"})
            return {isWorking: false}
        } else {
            throw new functions.https.HttpsError("permission-denied", "The user must be active at the organization.")
        }

        // Non-retrievable return!
        return "I'm still standing, yeah yeah yeah!";

    } catch (e) {
        if (e instanceof functions.https.HttpsError) {
            throw e;
        } else {
            console.error(e);
            throw new functions.https.HttpsError('internal', MESSAGE_INTERNAL);
        }
    }
}))

exports.break = functions.https.onCall((async (data, context) => {

    const {organization_id, message = ""} = data;
    const uid = verifyUid(context);

    // Validators
    if (organization_id == null || organization_id.length <= 0) {
        throw new functions.https.HttpsError("invalid-argument", "The function 'organization_id' is required but not provided.");
    }

    try {

        const {member} = await getMemberFromOrgDoc(uid, organization_id);

        // Break only if user is currently working
        const status = (await member.get()).data().status;
        if (status == "working") {
            await member.update({status: "break"})
            return {status: "break"}
        } else if (status == "break") {
            await member.update({status: "working"})
            return {status: "working"}
        } else {
            throw new functions.https.HttpsError("permission-denied", "The user must be currently working.")
        }

        // Non-retrievable return!
        return "I'm still standing, yeah yeah yeah!";

    } catch (e) {
        if (e instanceof functions.https.HttpsError) {
            throw e;
        } else {
            console.error(e);
            throw new functions.https.HttpsError('internal', MESSAGE_INTERNAL);
        }
    }
}))

exports.requestVacation = functions.https.onCall((async (data, context) => {

    const {organization_id, time_start, time_end, message = ""} = data;
    const uid = verifyUid(context);

    // Validators
    let missing_args = []

    if (organization_id == null){
        missing_args = missing_args.concat("organization_id");
    }
    if (time_start == null){
        missing_args = missing_args.concat("time_start");
    }
    if (time_end == null){
        missing_args = missing_args.concat("time_end");
    }
    if (missing_args.length > 0){
        throw new functions.https.HttpsError("invalid-argument", `The parameters [${missing_args.toString()}] are required but not provided.`);
    }

    try {
        // Get organization of member
        const {member} = await getMemberFromOrgDoc(uid, organization_id);
        const org = member.parent.parent;

        const payload = {
            time_submitted: new Date(Date.now()),
            sender: member,
            request_type: "vacation",
            time_start: time_start,
            time_end: time_end,
            message: message
        }

        // Add a 'vacation' request to the requests collection;
        const request = await org.collection("requests").add(payload)

        return {
            successful: true,
            payload: payload
        };

    } catch (e) {
        if (e instanceof functions.https.HttpsError) {
            throw e;
        } else {
            console.error(e);
            throw new functions.https.HttpsError('internal', MESSAGE_INTERNAL);
        }
    }
}))

exports.requestCover = functions.https.onCall((async (data, context) => {

    const {shift_uuid, message = ""} = data;
    const uid = verifyUid(context);

    // Validators
    let missing_args = []

    if (shift_uuid == null){
        missing_args = missing_args.concat("shift_uuid");
    }
    if (missing_args.length > 0){
        throw new functions.https.HttpsError("invalid-argument", `The parameters [${missing_args.toString()}] are required but not provided.`);
    }

    try {
        // Get shift provided
        const shift = await getShift(shift_uuid);

        const org = shift.parent.parent.parent.parent;

        // Validate that shift is already assigned to user
        const shiftSnap = await shift.get();
        const {user, member} = await getMemberFromOrgDoc(uid, org.id);
        if(!user.isEqual(shiftSnap.data().assignee)) {
            throw new functions.https.HttpsError("permission-denied", "The user is currently not assigned that shift.");
        }

        const payload = {
            time_submitted: new Date(Date.now()),
            sender: member,
            request_type: "cover",
            shift: shift,
            message: message
        }

        // Add a 'cover' request to the requests collection;
        const request = await org.collection("requests").add(payload)

        return {
            successful: true,
            payload: payload
        };

    } catch (e) {
        if (e instanceof functions.https.HttpsError) {
            throw e;
        } else {
            console.error(e);
            throw new functions.https.HttpsError('internal', MESSAGE_INTERNAL);
        }
    }
}))
