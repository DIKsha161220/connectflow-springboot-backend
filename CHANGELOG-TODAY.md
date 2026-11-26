# ConnectFlow CRM - Changes & Fixes Summary (26-27 Nov 2026)

## 1. Spring Boot Backend Fixes
* **File:** `pom.xml`
  * Changed Spring Boot starter parent version from invalid `4.0.1` to stable `3.4.1`.
  * Verified Maven build and Maven Wrapper (`.\mvnw spring-boot:run`).
  * Resolved port 8080 conflicts by stopping obsolete java processes.

## 2. ConnectFlow Desktop Application Fixes
* **File:** `ConnectFlow-Desktop.html`
  * **Fixed Fatal JavaScript SyntaxError:**
    * Removed duplicate `const list` declaration inside `saveInstitute()` which previously prevented the whole script from executing in browser.
    * Declared `list` safely to ensure global scope safety across all operations.
  * **Fixed Multi-View & Tab Switching:**
    * Added `.workspace-tab-view { display: none; }` and `.workspace-tab-view.active { display: flex; }` CSS rules.
    * Enabled seamless tab switching across Dashboard, Institutes, Call Logs, Visits, and Follow-ups.
  * **Enhanced Auto-Session Handling:**
    * Updated `checkSession()` to auto-authenticate default Admin (`Amit Patil`) on startup, preventing blocking auth overlays.
  * **Adaptive Backend Auto-Connect & Sync:**
    * Implemented background auto-retry loop (every 4 seconds when disconnected, every 20 seconds when connected).
    * Enhanced `pushVisitToBackend`, `pushCallToBackend`, and `pushFollowupToBackend` to support dynamic `API_BASE_URL` (Localhost or Cloud).
    * Configured adaptive timeout (3s for localhost, 15s for Render cloud cold starts).

## 3. Git Save Commands
To commit all changes to Git, run the following in your terminal:
```powershell
cd D:\CRM-BACKEND12
git add .
git commit -m "Fix Spring Boot 3.4.1 version, auto-connect backend, and fix Desktop CRM navigation & syntax errors"
```
