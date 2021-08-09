# "group" Document (organization_id/groups/_id)

> Also known as roles/occupations within an organization. Events in a schedule use groups to assign workers when needed. Groups are dynamic entries that are customizable by the user. For example, a grocery store may have groups such as "stock", "cashier", or "assistant-manager".

Global Rules: ```Allow Read/Write for *authenticated* users where 'role=admin' only.```

|Field Name |Data Type |Description | Default Value |Security Rules [admin] |
--- | --- | --- | --- | ---
|_id|string|Document ID used to reference this|\<uuid value\>|Allow Read|
|time_created|date/time|Timestamp of when the group was created.|required|Allow Read|
|time_lastupdated|date/time|Timestamp of when the group was last updated|required|Allow Read|
|title|string|The displayable name of the group. Ex: "stock"|"Untitled Group"|Allow Read/Write|
|description|string|Description of the group.|""|Allow Read/Write|
|color|RBG Array [r **0-255**, g, b]|The color code that represents the group. Can be used to show shifts in schedules as that color.|<span style="color:#478c5c">[71, 140, 92]</span>|Allow Read/Write|

---