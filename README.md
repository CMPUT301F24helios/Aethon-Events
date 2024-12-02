# Aethon-Events: Event Lottery System Application

## Table of Contents
- [Project Overview](#project-overview)
- [Features](#features)
- [User Roles](#user-roles)
- [Installation](#installation)
- [Usage](#usage)
- [Technologies Used](#technologies-used)


## Project Overview
The Event Lottery System Application is a mobile application designed to streamline the registration process for popular community center events. It aims to provide a fair and accessible way for individuals to join waiting lists and get selected for events through a lottery system. By eliminating the need for constant webpage refreshing, the app enhances the experience for users with limitations, such as those with disabilities or work commitments.

## Features
- **Lottery System:** Allows event organizers to randomly select attendees from a waiting list.
- **QR Code Scanning:** Entrants can scan QR promotional codes to view event details and join waiting lists.
- **Firebase Integration:** Utilizes Firebase for real-time data storage and updates on event details and attendee lists.
- **Multi-User Interaction:** Differentiates between entrants, organizers, and administrators, granting specific roles and privileges.
- **Image Upload:** Organizers can upload event posters to provide visual information to entrants.
- **Geolocation Verification (Optional):** Uses geolocation to verify user locations when joining waiting lists.

## User Roles
### Entrant
- Sign up for events.
- Leave waiting lists.
- Provide and update personal information.
- Receive notifications regarding selection status.

### Organizer
- Create and manage events.
- View and manage entrant lists.
- Send notifications to entrants.

### Administrator
- Manage event and user profiles.
- Browse events, profiles, and images.
- Enforce app policies.

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/CMPUT301F24helios/Aethon-Events.git
2.	Open the project in Android Studio.
3.	Sync the project with Gradle files.
4.	Set up Firebase and add the necessary configuration files.
5.	Run the application on an Android device or emulator.

## Usage

1.	Open the app and create a user profile (Entrant, Organizer, or Administrator).
2.	Scan QR codes for events or create new events as an organizer.
3.	Join waiting lists for events you’re interested in.
4.	Wait for notifications regarding your selection status.

## Technologies Used

•	Android Studio
•	Firebase (Firestore, Authentication)
•	Java

