# ExpenseTracker 💸

A modern, fintech-style expense tracking app built with **Jetpack Compose** for Android.

---

## Screenshots

| Home (Dark) | Home (Light) | Analytics | Profile |
|---|---|---|---|
| _(add screenshot)_ | _(add screenshot)_ | _(add screenshot)_ | _(add screenshot)_ |

> To add screenshots: run the app, take screenshots from the emulator/device (`adb exec-out screencap -p > screen.png`), and drop them in a `screenshots/` folder.

---

## Features

- **Gradient Balance Card** — credit-card style with animated shimmer
- **Dark / Light mode toggle** — persisted via DataStore, instant switch
- **Add income & expense** — amount, category, date picker, optional note
- **Form validation** — amount > 0, category required
- **Category-based tracking** — 9 expense + 6 income categories with icons and colours
- **Swipe to delete** — swipe left on any transaction
- **Favourites** — star any transaction; filter to view favourites only
- **Search** — live animated search bar across category + note fields
- **Weekly / Monthly filter** — segmented toggle on home screen
- **Analytics screen** — animated pie chart (category breakdown), animated bar chart (last 6 months — real data from DB), Financial Health Score gauge
- **Financial Health Score** — dynamically calculated from savings ratio, activity, and category diversification
- **Currency display toggle** — view amounts in INR / USD / EUR on analytics screen
- **Savings rate & daily average** — quick stat cards on analytics screen
- **Profile screen** — edit name/email, dark mode toggle, logout
- **Auth screen** — name + email onboarding (persisted locally)
- **Animated mesh background** — animated teal/purple glow blobs, dark & light variants
- **Empty states** — illustrated empty states for no transactions, no search results, no favourites
- **Bottom tab navigation** — Home, Analytics, Profile with animated transitions

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Local DB | Room (SQLite) |
| Preferences | DataStore |
| Navigation | Navigation Compose |
| Build | Gradle 8.11 / AGP 8.x |

---

## Setup

1. **Clone** the repository
   ```bash
   git clone https://github.com/<your-username>/ExpenseTracker.git
   ```
2. **Open** in Android Studio Jellyfish (2023.3.1) or later
3. Let **Gradle sync** complete (requires JDK 17+)
4. **Run** on a physical device (API 26+) or emulator — no internet needed

> All data is stored locally via Room. No backend, no API keys required.

---

## APK

A debug APK is available at `app/build/outputs/apk/debug/app-debug.apk` after building, or via the GitHub Releases page.

To build from source:
```bash
./gradlew assembleDebug
```

---

## Project Structure

```
app/src/main/java/com/minor/expensetracker/
├── data/
│   ├── local/          # Room DB, DAO, DataStore preferences
│   ├── model/          # Transaction, Category, enums
│   └── repository/     # TransactionRepository
├── ui/
│   ├── components/     # GlassEffect, GradientButton, EmptyState
│   ├── navigation/     # AppNavigation, BottomNavItem
│   ├── screens/
│   │   ├── auth/       # AuthScreen (onboarding)
│   │   ├── home/       # HomeScreen + BalanceCard, TransactionItem, SegmentedToggle
│   │   ├── stats/      # StatsScreen + CategoryPieChart, MonthlyBarChart, CreditScoreGauge
│   │   ├── profile/    # ProfileScreen
│   │   └── transaction/# AddTransactionSheet
│   ├── theme/          # Color, Type, Shape, Theme
│   └── viewmodel/      # TransactionViewModel, ProfileViewModel
└── MainActivity.kt
```
