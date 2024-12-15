# Call Monitor App

## Overview
The Call Monitor app is an Android application designed to monitor and log phone call data while 
running as an HTTP server. The app can serve call data to other devices on the same WiFi network 
and provides UI interface for viewing call logs and managing server operations. The user interface 
is minimalistic, reflecting the primary focus on robust architecture and functionality.

---

## Features

- **HTTP Server**:
    - Serves call log data, ongoing call information, and server metadata.
    - Accessible to devices on the same WiFi network.
- **Call Log Monitoring**:
    - Logs incoming and outgoing phone calls taken from the device’s call log.
    - Monitors ongoing call status, including caller name and phone number.
    - Updates call log data in real-time.
    - Tracks metadata such as call duration and the number of times call data was queried.
- **UI Components**:
    - Handle permission in user-friendly manner. 
    - Display the server’s IP address and port.
    - Allow users to start/stop the server.
    - List logged calls, showing caller name and call duration.
- **Background Execution**:
    - Runs in the background unless explicitly stopped by the user.

---

## Tech Stack
- **Kotlin**
- **Jetpack Compose**
- **Coroutines and Flows**
- **Ktor HTTP Server**
- **Manual DI**
- **MVVM Architecture**

---

## API Endpoints
The app exposes the following API endpoints via the HTTP server:

### Root Endpoint
**`GET /`**

Returns metadata about the service.

Example Response:
```json
{
  "start": "2024-12-14T23:00:00+00:00",
  "services": [
    { "name": "status", "uri": "http://192.168.1.100:8080/status" },
    { "name": "log", "uri": "http://192.168.1.100:8080/log" }
  ]
}
```

### Status Endpoint
**`GET /status`**

Returns the current call status.

Example Response:
```json
{
  "ongoing": "true",
  "number": "+12025550108",
  "name": "John Doe"
}
```

### Log Endpoint
**`GET /log`**

Returns the call log since the app was launched.

Example Response:
```json
[
  {
    "beginning": "2024-12-14T12:00:00+00:00",
    "duration": "498",
    "number": "+120255550203",
    "name": "Jane Doe",
    "timesQueried": "1"
  },
  {
    "beginning": "2024-12-14T11:00:00+00:00",
    "duration": "300",
    "number": "+120255550204",
    "name": "John Doe",
    "timesQueried": "3"
  }
]
```

---

## Permissions
The app requires the following permissions to function fully:
- **READ_CALL_LOG**: To retrieve call log data.
- **READ_PHONE_STATE**: To monitor ongoing call status.
- **READ_CONTACTS**: To retrieve caller name from contacts.
- **FOREGROUND_SERVICE**: To run HTTP server as a foreground service.

When any permission is not granted app will continue to work but with limited functionality and mocked data.

---

## Known bugs and potential improvements
- Limited test coverage, only sample tests for RealCallLogProvider are implemented
- The app does not react on permission changes on runtime, app needs to be restarted to apply changes
- The app does not handle network state changes in regard to the HTTP server
- HTTP server could be refactored into several classes, e.g. one that will track timesQueried