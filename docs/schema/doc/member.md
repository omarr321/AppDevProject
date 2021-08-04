# "member" Document (organization_id/members/_id)

> Stores references of all users that belong (work) at this organization. They can be managed by **admins** and assigned to schedules in that sub-collection.

Global Rules: ```Allow Read/Write for *authenticated* users where 'role=admin' only.```

|Field Name |Data Type |Description | Default Value |Security Rules [admin] |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|user_id|ref: ```users```|Reference to user that belongs to organization|required|Allow Read|
|time_joined|date/time|Timestamp of when the member was added to organization.|required|Allow Read|
|status|string [[enum]](#status-enums)|The current status of the member in an organization|"active"|Allow Read/Write|
|roles|Array[string [enum]]|The current role levels that a member can have. Different levels such as 'ORG_ADMIN' can grant permissions various within an organization.|["guest"]|Allow Read/Write
|groups|Array[ref: ```groups```]|The groups that a member belongs to in an organization. Assigning the group to a member makes schedules with that group visible and makes them available|[]|Allow Read/Write
|**pay**|Map{...} (Object)|Contains information to calculate payment stubs.|{...}|Allow Read/Write|
|\\ type|string [[enum]](#paytype-enums)|The recurrance of how a worker is paid. |""|^|
|\\ amount|number|the rate a worker is paid depending on it's type. |0.00|^|
|\\ occurrence|string [[enum]](#occurence-enums)|Required if ```pay.type == "salary"```. The frequency at which a worker's salary is paid.|"yearly"|^|

---

### status enums:
|Enum |Description |
--- | ---
|```active```|Member is currently working for the organization.|
|```hold```|Member's job is currently on hold.|
|```leave```|Member is currently on leave.|
|```terminated```|Member was terminated from organization.|

### pay.type enums:
|Enum |Description |
--- | ---
|```hourly```|Worker is paid at an hourly rate. Calculating total pay should take into account the sum aggregation of all shifts a worker is assigned to.|
|```salary```|Worker is paid at a fixed amount over a period of time, regardless of their shift schedule.|

### role enums:

> NOTE: Because a member can have multiple roles, such permissions are **unioned (OR)** with eachother. Meaning that if **role x has permission A** and **role y has permission A and B**. The combination would be **A and B permissions**.

|Enum |Description |
--- | ---
|```guest```|The placeholder role that allows for a member to spectate the organization and their schedules. Lowest level of permissions are granted for this role.|
|```worker```|Standard role for a non-managerial member. They can be added to a schedule and make general requests for shift preferences and time off. They cannot directly modify the schedules and organization unlike managers.
|```manager```|Standard role for a managerial member. More permissions are enabled within the organization, as they are able to modify certain schedules and their rosters (assigned members).|
|```owner```|Special role that inherits privilages to "manager".|
|```admin```|Highest permission level for member. Such members with this role has full access to the organization and their schedules. For this doc page, the 'Security Rules' uses refer to the acess a member with this role has.|

### occurence enums

> Worker will recieve their salary based on the frequency selected below.

```
"daily", "weekly", "bi-weekly", "monthly", "quarterly", "yearly"
```

- Yes, people can get paid daily. Just ask omar!
