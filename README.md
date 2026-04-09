This is a Kotlin Multiplatform project targeting Android, iOS.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

---

## 📜 Changelog
 
### [v0.4.0-alpha] - 2026-04-10
**Phase 4: The "Silt & Stone" Holographic Overhaul**
- **Cylinder Stack UI**: Transformed the flat wallet list into a high-fidelity 3D rotating wheel with non-linear focus logic ($focusFactor^2$).
- **Editorial Brutalism Design**: Implemented a comprehensive design system with muted "Silt & Stone" tokens and bundled Space Grotesk typography.
- **Interactive Details**: Added an expandable card view with custom 3D plane transitions and auto-saving persistence.
- **Persistence Upgrade**: Migrated SQLDelight schema with a `notes` column, preserving all existing card data.
- **Intelligence Refinement**: Implemented `BankMatcher` fuzzy logic to improve OCR bank detection from stylized logos.
- **Hardware-Synced Sheen**: Refined the refractive highlight engine for better cohesion with the new Material tokens.


### [v0.3.0-alpha] - 2026-04-10
**Phase 3: CameraX & OCR Integration**
- **Cybernetic Viewfinder**: Implemented an animated, orientation-reactive scanner overlay with haptic "lock-on" feedback.
- **Real-Time OCR Pipeline**: Integrated CameraX with ML Kit Text Recognition for instant card number and expiry detection.
- **Luhn Algorithm Validation**: Automated checksum verification for scanned card numbers.
- **Lifecycle & Permission Management**: Robust handling of Android camera permissions and hardware surface synchronization.
- **Versioning UI**: Added a subtle version footer tracking to the main screen.

### [v0.2.0-alpha] - 2026-04-09
**Phase 2: Sensor Fusion & Motion Engine**
- **Hardware Sensor Layer**: Implemented `AndroidHardwareSensorEngine` using high-precision `TYPE_ROTATION_VECTOR`.
- **Landscape Support**: Integrated coordinate remapping to handle device rotation dynamically.
- **Anti-Gravity UI**: Created `Modifier.holographicTilt` with 3D transformations.
- **Dynamic Specular Highlight**: Added a linear sheen gradient that reacts to device tilt.
- **Smoothed Motion**: Implemented an Alpha-smoothing low-pass filter for a "weightless" feel.

### [v0.1.0-alpha] - 2026-04-09
**Phase 1: Enterprise Foundation**
- **Project Scaffolding**: Configured KMP dependencies for SQLDelight, Koin, Coroutines, and Coil 3.
- **Domain & Data**: Defined `CreditCard` domain models and implemented SQLDelight database.
- **Hardware Interfaces**: Defined `CardScannerEngine` and `HardwareSensorEngine` interfaces.
- **UI Scaffolding**: Built the initial `WalletScreen` with Koin DI integration.