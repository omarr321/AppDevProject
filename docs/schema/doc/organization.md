# "organization" Document (organization/_id)

> Description: A company that holds references to its users and schedules. Users can be invited into an organization and be assigned a schedule by a manager.

Global Rules: ```Allow Read/Write for *authenticated* users where 'ROLE=ORG_ADMIN' only.```

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
|\| state|string|The state that organization resides|""|^|
|\| zip|string:zip|The zip code of the organization|""|^|

## Sub-collections
