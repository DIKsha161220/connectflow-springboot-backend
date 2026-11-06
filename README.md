# ConnectFlow CRM backend

Spring Boot 4 / Java 17 REST API for the existing `connectflow_crm` PostgreSQL database.
The application maps the eight guide tables and validates their shape at startup; it does
not create tables, run seed scripts, or change credentials.

## Database configuration

`application.properties` imports the existing local database settings from
`D:\backendd\connectflow-backend\src\main\resources\application.properties`. Credentials
are not copied into this project. JPA is explicitly configured with
`spring.jpa.hibernate.ddl-auto=validate` and SQL initialization is disabled. To use a
different external configuration on another machine, override `SPRING_CONFIG_IMPORT` or
set the standard `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and
`SPRING_DATASOURCE_PASSWORD` environment variables.

Set `JWT_SECRET` to a Base64-encoded random key of at least 32 bytes before starting the
application. Do not commit the value.

## Run on Windows

From `D:\CRM-BACKEND12`:

```powershell
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$bytes = New-Object byte[] 32
$rng.GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
$rng.Dispose()
.\mvnw.cmd spring-boot:run
```

The command above creates a session-only key; existing access tokens will stop working
when a different key is used. Set a stable secret in your user environment for normal use.
Run tests with `.\mvnw.cmd test` and build with `.\mvnw.cmd package`.

The React app can use `http://localhost:8080/api` as its Axios base URL. Configure
additional allowed frontend origins with `FRONTEND_ORIGINS` as a comma-separated list.

## Access rules

* Administrative: all institute and CRM access; may create institutes.
* Calling Team: assigned institutes and their contacts; may create calls and read call
  history for assigned institutes; no visit or follow-up access.
* On-field Employee: assigned institutes and their contacts; may create calls and visits
  and change visit status; may not read call history, edit/delete calls, or change
  institute assignments.
* Follow-ups, employees, roles, audit logs, and dashboard: Administrative only.

Calls, visits, follow-ups, and institute assignment ownership are derived from the
authenticated employee, not a client-supplied employee ID. Employee passwords are stored
as BCrypt hashes. Existing database records are not seeded or changed by startup.
