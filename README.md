# Care Home Management System
**A Complete JavaFX Application for Healthcare Facility Management**

*Student Project by Medha Aluka - RMIT University COSC1295*

---

## Project Overview

This is a comprehensive Care Home Management System built using JavaFX that demonstrates advanced Java programming concepts, object-oriented design principles, and modern GUI development. The system manages residents, staff, shifts, prescriptions, and maintains complete audit trails for healthcare compliance.

## What This Project Demonstrates

- **Object-Oriented Programming**: Inheritance, polymorphism, encapsulation
- **Design Patterns**: Singleton, MVC, Repository, Service Layer
- **JavaFX GUI Development**: Modern, responsive user interface
- **Collections Framework**: Lists, Maps, Streams for data management
- **Exception Handling**: Custom exceptions with proper error management
- **Business Logic Implementation**: Complex healthcare rules and validation
- **Audit Logging**: Complete action tracking for compliance

---

## Quick Start

### Prerequisites
- **Java 17+** (OpenJDK or Oracle JDK)
- **Maven 3.6+** (for dependency management)
- **JavaFX 21** (included in dependencies)

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Medha-Aluka/S4154047_AlukaSaiMedha_Assignment2.git
   cd S4154047_AlukaSaiMedha_Assignment2
   ```

2. **Build and run:**
   ```bash
   # Windows
   .\mvnw.cmd javafx:run
   
   # Linux/Mac
   ./mvnw javafx:run
   ```

3. **Login with default credentials:**
   - **Manager**: `admin` / `admin` (Full access)
   - **Doctor**: `parthu` / `parthu` (Prescription management)
   - **Nurse**: `madhu` / `madhu` (Medication administration)

---

## System Architecture

### Package Structure
```
au.edu.rmit.cosc1295.carehome/
├── app/                    # Application entry points
│   ├── CareHomeApp.java    # Main JavaFX launcher
│   └── AppContext.java     # Singleton context manager
├── controller/             # JavaFX controllers (MVC)
│   ├── ResidentsController.java
│   ├── StaffController.java
│   ├── ShiftsController.java
│   ├── PrescriptionsController.java
│   └── AdminLogController.java
├── model/                  # Domain models
│   ├── Person.java         # Abstract base class
│   ├── Staff.java         # Abstract staff class
│   │   ├── Manager.java
│   │   ├── Doctor.java
│   │   └── Nurse.java
│   ├── Resident.java
│   ├── Bed.java
│   ├── Shift.java
│   ├── Prescription.java
│   └── AdministrationRecord.java
├── service/               # Business logic layer
│   ├── ResidentService.java
│   ├── StaffService.java
│   ├── ShiftService.java
│   ├── PrescriptionService.java
│   ├── AdministrationService.java
│   ├── AuthorizationService.java
│   ├── AuditService.java
│   └── BedService.java
├── repository/            # Data access layer
│   ├── InMemoryResidentRepository.java
│   ├── InMemoryStaffRepository.java
│   ├── InMemoryBedRepository.java
│   ├── InMemoryPrescriptionRepository.java
│   └── InMemoryAdministrationRepository.java
├── exceptions/           # Custom exceptions
│   ├── AuthorizationException.java
│   ├── BedOccupiedException.java
│   ├── ShiftViolationException.java
│   └── NotFoundException.java
└── util/                 # Utility classes
    ├── DatabaseManager.java
    └── Ids.java
