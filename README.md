# ExpenseTracker

ExpenseTracker is a modern, fintech-style mobile application built with Jetpack Compose. It allows users to track their daily income and expenses seamlessly with a sleek UI. This project was developed as a submission for a Mobile UI assignment.

## Features
- **Dark-First Modern UI**: Includes smooth gradients, glassmorphism elements, and high-quality Material 3 components.
- **Transaction Tracking**: Add transactions (income/expense) with custom categories, dates, and amounts.
- **Dynamic Analytics**: Includes pie charts for category breakdowns, monthly bar charts, and a generated Financial Health Score based on actual spending behaviors.
- **Data Persistence**: Uses Room Database for local caching of transactions, and DataStore for storing user preferences like dark/light mode toggle.
- **MVVM Architecture**: Follows best practices with separated Data, UI, and ViewModel layers.

## Tech Stack
- **Kotlin**: 2.0.21
- **UI Toolkit**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Local Database**: Room
- **Preferences**: DataStore
- **Navigation**: Navigation Compose

## Setup
1. Clone the repository.
2. Open the project in Android Studio (Jellyfish or later).
3. Let Gradle sync and download dependencies.
4. Run the app on a physical device or emulator. No internet connection required as all data is stored locally!

## Built With Quality Over Quantity
We made conscious decisions to focus on meaningful micro-interactions (e.g. animated pie charts, shimmer loading states) over creating empty placeholders. For example, instead of a static placeholder "credit score", your score dynamically adapts based on your tracking frequency and savings ratio!
