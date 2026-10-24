# JJCVs POS System

JJCVs POS System is a Java desktop point-of-sale and store management
application. It supports user accounts, product and inventory management,
stock movements, store status, sales transactions, receipt generation, and
SQLite data storage.

## Features

- Admin and employee login
- User registration
- Product and inventory management
- Stock-in and stock-out tracking
- Point-of-sale transaction processing
- Store status management
- Receipt generation
- SQLite database storage

## Quick start on Windows

### Using the executable

1. Download or clone this repository.
2. Keep the project folder structure intact.
3. Double-click `JJCV's System.exe`.
4. If Windows shows a security warning, select **More info**, then **Run
   anyway** only if you trust the copy you downloaded.

### Using the Java JAR

1. Install Java Runtime Environment (JRE) 8 or later.
2. Open PowerShell in the project folder.
3. Run:

   ```powershell
   java -jar "JJCV's System v2.0.jar"
   ```

## Demo login credentials

The bundled demo database contains two accounts:

**Admin account**

```text
Username: admin
Password: admin
```

This account opens the administration page, where inventory, users, and other
store functions can be managed.

**POS user account**

```text
Username: user
Password: user
```

This account opens the point-of-sale page for processing transactions.

To log in, launch the application, enter the appropriate username and
password, and select **LOGIN**. These are demonstration credentials only.
Passwords are currently stored as plain text in SQLite, so do not use this
authentication design for real business or production data without improving
password storage and account security.

An administrator can create additional accounts from the registration page.

## Build from source

1. Install JDK 8 or later and Visual Studio Code with the Extension Pack for
   Java, or use another Java IDE.
2. Open the project folder.
3. Add the dependencies in `lib` to the project classpath:
   `jcalendar-1.4.jar` and `sqlitejdbc-v056.jar`.
4. Compile the Java files in `src` to the `bin` directory.
5. Run the `Main` class with `bin` and the JAR files in `lib` on the
   classpath.

The application expects the SQLite database and image resources in their
existing project locations. Keep the folder structure intact when moving or
running the application.

## Project structure

- `src` - Java source code, application resources, and SQLite database
- `lib` - external Java dependencies
- `bin` - compiled classes and runtime resources
- `slips` - generated receipt files
- `sample images` - sample product images
- `JJCV's System.exe` - Windows executable
- `JJCV's System v2.0.jar` - executable Java archive
