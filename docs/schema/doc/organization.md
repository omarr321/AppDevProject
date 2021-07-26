# "organization" Document (organization/_id)

> Description: A company that holds references to its users and schedules. Users can be invited into an organization and be assigned a schedule by a manager.

Global Rules: ```Allow Read/Write for *authenticated* users where 'role=admin' only.```

## Fields

|Field Name |Data Type |Description | Default Value |Security Rules |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|org_name_legal|string|Legal name of the organization (Ex: Vinnin Square Liquors Inc.)|required|Allow Read/Write|
|ein|string: **ein**|Employer Identification Number (EIN) of the legal company|0|Allow Read/Write|
|org_name|string|Name that is displayed to users on application (Ex: Vinnin Liquors)|required|Allow Read/Write|
|**contact**|Map (Object)|An object containing the contact information of this organization.|{...}|Allow Read/Write|
|\| phone|string: **phone**|The phone number of organization|""|^|
|\| address|string|The number and street name of organization|""|^|
|\| city|string|The city that organization resides|""|^|
|\| state|string [enum]|The state that organization resides|""|^|
|\| zip|string:zip|The zip code of the organization|""|^|
|time_created|date/time|Timestamp of when the organization was registered.|required|Allow Read|

## Sub-collections
|Collection Name |Description |Document Schema |
--- | --- | ---
|"schedules"|Stores all created work schedules that belong to an organization. Users are referenced in order to assign shifts.|[organization/schedule_id/_id](/schema/doc/schedule)|
|"members"|Stores references of all users that belong (work) at this organization|[organization/member_id/_id](#members)|
|"groups"|Stores job groups that are assigned to members. This is used to determine shift availability of a particular group. Groups are synonomous with job roles. An example of a group could be **"stock"** or **"cashier"**.|[organization/group_id/_id](#groups)|

## "members" Sub-collection Document (organization/member_id/_id)

> Stores references of all users that belong (work) at this organization. They can be managed by **admins** and assigned to schedules in that sub-collection.

Global Rules: ```Allow Read/Write for *authenticated* users where 'role=admin' only.```

|Field Name |Data Type |Description | Default Value |Security Rules |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|user_id|ref: ```users```|Reference to user that belongs to organization|required|Allow Read|
|time_joined|date/time|Timestamp of when the member was added to organization.|required|Allow Read|
|status|string [enum]|The current status of the member in an organization|"active"|Allow Read/Write|
|roles|Array[string [enum]]|The current role levels that a member can have. Different levels such as 'ORG_ADMIN' can grant permissions various within an organization.|["guest"]|Allow Read/Write
|groups|Array[ref: ```groups```]|The groups that a member belongs to in an organization. Assigning the group to a member makes schedules with that group visible and makes them available|[]|Allow Read/Write

### status enums:
|Enum |Description |
--- | ---
|```active```|Member is currently working for the organization.|
|```hold```|Member's job is currently on hold.|
|```leave```|Member is currently on leave.|
|```terminated```|Member was terminated from organization.|

### role enums:

> NOTE: Because a member can have multiple roles, such permissions are **unioned (OR)** with eachother. Meaning that if **role x has permission A** and **role y has permission A and B**. The combination would be **A and B permissions**.

|Enum |Description |
--- | ---
|```guest```|The placeholder role that allows for a member to spectate the organization and their schedules. Lowest level of permissions are granted for this role.|
|```worker```|Standard role for a non-managerial member. They can be added to a schedule and make general requests for shift preferences and time off. They cannot directly modify the schedules and organization unlike managers.
|```manager```|Standard role for a managerial member. More permissions are enabled within the organization, as they are able to modify certain schedules and their rosters (assigned members).|
|```admin```|Highest permission level for member. Such members with this role has full access to the organization and their schedules. For this doc page, the 'Security Rules' uses refer to the acess a member with this role has.|
