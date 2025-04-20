# Java Task Management Application

A simple task management application built in Java with Swing and FlatLaf UI components.

## Prerequisites

- Java JDK (8 or higher)
- FlatLaf library (included in the lib folder)

## Compilation

### Using Java 8 specifically:
```
javac --release 8 -cp ".;lib/flatlaf-3.6.jar" -d bin java/main/Main.java java/model/*.java java/view/*.java java/controller/*.java java/service/*.java java/database/*.java util/IconFactory.java
```

### Using any installed Java version:
```
javac -cp ".;lib/flatlaf-3.6.jar" -d bin java/main/Main.java java/model/*.java java/view/*.java java/controller/*.java java/service/*.java java/database/*.java util/IconFactory.java
```

## Running the Application

### On Windows:
```
java -cp "bin;lib/flatlaf-3.6.jar" main.Main
```

### On Linux/Mac:
```
java -cp "bin:lib/flatlaf-3.6.jar" main.Main
```

## Features

- Create, edit, and delete tasks
- Organize tasks by categories
- Set due dates and priorities
- Filter and search functionality
- Modern FlatLaf UI design

## Project Structure

- `java/main`: Contains the main application entry point
- `java/model`: Data models and business logic
- `java/view`: UI components and screens
- `java/controller`: Application controllers
- `java/service`: Service layer for business operations
- `java/database`: Data persistence layer
- `util`: Utility classes including IconFactory
