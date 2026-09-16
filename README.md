# Barebone

Barebone is a starter Android application (Kotlin) that provides a baseline
set of common app building blocks: user authentication, a contacts list, a
map/places view, and push notifications.

## Features

- **Authentication** — login, registration, and forgot-password flows backed
  by `SharedPreferences` (`MainActivity`, `Register`, `ForgotPassword`).
- **Contacts list** — add/edit/delete contacts persisted locally with a Room
  database (`Barebonedb`, `User`, `UserDao`, `AddEdit`).
- **Bottom navigation** — a `BottomNav` activity hosting Dashboard,
  Notifications, and Settings fragments (`ui/dashboard`, `ui/notifications`,
  `SettingsFragment`).
- **Maps & Places** — a map screen backed by the Google Places API via
  Retrofit (`ui/maps/MapFragment`, `services/PlacesAPI`,
  `datamodel/PlaceResultModel`).
- **Push notifications** — Firebase Cloud Messaging integration
  (`FirebaseMessagingService`) plus Firebase Analytics and Functions.

## Tech stack

- Kotlin, Android Gradle Plugin 4.0.2, Kotlin 1.4.10
- `compileSdkVersion` 29, `minSdkVersion` 22, `targetSdkVersion` 29
- AndroidX (AppCompat, ConstraintLayout, Navigation, Lifecycle)
- Room for local persistence
- Retrofit + Gson + RxJava2 for networking
- Firebase (Cloud Messaging, Analytics, Functions) and Google Play Services
  (Maps, Places)

## Project structure

```
app/src/main/java/com/barebone/app/
├── MainActivity.kt              # Login
├── Register.kt                  # Registration
├── ForgotPassword.kt            # Forgot password
├── BottomNav.kt                 # Bottom navigation host activity
├── AddEdit.kt                   # Add/edit a contact
├── Barebonedb.kt, User.kt, UserDao.kt, ModelUser.kt   # Room database layer
├── FirebaseMessagingService.kt  # FCM push notifications
├── SettingsFragment.kt          # App settings
├── datamodel/PlaceResultModel.kt
├── services/PlacesAPI.kt        # Retrofit API for Google Places
└── ui/
    ├── dashboard/                # Dashboard fragment + view model
    ├── maps/                     # Map fragment + view model
    └── notifications/            # Notifications fragment + view model
```

## Building

This is a standard Gradle Android project.

1. Install Android Studio / the Android SDK (compileSdk 29, build-tools
   29.0.3).
2. Provide Google API keys in a `secure.properties` file at the repo root:
   ```
   MAPS_API_KEY=your_maps_api_key
   PLACES_API_KEY=your_places_api_key
   ```
3. Add a Firebase `google-services.json` file under `app/` (required by the
   `com.google.gms.google-services` plugin).
4. Build with the included wrapper:
   ```
   ./gradlew assembleDebug
   ```

## Tests

The project currently only contains the default Android project test stubs
(`app/src/test/.../ExampleUnitTest.kt` and
`app/src/androidTest/.../ExampleInstrumentedTest.kt`); there is no
project-specific test suite yet.
