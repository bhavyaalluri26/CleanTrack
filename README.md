# CleanTrack: Smart Waste Collection and Management System

## 1. Required software
- JDK 17 or newer (`java -version`)
- Apache Maven 3.8+ (`mvn -version`)
- MySQL Server 8.x (running on port 3306)
- Internet connection (Maven downloads, and Bootstrap is loaded from a CDN)

## 2. Database setup
1. Make sure MySQL is running. The database `cleantrack_db` is created automatically.
   (Optional manual way: `mysql -u root -p < database\schema.sql`)
2. Open `backend\src\main\resources\application.properties` and set
   `spring.datasource.username` and `spring.datasource.password` to YOUR MySQL credentials.

## 3. Run the backend (Windows CMD)
```
cd path\to\CleanTrack\backend
mvn spring-boot:run
```
## 4. Run the frontend
No separate command is needed. The backend serves the `frontend` folder, so keep the folder structure unchanged.

## 5. URLs
- Website: http://localhost:8080
- Backend API base: http://localhost:8080/api

## 6. First admin account (created automatically on first start)
- Admin: `admin@cleantrack.com` / `Admin@123`
- Demo collector: `collector@cleantrack.com` / `Collector@123`

## 7. Testing the workflow
1. Open http://localhost:8080 > Register a new user (citizen) and submit a complaint (status PENDING).
2. Logout, login as admin > Complaints tab > assign the complaint to a collector (ASSIGNED). Try search and status filters. Add more collectors in "Users & Collectors".
3. Logout, login as the collector > see only assigned complaints > "Start Work" (IN PROGRESS) > "Mark Resolved" (RESOLVED).
4. Login as the user again > the complaint is in History > click "Give Feedback" and rate it.
5. Restart the backend: all data remains in MySQL.
(Postman: use Basic Auth with the same emails/passwords, e.g. GET /api/admin/stats.)

## 8. Common errors
- **Access denied for user 'root'** - wrong MySQL username/password in application.properties.
- **Communications link failure** - MySQL is not running or not on port 3306.
- **Port 8080 already in use** - change `server.port` in application.properties and use the new port in the URL.
- **'mvn' is not recognized** - install Maven and add its `bin` folder to PATH, then reopen CMD.
- **release version 17 not supported** - install JDK 17+ and check JAVA_HOME.
- **Blank page / 404 at localhost:8080** - run the command from inside the `backend` folder so `..\frontend` is found.
- **Unstyled pages** - no internet, the Bootstrap CDN could not load.
