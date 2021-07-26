# "schedule" Document (organization/schedule_id/_id)

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
|Events|All the time allocations within a schedule. Most events are shift times and can be usually occuring (Ex: Every Monday 2pm-10pm). An event can be assigned to a member apart of the corresponding group.|[schedule/event_id/_id](#event)

---

# Event

> All the time allocations within a schedule. Most events are shift times and can be usually occuring (Ex: Every Monday 2pm-10pm). An event can be assigned to a member apart of the corresponding group.

## Fields

// TODO: Possibly make 'asignee' a seperate sub-collection

|Field Name |Data Type |Description | Default Value |Security Rules [admin] |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|title|string|Working title of event (name)|"Untitled Event"|Allow Read/Write|
|time_start|date/time|Timestamp of when the event starts.|required|Allow Read/Write|
|time_end|date/time|Timestamp of when the event starts.|required|Allow Read/Write|
|occurence|Array[string [enum: weekday]]|The repetition of the event. (Ex: An event could be "every monday" or "everyday" for consistency.)|[]|Allow Read/Write|
|group|ref: ```groups```|Required group to be allocated. All members apart of the group can be assigned to shift event.|required|Allow Read/Write|
|assignee|Array[Map{...}]|A member can be assigned to a specific occurence of a shift, which is why an object is used to indicate the occurence day.|[]|Allow Read/Write|
|\\ member|ref: ```members```|The member assigned to the shift occurence|required|^|
|\\ occurence|date/time|The specific instance of the occurence a worker is assigned to|required|^|

---

### "weekday" enums

```
"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday", "all"
```
