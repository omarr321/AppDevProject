const functions = require("firebase-functions");
const admin = require("firebase-admin");

// Initialize Firebase Functions
admin.initializeApp()
const db = admin.firestore();

// Import helper methods
const {getUserDoc, getUsersShifts} = require("./queries");

const TIME_HOUR = 1000*60*60;
const TIME_DAY = TIME_HOUR*24;
const TIME_WEEK = TIME_DAY*7;

const MESSAGE_INTERNAL = "Something weird has occurred on our end! Please contact the developers to report a bug."

const getShifts = async (uid, callback=(shift) => {}, time_start=new Date(), time_end = new Date(Date.now() + TIME_WEEK)) => {
    // TODO: Support optional references
    const user = await getUserDoc(uid);
    if (user.empty) {
        throw new functions.https.HttpsError('not-found', "The user is not registered in the database. Please contact customer service to resolve this issue.")
    }
    await getUsersShifts(user.docs[0].ref, callback, time_start, time_end)
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

// TODO: Add 11:59 to time_end
exports.shifts = functions.https.onCall((async (data, context) => {
    if (!context.auth) {
        throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
            'while authenticated.')
    }
    const uid = context.auth.uid;

    try {
        let results = [];

        await getShifts(uid, (shift) => {
            results = results.concat(shift.data());
        });

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
    // TODO: Support optional references
    if (!context.auth) {
        throw new functions.https.HttpsError('failed-precondition', 'The function must be called ' +
            'while authenticated.')
    }
    const uid = context.auth.uid;

    try {
        let sum = 0;

        await getShifts(uid, (shift) => {
            const data = shift.data();
            sum = sum + new Date(data.time_end - data.time_start).getTime() / (60*60);
        });

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
    return "Work in progress..."
}))

exports.break = functions.https.onCall(((data, context) => {
    return "Work in progress..."
}))

exports.requestVacation = functions.https.onCall(((data, context) => {
    return "Work in progress..."
}))

exports.requestCover = functions.https.onCall(((data, context) => {
    return "Work in progress..."
}))