```

### Design Patterns Implemented

1. **Singleton Pattern** (`AppContext`)
   - Single instance managing all services and repositories
   - Ensures consistent state across the application

2. **MVC Pattern** (Model-View-Controller)
   - **Models**: Domain objects (Resident, Staff, etc.)
   - **Views**: FXML files defining the UI
   - **Controllers**: Handle user interactions and business logic

3. **Repository Pattern**
   - Abstract data access layer
   - Easy to swap implementations (in-memory → database)

4. **Service Layer Pattern**
   - Business logic separated from controllers
   - Reusable across different UI components

---

## Features & Functionality

### 1. Resident Management
- Add new residents with name and gender
- Assign residents to beds with visual bed layout
- Move residents between beds with automatic bed management
- Visual ward layout with color-coded gender indicators:
  - **Blue**: Male residents
  - **Pink**: Female residents  
  - **Grey**: Vacant beds
- Click beds to view resident details

### 2. Staff Management (Manager Only)
- Add new staff (Managers, Doctors, Nurses)
- Modify staff credentials (username/password)
- View all staff in organized table format
- Role-based access control enforcement

### 3. Shift Management (Manager Only)
- Assign shifts to staff members
- Visual weekly schedule grid
- Automatic compliance checking:
  - Maximum 8 hours per shift
  - Maximum 12 hours per day
  - Maximum 48 hours per week
  - No overlapping shifts allowed
- Real-time statistics and compliance indicators

### 4. Prescription Management
- **Doctors**: Add prescriptions (must be on duty)
- **Nurses**: Administer medications (must be on duty)
- View prescriptions and administration records by resident
- Authorization and roster checking for all clinical actions

### 5. Audit Logging
- Complete action tracking with timestamps
- Staff ID logging for accountability
- Viewable audit trail in the application
- Regulatory compliance support

---

## Security & Authorization

### Role-Based Access Control
- **Manager**: Full system access
- **Doctor**: Can add prescriptions (when rostered)
- **Nurse**: Can administer medications (when rostered)
- **All roles**: Can view information

### Roster Checking
- Clinical actions require staff to be **on duty**
- **Real-time verification** against shift schedule
- **Authorization exceptions** for violations

### Audit Trail
- **Every action logged** with timestamp and staff ID
- **Immutable log file** for compliance
- **Complete accountability** for all operations

---

## Business Rules & Validation

### Shift Management Rules
1. Maximum 8 hours per single shift
2. Maximum 12 hours per day
3. Maximum 48 hours per week
4. No overlapping shifts allowed
5. Exceptions thrown on rule violations

### Bed Assignment Rules
1. Cannot assign resident to occupied bed
2. Moving resident automatically vacates previous bed
3. Visual color-coding by gender

### Clinical Action Rules
1. Only doctors can prescribe medications
2. Only nurses can administer medications
3. Must be on duty (rostered) for clinical actions
4. All actions logged for audit trail

---

## Technologies Used

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17+ | Core programming language |
| **JavaFX** | 21 | Modern GUI framework |
| **Maven** | 3.6+ | Build and dependency management |
| **FXML** | - | Declarative UI definition |
| **CSS** | - | Styling and theming |
| **H2 Database** | 2.2.224 | Embedded database for persistence |

---

## Data Model

### Class Hierarchy
```
Person (abstract)
├── Staff (abstract)
│   ├── Manager
│   ├── Doctor
│   └── Nurse
└── Resident

Exception (built-in)
├── RuntimeException
    ├── AuthorizationException
    ├── BedOccupiedException
    ├── ShiftViolationException
    └── NotFoundException
