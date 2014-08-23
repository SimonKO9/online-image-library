Website routes
==============

GET /
-----------
Home page route. Serves index.html.


GET /public/*
-------------
Serves static resources located under /public/* directory from resources.


GET /webjars/*
--------------
Serves webjars' resources.


API routes
==========

GET /api/image/{key}
--------------------
Get image resource by key.

Successful http entity:
Content-Type: image/jpeg or image/png or image/gif
Status code: 200 OK

Error http entity:
404 Not Found if key not found


POST /api/image
---------------
Upload image resource by key.
Accepted content types: multipart/form-data. Request must contain image part with Content-Disposition header describing file.
Uploaded file must have *.jpg, *.gif or *.png extension.
Maximum file size is 2MB.

Successful http entity:
Status code: 200 OK
Content-Type: image/jpeg or image/png or image/gif
Location: path to created resource

Error:
Status code: 400 Bad Request
Payload: json describing problem { "reason": "..." }

