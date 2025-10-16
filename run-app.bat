@echo off
echo Starting Care Home Management System...
echo.

echo Compiling...
call mvn clean compile -q

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)

echo Running application with Maven exec plugin...
mvn exec:java -Dexec.mainClass="au.edu.rmit.cosc1295.carehome.app.CareHomeApp" -Dexec.args="--module-path \"C:/Users/Medha Aluka/.m2/repository/org/openjfx/javafx-controls/21.0.2/javafx-controls-21.0.2-win.jar;C:/Users/Medha Aluka/.m2/repository/org/openjfx/javafx-fxml/21.0.2/javafx-fxml-21.0.2-win.jar\" --add-modules javafx.controls,javafx.fxml"

pause
