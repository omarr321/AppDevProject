const functions = require("firebase-functions");
const {getUserDoc, getMemberDocs, getUsersShifts} = require("./queries");

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

const getUserMemberDocs = async (uid, callback = (members) => {
}) => {
    const user = await getUser(uid)
    const members = (await getMemberDocs(user)).docs;
    return {user, members}
}

const getMemberFromOrgDoc = async (uid, organization) => {
    let member = null;
    const {user, members} = await getUserMemberDocs(uid);

    // Check each member doc to match organizations
    for (let m in members) {
        let org = members[m].ref.parent.parent;
        if (org.id == organization_id) {
            member = members[m].ref;
        }
    }

    // Return if user is not apart of organization
    if (member == null) {
        throw new functions.https.HttpsError("not-found", "The user does not belong to the provided organization.")
    }

    return {user, member};
}

const getUserShifts = async (uid, callback = (shift) => {
}, time_start = new Date(), time_end = new Date(Date.now() + TIME_WEEK)) => {
    // TODO: Support optional references
    console.log(time_start)
    console.log(time_end)
    const user = await getUser(uid);
    await getUsersShifts(user, callback, time_start, time_end);
}

module.exports = {
    verifyUid,
    getUser,
    getUserMemberDocs,
    getMemberFromOrgDoc,
    getUserShifts
}
