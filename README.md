# HelpArticles

HelpArticles is an Android application built using the MVI (Model–View–Intent) architecture pattern and Jetpack Compose for declarative UI. It uses Hilt for dependency injection and a Kotlin Multiplatform (KMP)  ~~in-memory~~ file-based cache to provide offline support.
 The project demonstrates predictable state management, explicit data refresh rules, and robust error handling, all without relying on a traditional database.

## Architecture & Key Decisions
- **MVI Pattern**:
  - **Intent**: User actions or lifecycle events (e.g. app resume) that trigger data loading.
  - **Model**: Immutable UI state exposed as a `Flow`.
  - **View**: Observes state changes and renders the UI accordingly.
- **Repository pattern**: Acts as the single source of truth by coordinating network responses and the KMP cache.
- **Hilt (Dependency Injection)**:
  - Manages creation and lifecycle of repositories, network clients, interceptors, and cache components.
  - Improves testability and enforces clear dependency boundaries.
- **~~In Memory Cache~~ File-based Cache**: Data persistence and offline support are handled exclusively via the KMP cache. the cache is highly configurable and storage method can changed with ease (Ex: Any moment if api response significantly grows and file system seems inefficient, storage can be switched to sqldelight or any other local database.)
- **Mocked API using OkHttp Interceptor**:
  - API responses are mocked at the network layer using a custom OkHttp interceptor.
  - This allows realistic networking behavior without a real backend.
- **Coroutines & Flow**: Used for asynchronous operations and reactive state updates.

## Connectivity/Transport, vs Backend Errors
- **Connectivity / transport errors**: Result in exceptions (UnknownHostException, SocketTimeoutException).
- **Backend errors**: Simulated via mocked HTTP status codes (5xx, for now only 500)  in the interceptor and mapped to meaningful UI states.
- Errors are propagated using **sealed result types**, enabling precise UI reactions.

## Data Refresh & Prefetch Strategy
There is **no continuous auto-refresh**. Data updates are triggered explicitly in two cases:
1. **Prefetch every 24 hours**:  
   Cached articles are refreshed when they exceed the defined staleness window.
2. **On app resume**:  
   When a screen becomes visible (newly opened or returning from the background), it refreshes data from the cache or falls back to the API if the cached data is stale.

## Background Prefetching & Scheduling
WorkManager is used to run scheduled prefetch in the background because:
1. **Reliable Background Execution**: Ensures tasks like article prefetching run even if the app is closed, the device restarts, or network conditions change.
2. **Flexible & Efficient Scheduling**: Handles tasks based on conditions like battery level and network availability, runs without keeping the app open, survives device restarts, and automatically reschedules on failure.

## Staleness / Expiry & KMP Cache
- **Staleness rule**: Cached articles older than **24 hours** are considered stale and eligible for refresh.
- **KMP cache**:
  - Caches articles and their metadata  is cached in  File Storage and maintain TTL/staleness logic (initially this was in memory cache, but since using background prefetching, it only makes sense to persist cache across app restart).
  - Enables offline access and controlled refresh behavior.
  - Avoids redundant network calls while keeping the UI responsive.

## Testing
- Included one unit test in the shared code for confirming cache or staleness logic.
- One Compose UI test (flow: error state + Retry interaction).
## Mock-Interceptor VS TestStub-Interceptor
- **Mock-Interceptor**: Can configure response probablity. Able to handle various types of Error.

- **Stub-Interceptor**: Replaces Mock-Interceptor in UI Test to stub response as per test scenario.
 
