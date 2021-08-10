const admin = require("firebase-admin");
const db = admin.firestore();

exports.getUserDoc = async (uid) => {
    return await db.collection('users').where('auth_id', '==', uid).get();
}

exports.getMemberDocs = async (userRef) => {
    return await db.collectionGroup('members').where('user_id', '==', userRef).get();
}

exports.getUsersShifts = async (userRef, callback, time_start, time_end) => {
    return (await db.collectionGroup("shifts")
        .where('assignee', '==', userRef)
        .where('time_start', '>=', time_start)
        .where('time_start', '<=', time_end)
        .orderBy("time_start", "asc")
        .get()).forEach(callback);
}

exports.getShiftDocs = async (shiftUUID) => {
    return await db.collectionGroup('shifts').where('uuid', '==', shiftUUID).get();
}
