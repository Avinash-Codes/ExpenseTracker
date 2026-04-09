# ExpenseTracker 💸

A modern, fintech-style expense tracking app built with **Jetpack Compose** for Android.

---

## Screenshots

> **How to add:** Run the app on an emulator, press Ctrl+S (Mac: Cmd+S) in Android Studio to capture screens, then drop them into a `screenshots/` folder and update the paths below.

| Home (Dark) | Home (Light) | Analytics | Profile |
|---|---|---|---|
| `screenshots/home_dark.png` | `screenshots/home_light.png` | `screenshots/analytics.png` | `screenshots/profile.png` |

---

## Features

### Core
- **Gradient Balance Card** — credit-card style with animated shimmer and dynamic expiry date
- **Dark / Light mode toggle** — persisted via DataStore, instant switch, full light-mode polish
- **Add income & expense** — amount, category picker, date picker, optional note
- **Form validation** — amount > 0 and category required before saving
- **Category-based tracking** — 9 expense + 6 income categories, each with a unique icon and colour

### Transactions
- **Swipe to delete** — swipe left on any transaction row with animated reveal
- **Tap to edit** — tap any transaction to open a full edit screen (change amount, category, date, note)
- **Delete with confirmation** — destructive delete guarded by an AlertDialog in the edit screen
- **Favourites** — star any transaction; filter the home list to starred items only
- **Search** — live animated search bar across category and note fields

### Home screen
- **Month navigator** — `< April >` chevrons let you browse any past month reactively
- **Weekly / Monthly filter** — segmented toggle switches the transaction list period
- **Summary cards** — live income, expense, and balance tiles for the selected period

### Analytics screen
- **Animated pie chart** — category breakdown for current month's expenses
- **Animated bar chart** — last 6 months of real expense data from Room (not hardcoded)
- **Financial Health Score** — 100–900 gauge dynamically calculated from savings ratio, activity, and diversification
- **Savings rate & daily average** — quick stat cards

### Profile screen
- **Edit name and email** — changes persisted to DataStore
- **Export to CSV** — exports all transactions to a CSV file and opens Android's share sheet
- **Appearance toggle** — dark/light mode switch with immediate effect

### Architecture & UX
- **Glassmorphism UI** — cards use glass effect that adapts correctly in both dark and light mode
- **Animated mesh background** — animated teal/purple glow blobs, theme-aware colours
- **Empty states** — illustrated states for no transactions, no search results, no favourites
- **Bottom tab navigation** — Home, Analytics, Profile with animated slide transitions
- **Keyboard handling** — `imePadding`, `ImeAction` chaining, focus traversal on all forms
- **Auth screen** — name + email onboarding, persisted via DataStore

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow + Flow) |
| Local DB | Room (SQLite) |
| Preferences | DataStore |
| Navigation | Navigation Compose |
| File sharing | FileProvider + Android share sheet |
| Build | Gradle 8.11 / AGP 8.x |

---

## Setup

1. **Clone** the repository
   ```bash
   git clone https://github.com/<your-username>/ExpenseTracker.git
   ```
2. **Open** in Android Studio Jellyfish (2023.3.1) or later
3. Let **Gradle sync** complete (requires JDK 17+)
4. **Run** on a physical device (API 26+) or emulator

> All data is stored locally. No backend, no API keys, no internet required.

---

## Building the APK

```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

---

## Project Structure

```
app/src/main/java/com/minor/expensetracker/
├── data/
│   ├── local/           # Room DB, DAO, DataStore PreferencesManager
│   ├── model/           # Transaction, Category, TransactionType
│   └── repository/      # TransactionRepository
├── util/
│   └── CsvExporter.kt   # CSV generation + FileProvider share sheet
├── ui/
│   ├── components/      # GlassEffect, GradientButton, EmptyState, AnimatedCounter
│   ├── navigation/      # AppNavigation, BottomNavItem
│   ├── screens/
│   │   ├── auth/        # AuthScreen (name + email onboarding)
│   │   ├── home/        # HomeScreen + BalanceCard, TransactionItem, SegmentedToggle
│   │   ├── stats/       # StatsScreen + CategoryPieChart, MonthlyBarChart, CreditScoreGauge
│   │   ├── profile/     # ProfileScreen (overview + edit tabs, export)
│   │   └── transaction/ # AddTransactionSheet, EditTransactionScreen
│   ├── theme/           # Color, Type, Shape, Theme (dark + light schemes)
│   └── viewmodel/       # TransactionViewModel, ProfileViewModel
└── MainActivity.kt
```
