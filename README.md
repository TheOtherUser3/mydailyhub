# My Daily Hub – Notes, Tasks, and Calendar with Bottom Navigation

This project demonstrates how to build a **multi-screen productivity app** in **Jetpack Compose (Material 3)** using **bottom navigation** and proper **state management with ViewModel**.  
The app includes three functional screens — Notes, Tasks, and Calendar — with smooth animated transitions and persistent UI state.

---

## Features
- **Three Functional Screens**
  - **Notes:** Create and view text notes.
  - **Tasks:** Manage a list of checkable tasks.
  - **Calendar:** Static placeholder view for demonstration.
- **Bottom Navigation Bar**
  - Implemented with `NavigationBar` and `NavigationBarItem` (Material 3 versions).
  - Dynamic highlighting using `currentBackStackEntryAsState()`.
- **Navigation Architecture**
  - Sealed `Route` objects for all destinations.
  - Controlled transitions with `popUpTo`, `launchSingleTop`, and `restoreState = true` to avoid duplicate destinations.
- **State Preservation**
  - ViewModels (`NotesViewModel`, `TasksViewModel`) retain user data across recompositions and configuration changes.
- **Animated Transitions**
  - Screen transitions fade in/out based on a navigation argument (`anim`), implemented using `AnimatedVisibility` with `fadeIn` and `fadeOut`.
- **Material 3 Design**
  - Fully styled with Material 3 components: `Scaffold`, `NavigationBar`, `OutlinedTextField`, `TopAppBar`, `Button`, and `Card`.

---

## Technologies Used
- Jetpack Compose (Material 3)
- Navigation for Compose
- Lifecycle ViewModel
- Kotlin Coroutines (Compose runtime)
- AnimatedVisibility (Compose Animation)
- Scaffold-based responsive layout

---

## Backstack Behavior
- When switching tabs, navigation uses:
  ```kotlin
  popUpTo(navController.graph.findStartDestination()) {
      saveState = true
  }
  launchSingleTop = true
  restoreState = true

## Documentation on AI Usage and Navigation Misunderstandings

**AI Usage:**  
This app was developed with assistance from **OpenAI’s ChatGPT (GPT-5, October 2025)** to accelerate layout creation, navigation setup, and state-management integration.  
I used AI for structural and boilerplate tasks, including ViewModel setup, sealed-route patterns, and bottom-navigation logic.  I first used it to help me come up with ideas
for the architecture, taking some of them, modyfing some of them, or using them to come up with my own completely different ideas, then had it slowly help me make that into code.
After generating the base code and writing the rest, I personally revised and tested all navigation transitions, lifecycle handling, and UI behavior to ensure correctness.

**Where AI Misunderstood Navigation:**  
AI initially attempted to use `rememberNavController()` inside individual composables (like each screen), which would have created multiple isolated navigation graphs, breaking backstack control and tab restoration.  
It also suggested using `Crossfade` for animations, which caused runtime instability and unnecessary parameter warnings.  
I corrected this by:
- Creating one single `NavController` at the app level.  
- Passing navigation actions downward (instead of creating new controllers).  
- Replacing `Crossfade` with `AnimatedVisibility` and `fadeIn`/`fadeOut` animations.  
