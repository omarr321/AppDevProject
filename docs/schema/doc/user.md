# "user" Document (users/_id)

> Description: Represents the relative information belonging to a user of the service. A user is tied to an account used for Authentication, but the information concerning user Authentication is not related to this document. For more information, refer to [Firebase documentation regarding Authentication](https://firebase.google.com/docs/auth).

Global Rules: ```Allow Read/Write for *authenticated* user only.```

## Fields

|Field Name |Data Type |Description | Default Value |Security Rules |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|auth_id|ref: ```auth```|Reference to user object in Firebase Authentication|required|Allow Read|
|time_created|date/time|Timestamp of when the account was registered.|required|Allow Read|
|time_lastused|date/time|Timestamp of when the account was last accessed on a device.|required|Allow Read|
|name_first|string|First legal name of user|required|Allow Read/Write|
|name_middle|string|Middle legal name of user *(optional)*|""|Allow Read/Write|
|name_last|string|Last legal name of user|required|Allow Read/Write|
|dob|date|The birthday of user|required|Allow Read/Write|
|**phones**|Map (Object)|Contains the different phone numbers a user is associated with.|{...}|Allow Read/Write|
|\| phones.home|string: **phone**|The home phone number of the user|""|^|
|\| phones.work|string: **phone**|The work phone number of the user|""|^|
|\| phones.cell|string: **phone**|The cellpone phone number of the user|""|^|
