# "schedule" Document (organization_id/schedules/_id)

> Description: A shift schedule that belongs in an organization. Because an organization can have multiple schedules, they can be configured by the mangers to allocate various groups to shifts.

Global Rules:

\ ```Allow Read/Write for *authenticated* users where 'role=admin' only.```

\ ```Allow Read for *authenticated* users```

## Fields

|Field Name |Data Type |Description | Default Value |Security Rules [admin] |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|title|string|Working title of the schedule (name)|"Untitled Schedule"|Allow Read/Write|
|description|string|The printable description of the schedule.|"A new schedule."|Allow Read/Write|
|time_created|date/time|Timestamp of when the schedule was created.|required|Allow Read|
|time_lastupdated|date/time|Timestamp of when the account was last updated by a manager|required|Allow Read|

## Sub-collections
|Collection Name |Description |Document Schema |
--- | --- | ---
|"routines"|All the time allocations within a schedule. Most routines frequently occur and are designed to make the scheduling process easier. (Ex: Every Monday 2pm-10pm). A shift instance belonging to the routing can be assigned to a member apart of the corresponding group.|[.../schedule_id/routines/_id](#routine)
|"schedule_members"|Collection of member references that belong to the schedule. If part of the collection, a member can view the schedule and be eligible for being assigned shifts.|[.../schedule_id/schedule_members/_id](#schedule_member)|
|"shifts"|A single instance that may follow a routine. Members with the correct group can be assigned to a shift.|[.../schedule_id/shifts/_id](#shifts)|
---

# routine:

> All the time allocations within a schedule. Most routines frequently occur and are designed to make the scheduling process easier. (Ex: Every Monday 2pm-10pm). A shift instance belonging to the routing can be assigned to a member apart of the corresponding group.

## Fields

|Field Name |Data Type |Description | Default Value |Security Rules [admin] |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|title|string|Working title of routine (name)|"Untitled Routine"|Allow Read/Write|
|description|string|Description of routine|""|Allow Read/Write|
|time_start|date/time|Timestamp of when the routine starts.|required|Allow Read/Write|
|time_end|date/time|Timestamp of when the routine ends.|required|Allow Read/Write|
|occurrence|Array[string [[enum]](#weekday-enums)|The repetition of the routine. (Ex: A routine could be "every monday" or "everyday" for consistency.)|[]|Allow Read/Write|
|group|ref: ```groups```|Required group to be allocated. All members apart of the group can be assigned to shifts referencing the routine.|required|Allow Read/Write|

### weekday enums:

```
"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "all"
```

---

# shifts:

> A single instance that may follow a routine. Members with the correct group can be assigned to a shift.

## Fields
|Field Name |Data Type |Description | Default Value |Security Rules [admin] |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|routine_id|ref: ```routines```|An optional reference to a routine. If not null, the values of time inherit it's referenced routine.|null|Allow Read/Write|
|title|string|Working title of shift (name). **Inherits from routine if null**|"Untitled Shift"|Allow Read/Write|
|description|string|Description of shift. **Inherits from routine if null**|""|Allow Read/Write|
|time_start|date/time|Timestamp of when the shift starts. This value can **inherit the referenced routine's date**, taking account the occurrence. Therefore, the timestamp represents the true date/time of that shift.|required|Allow Read/Write|
|time_end|date/time|Timestamp of when the shift ends. This should be relative to the time_start field of this document, falling within or near the same day.|required|Allow Read/Write|
|group|ref: ```groups```|Group to be allocated. **Inherits referenced routine if value is null**.|null|Allow Read/Write|
|assignee|ref: ```members```|The member to be assigned the shift. When created, a shift may not yet be assigned. A core feature of this application would assign members to shifts either automatically with an algorithm, or manually through the admin.|null|Allow Read/Write|
---

# schedule_member:

> Collection of member references that belong to the schedule. If part of the collection, a member can view the schedule and be eligible for being assigned shifts.

## Fields
|Field Name |Data Type |Description | Default Value |Security Rules [admin] |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|member_id|ref: ```members```|The reference that backtracks to the member. This should refer to a member within the parent organization.|required|Allow Read/Write|
|visible|boolean|Determines if the schedule is visible to the member. If set to false, the member can still be assigned shifts.|true|Allow Read/Write|