# XIS CRM Example

Demo CRM for XIS Boot.

It intentionally shows several framework features in one compact application:

- local login through `xis-authentication`, backed by the CRM database
- role-based employee administration
- customer list and customer detail page
- modal forms for customer data, contact history and scheduled follow-ups
- current reminders as a refreshable frontlet with push updates for online users
- H2 file database with seed data

Run it with:

```bash
./gradlew xisRun
```

Open:

```text
http://localhost:8080/customers.html
```

Demo users:

| User | Password | Role |
| --- | --- | --- |
| `admin` | `admin` | Admin |
| `mara` | `demo` | Sales |
| `tom` | `demo` | Sales |

The H2 database is created under `./data/crm.mv.db`.
