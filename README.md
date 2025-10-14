# RMIT Care Home Management System

A comprehensive JavaFX-based application for managing a care home facility, including resident management, staff scheduling, prescription handling, and audit logging.

## Features

### 1. **Resident Management**
- Add new residents to the system
- Assign residents to vacant beds
- Move residents between beds
- Visual ward/room/bed layout with color-coded gender indicators:
  - Blue for Male residents
  - Pink for Female residents
  - Grey for vacant beds
- Click on occupied beds to view resident details

### 2. **Staff Management** (Manager Only)
- Add new staff members (Doctors, Nurses, Managers)
- Modify staff credentials (username/password)
- View all staff in a table format
- Role-based access control

### 3. **Shift Management** (Manager Only)
- Assign shifts to staff members
- Visual weekly schedule grid
- Automatic compliance checking:
  - Max 8 hours per shift
  - Max 12 hours per day
  - Max 48 hours per week
  - No overlapping shifts
- Real-time shift statistics display

### 4. **Prescription Management**
- **Doctors** can add prescriptions (must be on duty)
- **Nurses** can administer medications (must be on duty)
- View prescriptions and administration records by resident
- Authorization and roster checking for all actions

### 5. **Audit Log**
- All actions are logged with timestamp and staff ID
- View complete audit trail
- Satisfies regulatory compliance requirements

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- JavaFX 21

### Build and Run
```bash
# Navigate to project directory
cd DEMO

# Clean and compile
mvn clean compile

# Run the application
mvn javafx:run
```

### Default Login Credentials
- **Manager**: username: `mary`, password: `pass`
- **Doctor**: username: `sam`, password: `pass`
- **Nurse**: username: `kim`, password: `pass`

## System Architecture

### Design Patterns Used
1. **Singleton Pattern**: `AppContext` provides single point of access to all services
2. **MVC Pattern**: Separation of Model, View (FXML), and Controller
3. **Repository Pattern**: In-memory repositories for data management
4. **Service Layer Pattern**: Business logic encapsulated in service classes

### Key Components

#### Models
- `Resident`, `Staff` (Manager, Doctor, Nurse)
- `Bed`, `Shift`, `Prescription`, `AdministrationRecord`

#### Services
- `ResidentService`: Manages resident operations
- `StaffService`: Manages staff operations
- `ShiftService`: Manages shift assignment with business rules
- `PrescriptionService`: Manages prescriptions (Doctor only)
- `AdministrationService`: Manages medication administration (Nurse only)
- `AuthorizationService`: Enforces role-based access and roster checking
- `AuditService`: Logs all actions to file

#### Controllers
- `ResidentsController`: Handles resident and bed management UI
- `StaffController`: Handles staff management UI
- `ShiftsController`: Handles shift assignment UI
- `PrescriptionsController`: Handles prescription and administration UI
- `AdminLogController`: Displays audit log

## Business Rules

### Shift Management Rules
1. Maximum 8 hours per single shift
2. Maximum 12 hours per day
3. Maximum 48 hours per week
4. No overlapping shifts allowed
5. Exception thrown if rules violated

### Authorization Rules
1. Only **Managers** can add/modify staff
2. Only **Managers** can assign shifts
3. Only **Doctors** can add prescriptions (and must be on duty)
4. Only **Nurses** can administer medications (and must be on duty)
5. All actions logged with staff ID and timestamp

### Bed Assignment Rules
1. Cannot assign resident to occupied bed
2. Moving resident automatically vacates previous bed
3. Visual color-coding by gender

## Technologies Used
- **Java 17**: Core programming language
- **JavaFX 21**: GUI framework
- **Maven**: Build and dependency management
- **FXML**: Declarative UI definition
- **CSS**: Styling and theming

## Project Structure
```
DEMO/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── au/edu/rmit/cosc1295/carehome/
│   │   │       ├── app/           # Application entry points
│   │   │       ├── controller/    # JavaFX controllers
│   │   │       ├── model/         # Domain models
│   │   │       ├── service/       # Business logic
│   │   │       ├── repository/    # Data access
│   │   │       ├── exceptions/    # Custom exceptions
│   │   │       └── util/          # Utility classes
│   │   └── resources/
│   │       ├── fxml/              # FXML view files
│   │       └── css/               # Stylesheets
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## Future Enhancements
- Database persistence (JPA/Hibernate)
- More comprehensive reporting
- Patient medical history tracking
- Medication inventory management
- Email/SMS notifications for staff
- Mobile app for nurses

## Design Decisions

### 1. Singleton AppContext
The `AppContext` class uses the Singleton pattern to provide centralized access to all repositories and services. This ensures consistent state across the application and simplifies dependency management.

### 2. Authorization Service
A dedicated `AuthorizationService` was created to enforce both role-based permissions and roster checking. This separation of concerns makes the authorization logic reusable and maintainable.

### 3. Audit Logging
All actions are logged synchronously to a file using the `AuditService`. This ensures regulatory compliance and provides a complete audit trail.

### 4. Visual Bed Layout
The bed visualization uses a grid-based layout with color-coding to provide an intuitive at-a-glance view of occupancy and resident gender distribution.

### 5. In-Memory Repositories
For simplicity and rapid development, in-memory repositories are used. These can be easily replaced with database-backed repositories by implementing the same interface.

## License
This project is created as an academic assignment for RMIT University COSC1295.

## Authors
Medha Aluka - RMIT University

