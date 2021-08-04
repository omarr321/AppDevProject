# "organization" Document (organizations/_id)

> Description: A company that holds references to its users and schedules. Users can be invited into an organization and be assigned a schedule by a manager.

Global Rules:

\ ```Allow Read/Write for *authenticated* users where 'role=admin' only.```

\ ```Allow Read for *authenticated* users```


## Fields

|Field Name |Data Type |Description | Default Value |Security Rules [admin] |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|org_name_legal|string|Legal name of the organization (Ex: Vinnin Square Liquors Inc.)|required|Allow Read/Write|
|ein|string: **ein**|Employer Identification Number (EIN) of the legal company|0|Allow Read/Write|
|org_name|string|Name that is displayed to users on application (Ex: Vinnin Liquors)|required|Allow Read/Write|
|**contact**|Map{...} (Object)|An object containing the contact information of this organization.|{...}|Allow Read/Write|
|\\ phone|string: **phone**|The phone number of organization|""|^|
|\\ address|string|The number and street name of organization|""|^|
|\\ city|string|The city that organization resides|""|^|
|\\ state|string [enum]|The state that organization resides|""|^|
|\\ zip|string:zip|The zip code of the organization|""|^|
|time_created|date/time|Timestamp of when the organization was registered.|required|Allow Read|

## Sub-collections
|Collection Name |Description |Document Schema |
--- | --- | ---
|"schedules"|**A sub-collection of organization.** Stores all created work schedules that belong to an organization. Users are referenced in order to assign shifts.|[organization_id/schedules/_id](/schema/doc/schedule)|
|"members"|**A sub-collection of organization.** Stores references of all users that belong (work) at this organization.|[organization_id/members/_id](/schema/doc/member)|
|"groups"|**A sub-collection of organization.** Stores job groups that are assigned to members. This is used to determine shift availability of a particular group. Groups are synonymous with job roles. An example of a group could be "stock" or "cashier".|[organization_id/groups/_id](/schema/doc/group)