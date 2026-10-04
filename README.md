# Employee Profile Management App (KMP)

A production-grade **Kotlin Multiplatform (KMP)** & **Compose Multiplatform** application for Android and iOS designed to manage employee profiles with local Room persistence, document attachments, real-time validation, and advanced algorithmic data operations.

---

## 🌟 Key Highlights & Architecture

- **Clean Architecture & Unidirectional Data Flow (UDF):** Clear separation across Domain, Data, and Presentation layers.
- **Shared Codebase:** 100% shared UI and business logic using Compose Multiplatform and Kotlin Coroutines/Flow.
- **Room KMP Persistence:** Local SQLite persistence with custom `TypeConverter`s, coroutine query flows, and background thread execution.
- **Platform Integrations (`expect/actual`):** Native Camera & Gallery image capture and PDF/DOC document picking with Android runtime permission management and iOS integration.
- **Dependency Injection:** Koin KMP for multiplatform dependency injection without manual ViewModel instantiations.
- **Material 3 Design System:** Comprehensive Light and Dark theme support with system toggle and runtime persistence.

---

## ⚙️ Data Structures & Algorithmic Complexities

All mandatory algorithmic operations are implemented in `commonMain` with documented time and space complexities:

### 1. Duplicate Detection (In-Memory HashSets)
- **Goal:** Real-time prevention of duplicate employee emails and normalized phone numbers before touching SQLite.
- **Time Complexity:** $O(1)$ average lookup time via `HashSet.contains()`.
- **Space Complexity:** $O(N)$ where $N$ is the total count of employee records.
- **Normalization:** Automatically strips `+91`, `0`, spaces, dashes, brackets, and periods before indexing. Allows self-matches in edit mode while preventing cross-employee collisions.

### 2. Top-N Highest Salaries (Bounded Min-Heap)
- **Goal:** Display top $N$ highest-earning employees with dynamic stepper controls ($N \in [1, 10]$).
- **Time Complexity:** $O(M \log N)$ where $M$ is total employees and $N$ is the top subset count.
- **Space Complexity:** $O(N)$ auxiliary space (bounded priority heap).
- **Why Min-Heap over full sorting?** Full sorting runs in $O(M \log M)$. When $N \ll M$ (e.g. top 5 out of 10,000 employees), iterating with a min-heap of fixed size $N$ is significantly faster and uses minimal memory.

### 3. Undo Delete (LIFO Stack)
- **Goal:** Restore accidentally deleted employee records within a 5-second snackbar window.
- **Time Complexity:** $O(1)$ push and pop operations.
- **Space Complexity:** $O(K)$ bounded to a maximum history depth of 10 items via `ArrayDeque`.

### 4. Full-Text Search (Prefix Trie Index)
- **Goal:** Fast multi-field token prefix queries across Name, Email, and Department with matched substring highlights.
- **Time Complexity:** $O(L)$ insertion per word token, $O(P)$ query time where $P$ is the search prefix length.

---

## 📊 Features & Bonus Checklist

| Section | Feature | Implementation Details | Status |
|---|---|---|---|
| **Section 1** | **Employee Form Screen** | Reusable create/edit form, real-time + blur validation, disabled submit state until 100% valid | ✅ Complete |
| **Section 2A** | **Profile Image Picker** | Camera & Gallery bottom sheet, circular 80dp avatar with camera overlay, local file storage | ✅ Complete |
| **Section 2B** | **Resume Document Upload** | PDF/DOC/DOCX picker, 5MB file-size validation with snackbar error, metadata persistence | ✅ Complete |
| **Section 3** | **Local Room DB** | Room KMP with Flow, Coroutines on `Dispatchers.IO`, `TypeConverter` for skills | ✅ Complete |
| **Section 4** | **Search, Filter & Sort** | Debounced search bar, multi-select bottom sheet filter with AND logic, sort dropdown | ✅ Complete |
| **Section 6** | **All 4 DSA Modules** | Duplicate detection $O(1)$, Top-N MinHeap $O(M \log N)$, Undo Stack $O(1)$, Trie index | ✅ Complete |
| **Bonus 1** | **Unit Tests (+4 pts)** | 18+ comprehensive tests covering DSA, Phone Normalizer, and Form Validators | ✅ Complete |
| **Bonus 2** | **Koin DI (+3 pts)** | Modular Koin setup in `commonMain` and platform-specific modules | ✅ Complete |
| **Bonus 3** | **Dark Mode (+2 pts)** | Material 3 light/dark palette with instant toggle and preference state | ✅ Complete |
| **Bonus 4** | **Animations (+2 pts)** | `AnimatedVisibility` for list item enter/exit transitions and route animations | ✅ Complete |

---

## 📁 Project Structure

```
EmployeeProfileKMP/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/kotlin/com/bookxpert/employeemanager/
│   │   │   ├── data/
│   │   │   │   ├── local/ (Room Database, Entity, Dao, TypeConverters, Preferences)
│   │   │   │   └── repository/ (EmployeeRepositoryImpl)
│   │   │   ├── domain/
│   │   │   │   ├── model/ (Employee, Department, Skill, EmploymentType, DocumentMetadata)
│   │   │   │   ├── repository/ (EmployeeRepository)
│   │   │   │   ├── dsa/ (MinHeap, DuplicateDetector, UndoStack, TrieSearchIndex)
│   │   │   │   └── util/ (PhoneNormalizer, FormValidator)
│   │   │   ├── presentation/
│   │   │   │   ├── theme/ (Color, Type, Theme with Dark Mode)
│   │   │   │   ├── components/ (Avatar, CustomTextField, Dropdown, SkillChips, DatePicker, DocumentCard)
│   │   │   │   ├── list/ (EmployeeListScreen, EmployeeListViewModel, EmployeeCard, FilterSheet, SortMenu)
│   │   │   │   ├── form/ (EmployeeFormScreen, EmployeeFormViewModel)
│   │   │   │   ├── detail/ (EmployeeDetailScreen)
│   │   │   │   ├── top_earners/ (TopEarnersScreen, TopEarnersViewModel)
│   │   │   │   └── navigation/ (NavGraph, Screen)
│   │   │   ├── platform/ (ImagePicker, FilePicker, PlatformUtils expect declarations)
│   │   │   ├── di/ (AppModule, initKoin)
│   │   │   └── App.kt (Root Composable)
│   │   ├── commonTest/kotlin/ (18+ Unit Tests: DSA, Normalizers, Validators)
│   │   ├── androidMain/ (Android actuals, MainActivity, EmployeeApplication)
│   │   └── iosMain/ (iOS actuals, MainViewController)
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
└── README.md
```

---

## 🛠️ Build & Run Instructions

### Prerequisites
- JDK 17
- Android Studio Ladybug / Meerkat or IntelliJ IDEA
- Xcode (for iOS simulator execution)

### Running Android Application
```bash
./gradlew installDebug
```
Or open the project root in Android Studio and click **Run 'composeApp'**.

### Running Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Generated Debug APK
The compiled debug APK is located at:
```
composeApp/build/outputs/apk/debug/composeApp-debug.apk
```
