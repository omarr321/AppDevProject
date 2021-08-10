const functions = require("firebase-functions");
const admin = require("firebase-admin");

// Initialize Firebase Functions
admin.initializeApp()
const db = admin.firestore();

// Import helper methods
const {getUserDoc, getUsersShifts, getMemberDocs} = require("./queries");

const TIME_HOUR = 1000*60*60;
const TIME_DAY = TIME_HOUR*24;
const TIME_WEEK = TIME_DAY*7;

const MESSAGE_INTERNAL = "Something weird has occurred on our end! Please contact the developers to report a bug."

const verifyUid = (context) => {
    if (!context.auth) {
        throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
            'while authenticated.')
    }
    return context.auth.uid;
}

const getUser = async (uid) => {
    const user = await getUserDoc(uid);
    if (user.empty) {
        throw new functions.https.HttpsError('not-found', "The user is not registered in the database. Please contact customer service to resolve this issue.")
    }
    return user.docs[0].ref;
}

const getUserMemberDocs = async (uid, callback=(members) => {}) => {
    return await getMemberDocs(await getUser(uid));
}

const getMemberFromOrgDoc = async (uid, organization_id) => {
    let member = null;
    const members = (await getUserMemberDocs(uid)).docs;

    // Check each member doc to match organizations
    for(let m in members) {
        let org = members[m].ref.parent.parent;
        if (org.id == organization_id) {
            member = members[m].ref;
        }
    }

    // Return if user is not apart of organization
    if (member == null) {
        throw new functions.https.HttpsError("not-found", "The user does not belong to the provided organization.")
    }

    return member;
}

const getUserShifts = async (uid, callback=(shift) => {}, time_start=new Date(), time_end = new Date(Date.now() + TIME_WEEK)) => {
    // TODO: Support optional references
    console.log(time_start)
    console.log(time_end)
    const user = await getUser(uid);
    await getUsersShifts(user, callback, time_start, time_end);
}

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

exports.hoursAccumulated = functions.https.onCall(( async (data, context) => {

    const uid = verifyUid(context);

    let {time_start, time_end} = data;
    time_start = new Date(time_start);
    time_end = new Date(time_end);

    try {
        let sum = 0;

        await getUserShifts(uid, (shift) => {
            const data = shift.data();
            sum = sum + new Date(data.time_end - data.time_start).getTime() / (60*60);
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

exports.punch = functions.https.onCall((async (data, context) => {

    const {organization_id, message = ""} = data;
    const uid = verifyUid(context);

    // Validators
    if (organization_id == null || organization_id.length <= 0) {
        throw new functions.https.HttpsError("invalid-argument", "The function 'organization_id' is required but not provided.");
    }

    try {

        const member = await getMemberFromOrgDoc(uid, organization_id);

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

        const member = await getMemberFromOrgDoc(uid, organization_id);

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

exports.requestVacation = functions.https.onCall(((data, context) => {
    return "Work in progress..."
}))

exports.requestCover = functions.https.onCall(((data, context) => {
    return "Work in progress..."
}))
