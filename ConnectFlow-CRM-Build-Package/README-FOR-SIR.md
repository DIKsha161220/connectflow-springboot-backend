# ConnectFlow CRM — Enterprise Workspace & System Architecture

## 1. Project Overview
ConnectFlow CRM is a full-featured Customer Relationship Management solution designed for educational institutes, corporate outreach, telecalling, and field sales tracking. 
The system features both:
- **Standalone High-Performance Windows Desktop Application**: Offline-capable, zero-dependency, local persistent database, and responsive dual-column dashboard.
- **Spring Boot Enterprise Backend**: REST API with JWT authentication, PostgreSQL relational database mapping, BCrypt security hashing, and role-based access control.

---

## 2. Key Modules & Features

### A. Persistent Navigation & Responsive Layout
- **Persistent Dark Navy Sidebar (`#0f172a`)**: Fixed 240px primary navigation with active pill indicator. Eliminates horizontal scrolling and viewport displacement.
- **Seamless View Switching**: Direct one-click routing between Dashboard, Institutes Directory, Call Logs, Customer Visits, and Follow-up Schedules without requiring secondary "Back" buttons.
- **Real-Time KPI Metric Cards**: Live interactive counts for Total Institutes, Calls Today, Visits, and Pending Follow-ups with instant direct drill-down.

### B. Institute & Customer Directory Management
- Full institutional profiles with multi-branch recording, district/taluka/village geographical hierarchy, priority tagging, lead statuses, reference sourcing, and primary contact registers.
- Live real-time substring search and filtering across institute names, cities, districts, and assigned executives.

### C. Call Logging & Telecalling Management
- Record incoming and outgoing calls with timestamp logging, executive attribution, call outcome classification (Interested, Callback, Closed, Not Reached), notes, and linked follow-up scheduling.

### D. On-Site Campus Visits
- Track physical customer meetings, purpose of visit, attendees met, contact phone numbers, executive assignments, and execution status.

### E. Follow-up Reminders & Schedule Tracking
- Due, overdue, and upcoming follow-up schedules.
- Automatic task creation upon logging customer calls or registering institutes.

### F. Live Action Feed & Audit Trail
- Real-time chronological audit trail of all actions across the workspace.
- "View All" modal dialog featuring categorized filters (Calls, Visits, Follow-ups) and live query search.

### G. Zero-Loss Data Persistence
- Built-in persistent local storage architecture: All added records, updates, and custom entries remain intact upon page reload (F5), app restart, or machine reboot.

---

## 3. How to Run the Application

### Option 1: Standalone Windows Desktop Application (Zero Setup Required)
1. Extract `ConnectFlow-CRM-Desktop-App.zip`.
2. Double-click `ConnectFlow-CRM.exe` (or open `ConnectFlow-Desktop.html` in Microsoft Edge / Google Chrome).
3. The CRM desktop interface launches instantly. No web servers, Node.js, or Java installations required.

### Option 2: Spring Boot Enterprise REST API Backend
Prerequisites: Java 17+, Maven 3.8+, PostgreSQL.

1. Open PowerShell in the project directory.
2. Generate session JWT Secret and run:
```powershell
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$bytes = New-Object byte[] 32
$rng.GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
$rng.Dispose()
.\mvnw.cmd spring-boot:run
```
3. Backend service initializes at `http://localhost:8080/api`.

---

## 4. Default Demonstration Accounts & Roles

| Role | Default Email / Username | Default Password | Access Permissions |
|---|---|---|---|
| **Administrative** | `amit.patil@example.com` | `root` | Full unrestricted access to all institutes, activities, users, logs, and configurations |
| **Calling Team** | `sneha.deshmukh@example.com` | `root` | Access to assigned institutes and call logging workflows |
| **Field Executive** | `rohit.more@example.com` | `root` | Access to campus visits, meetings, and client follow-ups |

*(Note: New user accounts can also be created dynamically via the built-in account registration interface.)*

---

## 5. File Structure & Distribution Package

```text
ConnectFlow-CRM-Desktop-App/
├── ConnectFlow-CRM.exe          (Windows Native 64-bit Application Launcher)
├── ConnectFlow-Desktop.html     (Complete Standalone CRM Desktop Application)
├── README.txt                   (English Execution & Deployment Documentation)
└── app/
    └── index.html               (Synchronized Application Core)
```
