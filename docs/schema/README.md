# Database Schema

> Documents the database format of the API. Collections, documents, and fields can be accessed directly using the [Firestore API](https://firebase.google.com/docs/firestore/) with a ruleset. Note that the use of Firestore does not replace all application functionality but to provide getter methods for data in an efficient way.

## Collections
This list contains the available collections included in the Firestore. Collections can hold multiple documents in itself. For example, many user documents may belong to a "users" collection for a specific service.

While some actions to the database are only possible through API calls, as enforced by the rulset, such documents can be directly requested through the Firestore API with the correct permissions. Refer to Document Schema references for permission rules to determine the accessibility of the database.

[Firestore Documentation](https://firebase.google.com/docs/firestore/data-model) on Collections & Documents.

|Collection Name |Description |Document Schema |
--- | --- | ---
|"users"|Stores the information of each user within the service.|[users/_id](/schema/doc/user)|
|"organization"|Stores information about an organization's users, schedules, and policies.|[organization/_id](/schema/doc/organization)|
|\ "schedules"|**A sub-collection of organization.** Stores all created work schedules that belong to an organization. Users are referenced in order to assign shifts.|[organization/schedule_id/_id](/schema/doc/schedule)|
