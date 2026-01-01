# Building the Project

## Quick Build (Recommended)

Since you have Java 22 installed, use the Gradle wrapper with the updated version:

```powershell
# From the project root directory
.\gradlew.bat clean build
```

Or to build just the notification-service:

```powershell
.\gradlew.bat :services:notification-service:build
```

## For IDE Indexing

After building, refresh your IDE:

### IntelliJ IDEA
1. Right-click on the project root → **Reload Gradle Project**
2. Or: **File** → **Invalidate Caches** → **Invalidate and Restart**

### VS Code
1. Press `Ctrl+Shift+P` → Type "Java: Clean Java Language Server Workspace"
2. Or restart VS Code

### Eclipse
1. Right-click project → **Gradle** → **Refresh Gradle Project**

## Alternative: Use Java 17

If you prefer to use Java 17 (which the project is configured for):

1. Install Java 17
2. Set JAVA_HOME to Java 17:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
   ```
3. Then run: `.\gradlew.bat clean build`

## Troubleshooting

If you still get "Unsupported class file major version" errors:
- Clear Gradle cache: `.\gradlew.bat clean --refresh-dependencies`
- Or delete: `%USERPROFILE%\.gradle\caches`

