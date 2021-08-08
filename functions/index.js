const functions = require("firebase-functions");

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

exports.shifts = functions.https.onCall(((data, context) => {
    return "Work in progress..."
}))

exports.hoursAccumulated = functions.https.onCall(((data, context) => {
    return "Work in progress..."
}))

exports.punch = functions.https.onCall(((data, context) => {
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
