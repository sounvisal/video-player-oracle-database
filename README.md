# Video Player with Oracle Database

A Java-based desktop application for managing and playing videos with user authentication and admin controls. This application uses Oracle Database for storing user credentials, video metadata, and user session information.

## Features

- **User Authentication**: Secure login and registration system
- **Video Management**: Upload, view, edit, and delete videos
- **Admin Dashboard**: Admin panel for managing users and videos
- **User Dashboard**: Personalized dashboard showing user's uploaded videos
- **Video Playback**: Built-in video player for streaming videos
- **Database Integration**: Oracle Database for persistent data storage
- **Session Management**: User sessions for tracking logged-in users

## Project Structure

```
Videoplayer/
├── src/
│   ├── database/
│   │   └── Connection.java          # Database connection management
│   └── videoplayer/
│       ├── Videoplayer.java         # Main application entry point
│       ├── LoginScreen.java         # User login interface
│       ├── RegisterScreen.java      # User registration interface
│       ├── Session.java             # Session management
│       ├── MyDashboardScreen.java   # User dashboard
│       ├── AllVideosScreen.java     # Video browsing screen
│       ├── UploadVideoScreen.java   # Video upload interface
│       ├── EditVideoScreen.java     # Video editing interface
│       └── AdminDashboard.java      # Administrator panel
├── build.xml                         # Ant build configuration
├── manifest.mf                       # Manifest file
├── nbproject/                        # NetBeans project files
├── videos/                           # Local video storage
└── test/                             # Test files
```

## Prerequisites

- **Java**: JDK 8 or higher
- **Oracle Database**: 11g or higher
- **NetBeans IDE**: Recommended for development
- **Libraries**: Java Swing (built-in), JDBC drivers for Oracle

## Installation & Setup

### 1. Database Configuration

1. Create an Oracle database user and schema
2. Create the required tables for users, videos, and sessions
3. Update the database connection credentials in `src/database/Connection.java`

### 2. Build the Project

```bash
# Using Ant (NetBeans default)
ant clean build

# Or compile with javac
javac -d build/classes -cp src src/**/*.java
```

### 3. Run the Application

```bash
# Using Ant
ant run

# Or using Java directly
java -cp build/classes videoplayer.Videoplayer
```

## Usage

### First Time Setup

1. **Register**: Click "Register" to create a new user account
2. **Login**: Use your credentials to log in
3. **Upload Videos**: Navigate to "Upload" and select your video file
4. **View Videos**: Access your uploaded videos in "My Dashboard" or browse all videos in "All Videos"
5. **Edit Videos**: Modify video titles and descriptions
6. **Admin Access**: Admins can access the admin dashboard for system management

### User Roles

- **Regular Users**: Can upload videos, view all videos, manage their own content
- **Administrators**: Full system access including user management and content moderation

## Key Components

### Connection.java
Manages Oracle Database connections using JDBC and implements connection pooling for efficient database access.

### Videoplayer.java
Main entry point of the application that initializes the UI and manages screen navigation.

### Session.java
Handles user session management and authentication state throughout the application lifecycle.

### Screen Classes
- **LoginScreen**: Handles user authentication
- **RegisterScreen**: Processes new user registration
- **MyDashboardScreen**: Displays user's own videos
- **AllVideosScreen**: Browses all available videos
- **UploadVideoScreen**: Uploads new videos to the system
- **EditVideoScreen**: Modifies video information
- **AdminDashboard**: System administration and management

## Database Schema

The application requires the following main tables:

- **Users**: Stores user credentials and profile information
- **Videos**: Contains video metadata (title, description, upload date, uploader)
- **Sessions**: Tracks active user sessions

## Technology Stack

- **Language**: Java
- **UI Framework**: Java Swing
- **Database**: Oracle Database
- **Connection**: JDBC (Java Database Connectivity)
- **Build Tool**: Apache Ant
- **IDE**: NetBeans

## Configuration

Update the following in `src/database/Connection.java`:

```java
- Database URL: jdbc:oracle:thin:@[host]:[port]:[sid]
- Username: Your Oracle database username
- Password: Your Oracle database password
```

## Troubleshooting

### Database Connection Issues
- Verify Oracle Database is running
- Check database credentials in Connection.java
- Ensure JDBC driver is included in the classpath

### Build Errors
- Clean and rebuild: `ant clean build`
- Verify Java version compatibility
- Check all dependencies are available

### Runtime Issues
- Check application logs for errors
- Verify database connectivity
- Ensure video files are in the correct format (MP4, AVI, etc.)

## Contributing

To contribute to this project:

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Commit with descriptive messages
5. Push to your fork
6. Submit a pull request

## License

This project is provided as-is. Modify and distribute as needed for your use case.

## Support

For issues, questions, or suggestions, please create an issue on the GitHub repository.

## Author

Developed as a Java Swing and Oracle Database integration project.

---

**Note**: This application requires an Oracle Database instance to be running before launching. Ensure all database credentials are correctly configured for proper functionality.
