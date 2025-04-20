# Java Task Management Application

A simple task management application built in Java with Swing and FlatLaf UI components.

## Prerequisites

- Java JDK (8 or higher)
- FlatLaf library (included in the lib folder)

## Compilation

### Using Java 8 specifically (try using java 8 for proper functioning):
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

## Project Structure

- `java/main`: Contains the main application entry point
- `java/model`: Data models 
- `java/view`: UI components and screens
- `java/controller`: Application controllers
- `java/service`: Service layer for controller
- `java/database`: Data base
- `util`: Utility classes includes IconFactory