```

### Sample Data
**Staff Members:**
- Manager: admin
- Dr. Akshay
- Nurse Madhu

**Residents:**
- Kishan - Male
- Medha - Female

**Bed Layout:**
- Ward 1: 3 rooms with multiple beds
- Ward 2: 2 rooms with multiple beds

---

## Learning Outcomes Demonstrated

### CLO01: Java Programming
- Medium-sized application with professional coding standards
- Efficient algorithms and data structures
- Proper use of Java Collections Framework

### CLO02: Object-Oriented Development
- Analysis and design of solution
- UML-worthy class hierarchy
- SOLID principles applied

### CLO03: Collections Framework
- Lists, Maps, Streams used throughout
- Proper use of generics
- Efficient data management

### CLO04: Advanced Concepts
- JavaFX GUI development
- Event handling and user interaction
- Database-ready architecture

### CLO05: Design Patterns
- Singleton (AppContext)
- MVC (entire application)
- Repository (data access)
- Service Layer (business logic)

---

## Testing the Application

### Test Scenarios

1. **Resident Management**
   - Add new resident
   - Assign to bed
   - Move between beds
   - Verify color coding

2. **Staff Management**
   - Add new staff (Manager only)
   - Modify credentials
   - Test authorization

3. **Shift Management**
   - Assign valid shifts
   - Test rule violations
   - Check compliance indicators

4. **Prescription Management**
   - Doctor adds prescription (when rostered)
   - Nurse administers medication (when rostered)
   - Test authorization failures

5. **Audit Logging**
   - Verify all actions are logged
   - Check timestamps and staff IDs

---

## Project Files

### Core Application Files
- `CareHomeApp.java` - Main JavaFX launcher
- `AppContext.java` - Singleton context manager
- `pom.xml` - Maven configuration

### FXML Views
- `MainView.fxml` - Main tab container
- `ResidentsView.fxml` - Resident management
- `StaffView.fxml` - Staff management
- `ShiftsView.fxml` - Shift management
- `PrescriptionsView.fxml` - Prescription management
- `AdminLogView.fxml` - Audit log viewer

### Styling
- `styles.css` - Modern, professional styling

### Documentation
- `README.md` - This comprehensive guide
- `SETUP_GUIDE.md` - Detailed setup instructions
- `IMPLEMENTATION_SUMMARY.md` - Technical implementation details
- `TESTING_CHECKLIST.md` - 50 test cases
- `GUI_LAYOUT_DESCRIPTION.md` - Visual specifications

---

## Running Instructions

### Method 1: Maven (Recommended)
```bash
# Navigate to project directory
cd S4154047_AlukaSaiMedha_Assignment2

# Run the application
.\mvnw.cmd javafx:run
```

### Method 2: Batch File (Windows)
```bash
# Double-click or run
run-app.bat
```

### Method 3: IDE
1. Import as Maven project
2. Run `CareHomeApp.java` as JavaFX Application

---

## Troubleshooting

### Common Issues

1. **JavaFX Module Error**
   - Ensure Java 17+ is installed
   - Check JAVA_HOME environment variable

2. **Maven Build Fails**
   - Run `mvn clean compile` first
   - Check internet connection for dependencies

3. **Login Issues**
   - Use default credentials: admin/admin, parthu/parthu, madhu/madhu
   - Check database initialization

4. **GUI Not Loading**
   - Verify FXML files are in resources/fxml/
   - Check CSS file path in CareHomeApp.java

---

## Future Enhancements

### Planned Features
- **Database Persistence**: Replace in-memory with H2 database
- **Advanced Reporting**: Generate PDF reports
- **Patient Medical History**: Track medical records
- **Medication Inventory**: Track medication stock
- **Email Notifications**: Alert staff of important events
- **Mobile App**: Companion app for nurses

### Technical Improvements
- **Unit Testing**: JUnit test suite
- **API Integration**: RESTful web services
- **Cloud Deployment**: AWS/Azure hosting
- **Real-time Updates**: WebSocket communication

---

## Student Information

**Student**: Medha Aluka  
**Student ID**: S4154047  
**Course**: COSC1295 - Advanced Programming  
**Institution**: RMIT University  
**Semester**: 2024  

### Assignment Requirements Met
- Object-Oriented Design
- Design Patterns Implementation
- JavaFX GUI Development
- Collections Framework Usage
- Exception Handling
-Business Logic Implementation
- Audit Logging
- Documentation

---

## Contact & Support

**GitHub Repository**: [https://github.com/Medha-Aluka/S4154047_AlukaSaiMedha_Assignment2.git](https://github.com/Medha-Aluka/S4154047_AlukaSaiMedha_Assignment2.git)

**Alternative Repository**: [https://github.com/Medha-Aluka/Advance-Programming-assignment.git](https://github.com/Medha-Aluka/Advance-Programming-assignment.git)

---

## License

This project is created as an academic assignment for RMIT University COSC1295. All code is original work demonstrating advanced Java programming concepts and modern software development practices.

---

**Thank you for exploring my Care Home Management System!**

*This project showcases professional-level Java development skills, modern GUI design, and comprehensive healthcare management functionality. Perfect for demonstrating object-oriented programming mastery and real-world application development.*

---
*Last updated: $(date)*