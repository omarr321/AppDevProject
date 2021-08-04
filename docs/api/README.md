# API Calls

> Hello, World!

---

## PUT /punch
> Worker client makes call when they punch in or out of a shift.

Security Rule: ```*authenticated* users who belong to the organization that holds the schedule_id.```

|param|type|description|
--- | --- | ---
|_auth|context|The auth token that is already embedded into the request. Represents the user who will be punched in.|
|schedule_id|ref: ```schedule```|The schedule that contains the users shift to be punched into. A user can only punch in successfully if the time falls within their assigned shift on the schedule.|
|message|string|An optional message the user can leave. May be useful to provide logs of work, such as excuses for tardiness.

## PUT /request_vacation
> Worker client makes call when they request vacation time. 

Security Rule: ```*authenticated* users who belong to the organization that holds the schedule_id.```

|param|type|description|
--- | --- | ---