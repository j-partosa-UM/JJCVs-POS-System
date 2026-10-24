# JJCVs POS System

JJCVs POS System is a Java-based point-of-sale and store management application
for managing products, inventory, stock movements, user accounts, store status,
and customer transactions. The project includes the Java source code, required
libraries, application resources, and a prebuilt Windows executable.

## Features

- User registration and login
- Product and inventory management
- Stock-in and stock-out tracking
- Point-of-sale transaction processing
- Store status management
- Receipt generation
- SQLite database storage

## Installation

### Windows executable

1. Download or clone this repository.
2. Keep the project folder structure intact, including `lib`, `src`, and the
   application resources.
3. Double-click `JJCV's System.exe` to launch the application.

If Windows displays a security warning, select **More info**, then **Run
anyway** only if you trust the copy you downloaded.

### Java JAR

1. Install Java Runtime Environment (JRE) 8 or later.
2. Download or clone this repository.
3. Open a terminal in the project folder.
4. Run:

   ```powershell
   java -jar "JJCV's System v2.0.jar"
   ```

### Build from source

1. Install JDK 8 or later and Visual Studio Code with the Extension Pack for
   Java, or use another Java IDE.
2. Open the project folder.
3. Ensure the dependencies in `lib` are available to the project:
   `jcalendar-1.4.jar` and `sqlitejdbc-v056.jar`.
4. Compile the Java files in `src` to the `bin` directory.
5. Run the `Main` class with `bin` and the JAR files in `lib` on the
   classpath.

The application uses SQLite database files included with the project. Keep
these files in their expected locations so existing data and application
resources can be found.

## Accessing the application

1. Launch the Windows executable or run the JAR.
2. On the login screen, enter one of the accounts from the bundled demo
   database.
3. Select **Login**.
4. Admin accounts open the administration page. User accounts open the
   point-of-sale page.

### Bundled demo accounts

| Username | Password | Role |
| --- | --- | --- |
| `newAccount` | `admin` | Admin |
| `newAccount1` | `admin` | User |
| `Emp1` | `user` | User |
| `Emp2` | `user` | User |
| `Jorg` | `user` | User |

These credentials are stored in plain text in the included SQLite database and
are provided only for demonstrating the application. Change or remove these
accounts before using the application with real business data or publishing
the repository publicly. An administrator can create additional accounts using
the registration page.

## Project structure

- `src` - Java source code, application resources, and SQLite database
- `lib` - external Java dependencies
- `bin` - compiled classes and runtime resources
- `slips` - generated receipt files
- `sample images` - sample product images
- `JJCV's System.exe` - Windows executable
- `JJCV's System v2.0.jar` - executable Java archive
