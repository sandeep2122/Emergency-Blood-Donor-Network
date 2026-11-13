# Emergency Blood Donor Network (Java Swing + MySQL)

## Features
- Donor registration with consent and hashed login password, donor login and profile update
- Donor search by blood group and city (phone numbers are not displayed)
- Emergency request submission and administrative request status management
- Admin login with PBKDF2 password verification and donor/request statistics
- JDBC prepared statements, transactional registration, central database connection

## Setup (Windows / PowerShell)
1. Install Java JDK 21+ and MySQL 8.x. Open MySQL Command Line Client as `root`.
2. Run SQL in `database_setup.sql`. Existing data is retained. For new accounts, donor login requires `donor_accounts` table.
3. In MySQL root console, run the **commented user/GRANT statements** at the end of SQL file after replacing the placeholder with a unique strong password. The account needs the listed permissions. Do not use your MySQL root account for the app.
4. In a fresh VS Code PowerShell terminal, set the app database password (it is local to that terminal):
   ```powershell
   $env:BLOOD_DB_PASSWORD = Read-Host "Enter NEW blood_app password"
   ```
   NOTE: `Read-Host` input is visible while typing; avoid screen sharing. Do not send the password to anyone.
5. From the `javaproject` directory, compile and run:
   ```powershell
   javac -cp ".;lib/*" *.java
   java -cp ".;lib/*" DBConnection
   java -cp ".;lib/*" Main
   ```
6. For initial admin setup, use the included `CreateAdmin.java` CLI utility **with a MySQL root account** only on your own computer, and then remove the utility from deployed copies. Existing admin credentials remain in the `admins` table.
7. If `Access denied for user blood_app` appears, test credentials via MySQL command line and reset the account using root; this is a MySQL account problem, not a Java compilation problem.

## Important limitations
This is an educational local desktop prototype, not a hospital-grade deployment. `VERIFIED` and `COMPLETED` are legacy database status names used for administrative review/closure only, **not medical validation**. No actual notifications, blood reservations, donor medical screening, blood-type crossmatching, hospital integration, or emergency dispatch are implemented. Never rely on this software to arrange blood in a medical emergency. Contact a licensed hospital/blood bank immediately. Real deployment requires a server-side authorization layer, privacy compliance, consent withdrawal/deletion, audit logging, secure transport and storage, abuse protection, and clinician verification. Do not distribute JDBC credentials inside a desktop client.

## Code organization (one detailed Java file per feature)
- `Main.java`: application home screen and menu buttons
- `DBConnection.java`: JDBC connection test and environment-based credentials
- `AppUI.java`: common form styles, messages, and input helpers
- `PasswordUtil.java`: salted PBKDF2 password hashing and verification
- `RegisterPage.java`: donor registration and donor-account creation
- `LoginPage.java`: donor login
- `DonorDashboard.java`: donor profile and update
- `SearchDonorPage.java`: donor search without public phone numbers
- `EmergencyRequestPage.java`: emergency request intake
- `AdminLoginPage.java`: admin login
- `AdminSession.java`: demo UI session state (not production authorization)
- `AdminDashboard.java`: admin statistics and navigation
- `AdminDonorsPage.java`: admin donor directory
- `ManageRequestsPage.java`: admin request status management
- `CreateAdmin.java`: first-time local admin account setup

## Important notes for previous installations
- If `blood_app` gives MySQL `Access denied`, the account password must be corrected in MySQL by an administrator. Changing Java code alone will not fix invalid MySQL credentials.
- The MySQL password is never required inside donor-facing forms. Use an environment variable locally. In PowerShell, hidden entry is available using:
  ```powershell
  $secure = Read-Host 'Database password' -AsSecureString
  $env:BLOOD_DB_PASSWORD = [System.Net.NetworkCredential]::new('', $secure).Password
  ```
  This still places the password in the environment of the running process. Keep the computer private and close the terminal after testing.
- Old donors created before `donor_accounts` existed cannot log in until a donor account is created through an appropriate authorized migration. Existing donor data is not automatically changed.
- Admin donor listing is informational, not a medical eligibility check.
- Source files compile with JDK 21; live database and GUI interactions require testing with your local MySQL instance.
