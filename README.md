# Klivvr Android Assignment - City Search

## 1. Project Overview

This application is a solution to the Klivvr Android Developer assignment. It's a high-performance city searching app that allows users to filter through a list of over 200,000 cities in real-time. The project is built entirely with modern Android development practices, emphasizing clean architecture, efficient algorithms, and a polished, responsive user interface using Jetpack Compose.

The core challenge was to implement a search mechanism that is faster than linear time while handling a large local dataset and presenting the results in a sophisticated, animated UI with sticky headers.

---

## 2. Features

-   **⚡️ High-Performance Search:** Utilizes a **Trie (Prefix Tree)** data structure for near-instantaneous, case-insensitive prefix searches with `O(k)` time complexity (where `k` is the length of the prefix).
-   **💾 Efficient Memory Management:** Employs the **Paging 3** library to load and display the large list of cities efficiently, ensuring the app runs smoothly without `OutOfMemoryError` exceptions.
-   **📱 Modern & Responsive UI:** Crafted entirely with **Jetpack Compose**, featuring a clean, user-friendly interface that works seamlessly in both portrait and landscape orientations.
-   **🎨 Advanced UI Components:**
    -   **Sticky Headers:** Alphabetical headers stick to the top of the screen as the user scrolls through city groups.
    -   **Dynamic Search Bar:** The search bar animates its appearance and padding based on its focus state, creating an immersive user experience, especially when the keyboard is active.
-   **🧪 Unit Testing:** Includes unit tests for key architectural components, including the `Trie`, `CityRepoImpl`, `CityPagingSource`, and `CityViewModel`, to ensure logic correctness and reliability.
-   **🌗 Light & Dark Theme Support:** A custom, theme-aware color palette ensures the UI is aesthetically pleasing and legible in both light and dark modes.
-   **🗺️ Google Maps Integration:** Tapping on any city opens its location directly in the Google Maps app.
-   **🏗️ Clean & Scalable Architecture:** Built following MVVM and Clean Architecture principles, with clear separation of concerns between the UI, domain, and data layers.
-   **💉 Dependency Injection:** Uses **Hilt** for robust dependency injection, making the codebase modular, decoupled, and easily testable.

---

## 3. Architecture & Technical Decisions

This project was designed with maintainability, scalability, and performance as top priorities.

### 3.1. Overall Architecture: MVVM & Clean Principles

The app follows the **MVVM (Model-View-ViewModel)** pattern, which is highly recommended for modern Android development with Jetpack Compose.

-   **View (Composables):** The UI is built with stateless composables that observe state from the ViewModel.
-   **ViewModel (`CityViewModel`):** The ViewModel acts as the state holder and business logic processor. It does not have any reference to the Android framework's UI components, making it easy to unit test. It exposes UI state via `StateFlow`.
-   **Model (Repository & Data Sources):** The data layer is abstracted away by a `CityRepo` interface. This allows the data source to be changed (e.g., from a local JSON to a remote API) without affecting the ViewModel.

### 3.2. The Search Algorithm: Why a Trie?

The core requirement was a search algorithm with "better than linear" time efficiency.

-   **Problem:** A simple `list.filter { it.startsWith(prefix) }` would have a time complexity of `O(n)`, where `n` is the number of cities (200,000+). This would be too slow for a real-time search UI.
-   **Solution:** A **Trie (Prefix Tree)** was implemented.
    -   **Time Complexity:** A Trie provides `O(k)` search time, where `k` is the length of the search prefix. This is independent of the dataset size and is therefore extremely fast.
    -   **Implementation:** A custom `Trie` class was built from scratch. During app startup, the `CityRepository` reads the `cities.json` file in a background coroutine and inserts all 200,000+ cities into the Trie. This one-time setup cost makes all subsequent searches instantaneous.

### 3.3. Memory Management: Why Paging 3?

The initial challenge with loading a large dataset is memory. Loading all 200,000 cities into a simple list could cause memory issues on low-end devices.

-   **Solution:** The **Paging 3** library is used to handle data loading.
    -   The `CityRepository` provides a `Pager` that uses a custom `CityPagingSource`. This source takes the search results from the `Trie` and serves them in small, manageable "pages" on demand.
    -   This ensures that only the cities visible on the screen (plus a small buffer) are ever held in memory, making the app highly memory-efficient.

### 3.4. UI Implementation: Jetpack Compose

-   **State Management:** The UI observes a `StateFlow` from the `ViewModel`. This creates a unidirectional data flow, making the app's state predictable and easy to debug.
-   **Sticky Headers with Paging:** This was the most complex UI challenge. To solve it, the `ViewModel` uses the `insertSeparators` operator from the Paging 3 library to inject `HeaderItem` models into the `PagingData` stream. The `LazyColumn` in the UI then uses the `stickyHeader` function to correctly display these headers.
-   **Animations:** `animate*AsState` is used extensively to create smooth transitions for colors, padding, and corner radiuses in the search bar. `AnimatedVisibility` is used to fade the bottom bar's divider in and out.

---

## 4. How to Build and Run

1.  **Prerequisites:**
    -   Android Studio (latest stable version)
    -   JDK 17 or higher
2.  **Steps:**
    -   Clone the repository: `git clone <repository-url>`
    -   Open the project in Android Studio.
    -   Let Gradle sync and download all dependencies.
    -   Run the app on an emulator or a physical device.

---

## 5. Libraries Used

-   **Jetpack Compose:** For building the entire UI.
-   **Paging 3:** For efficient data loading and display.
-   **Hilt:** For dependency injection.
-   **Kotlin Coroutines & Flow:** For asynchronous operations and reactive data streams.
-   **JUnit & MockK & Turbine & coroutines-test :** For unit testing the application logic.
-   **Material 3:** For modern UI components and theming.
-   **Gson:** For parsing the initial JSON data.
