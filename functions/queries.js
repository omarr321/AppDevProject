const admin = require("firebase-admin");
const db = admin.firestore();

exports.getUserDoc = async (uid) => {
    return await db.collection('users').where('auth_id', '==', uid).get();
}

exports.getUsersShifts = async (userRef, callback, time_start, time_end) => {
    return (await db.collectionGroup("shifts")
        .where('assignee', '==', userRef)
        .where('time_start', '>=', time_start)
        .where('time_start', '<=', time_end)
        .get()).forEach(callback);
}
