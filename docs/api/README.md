# API Calls

> All available functions the client can call using [Firebase Function Calls](https://firebase.google.com/docs/functions/callable).

---

## <u>function</u> : shifts
> Returns an array of shifts assigned to the user.

Security Rule: ```*authenticated* users.```

|param|type|description|
--- | --- | ---
|_auth*|context|The auth token of the user.|
|organization_id|ref: ```organization```|Optional filter to narrow down upcoming shifts by organization.|
|schedule_id|ref: ```schedule```|Optional filter to narrow down upcoming shifts by schedule in organization.|
|time_start|date/time|Date to list shifts from. If not provided, the current date will be used.|
|time_end|date/time|Date to stop at. If not provided, the current date + 7 days will be used.|

* Note: If both **organization_id** and **schedule_id** are provided, the **<u>schedule_id</u>** will be used in favor.

### Return result (successful):

response format: ```JSON```
```
{
    "shifts": [
        {
            "title": string,
            "description": string,
            "time_start": date/time,
            "time_end": date/time,
            "group_title" string,
            "group_color" RGB Array [r 0-255, g, b]
        }, ...
    ]
}
```
## <u>function</u> : hoursAccumulated
> Returns the aggregated sum of hours based off assigned shifts. Format of call is similar to ```shifts```.

Security Rule: ```*authenticated* users.```

|param|type|description|
--- | --- | ---
|_auth*|context|The auth token of the user.|
|organization_id|ref: ```organization```|Optional filter to narrow down sum of upcoming shifts by organization.|
|schedule_id|ref: ```schedule```|Optional filter to narrow down sum of upcoming shifts by schedule in organization.|
|time_start|date/time|Date to aggregate shifts from. If not provided, the current date will be used.|
|time_end|date/time|Date to stop at. If not provided, the current date + 7 days will be used.|

* Note: If both **organization_id** and **schedule_id** are provided, the **<u>schedule_id</u>** will be used in favor.

### Return result (successful):

response format: ```JSON```
```
{
    "total_hours": number
}
```

## <u>function</u> : punch
> Worker client makes call when they punch in or out of a shift.

Security Rule: ```*authenticated* users who belong to the organization.```

|param|type|description|
--- | --- | ---
|_auth*|context|The auth token that is already embedded into the request. Represents the user who will be punched in.|
|organization_id*|ref: ```organization```|The organization that the user wishes to punch into.|
|message|string|An optional message the worker can leave. May be useful to provide logs, such as excuses for tardiness.

## <u>function</u> : break
> Worker client makes call when they go on or off break. Only successful if worker is punched in after calling ```punch```.

Security Rule: ```*authenticated* users who belong to the organization.```

|param|type|description|
--- | --- | ---
|_auth*|context|The auth token that is already embedded into the request. Represents the user who will go on/off break.|
|organization_id*|ref: ```organization```|The organization that user is currently working at.|
|message|string|An optional message the worker can leave. May be useful to provide reason for break.

## <u>function</u> : requestVacation
> Worker client makes call when they request vacation time or time off. 

Security Rule: ```*authenticated* users who belong to the organization.```

|param|type|description|
--- | --- | ---
|_auth*|context|The auth token that is already embedded into the request. Represents the user requesting vacation time.|
|organization_id*|ref: ```organization```|The organization that the user is requesting vacation time from. The request document will be added to the organization's request sub-collection.|
|time_start*|date/time|Date the vacation request starts.|
|time_end*|date/time|Date the vacation request ends.|
|message|string|An optional message the worker can leave regarding the request.|


* <span style="color:#FF0000"><u>Validation Note:</u> The difference between [time_end - time_start] must fall within the organization's vacation time policy.</span>

## <u>function</u> : requestCover
> Worker client makes call, requesting that another worker covers a specific shift.

Security Rule: ```*authenticated* users who belong to the organization and are currently assigned to the referenced shift.```

|param|type|description|
--- | --- | ---
|_auth*|context|The auth token that is already embedded into the request. Represents the user requesting vacation time.|
|shift_id*|ref: ```shift```|The shift that the worker is requesting a cover for. The request document will be added to the organization's request sub-collection.|
|message|string|An optional message the worker can leave regarding the request.|

* <span style="color:#FF0000"><u>Validation Note:</u> Worker must already be assigned to the shift.</span>
