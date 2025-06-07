# DroidDesign

**DroidDesign** is a collaborative Android app that enables users to scan QR codes, associate them with geolocated items, and manage rich object metadata including photos, scores, and comments — all backed by a real-time Firebase backend.

The app was built in an Agile team setting using Java, Android SDK, and Firebase, with an emphasis on clean architecture, testability, and intuitive user experience.

---

## Features

- QR Code Scanning — Fast QR scanning using camera integration
- Map Integration — Pin and track QR-associated items on a map
- Rich Object Metadata — Attach photos, scores, and user comments
- Firebase Backend — Real-time sync across devices with offline caching
- Unit Testing — JUnit test coverage for all core logic and backend behavior

---

## Project Highlights

### API + Firebase Integration

- Integrated Firebase Realtime Database to support object storage, comment syncing, and metadata persistence
- Structured data with efficient keying for fast retrieval and scalability
- Implemented event listeners for real-time updates and sync conflict resolution

### Unit Testing

- Built modular unit tests with JUnit to ensure:
  - Stable object creation and deletion
  - Firebase data sync accuracy
  - Metadata update and scoring logic
- Achieved robust error handling for edge cases like nulls, network loss, or concurrent updates

### Team Workflow

- Followed Agile methodology: weekly sprints, retrospectives, feature planning
- Used GitHub Projects for task tracking and issue management
- Maintained all documentation (use cases, diagrams, retrospectives) via GitHub Wiki

---

## Tech Stack

| Layer | Technologies |
|-------|--------------|
| Language | Java |
| Framework | Android SDK, Google Maps API |
| Backend | Firebase Realtime Database |
| Testing | JUnit |
| Version Control | Git, GitHub |
| Architecture | MVC-inspired modular design |

---

## UI Previews

- QR Scanner Interface  
- Item Detail Page  
- Interactive Map  
- Comments and Scoring

---

## Leadership Principles Reflected

| Principle | How It's Demonstrated |
|----------|------------------------|
| Deliver Results | Built and shipped a complete multi-feature mobile app within sprints |
| Customer Obsession | Focused on seamless scanning and clean mobile experience |
| Dive Deep | Debugged API and Firebase issues, wrote tests for backend behavior |
| Invent and Simplify | Used Firebase to reduce infrastructure complexity and enable real-time features |
| Ownership | Took lead on Firebase integration and testing strategy |

---

## Contributor Highlight

**Saher Afrin Khan**  
- Firebase integration (object storage, syncing)  
- API design and backend logic  
- Unit testing using JUnit  
- Sprint collaboration and documentation  

Email: saherafr@ualberta.ca  
LinkedIn: https://www.linkedin.com/in/saher-khan-961208216

---

## Future Improvements

- Flashlight toggle for low-light scanning  
- Role-based access for object modification  
- Historical scan tracking and object log  
- Enhanced search and filter capabilities

---

## Setup Instructions

```bash
# Clone the repo
git clone https://github.com/CMPUT301W24T08/DroidDesign.git

# Open in Android Studio

# Connect a device or emulator and run the app
