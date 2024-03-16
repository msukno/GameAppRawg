

# RAWG Game App 🎮
 
This is a gaming application that offers users experience in exploring and discovering games. This application is designed with a dual focus on providing fresh data when an internet connection is available and ensuring a good user experience even in offline mode by leveraging an caching system.
Application UI adapts to both mobile and tablet screens.

## Table of Contents 📚

1. [Database](#database)
2. [Repositories](#repositories)
3. [Pagination](#pagination)
4. [Workers](#workers)
5. [UI Components](#uicomponents)

## Database 💽

The local database is structured around several modules: `Game`, `Genre`, `GameImages`, `GenreImage`, and `GameFavorite`. Each module corresponds to a table entity and has an associated repository for data manipulation.

### Game 🎲

The `Game` module encapsulates essential information about each game. It also serves as the source for pagination, containing the necessary data for the Pager to paginate over the game items in the table effectively.

### Genre 🏷️

The `Genre` module encompasses all the information about a genre.

### GameImages 🖼️

The `GameImages` module stores the paths to images associated with each game, including background images and screenshots.

### GenreImage 🌄

The `GenreImage` module maintains the background image path associated with each genre.

## Repositories 📂

### Local Database Repositories 🗄️

Each local database module provides its own repository for data manipulation. The following repositories are available for inserting, updating, retrieving, and clearing data from the local database: `GameRepository`, `GenreRepository`, `GameFavoriteRepository`, `GameImageRepository`, `GenreImageRepository`.

### RAWG Repository 🌐

The `RawgRepository` interfaces with the RAWG API and provides methods for retrieving data from the remote RAWG database.

### Cache Repository 🗃️

The `ImageCacheRepository` offers methods for caching and clearing both game and genre images.

## Pagination 📄

Pagination is implemented using the `RemoteMediator` class. The `GameRepository` defines the appropriate methods that return the `PagingSource` object, which is passed to the `Pager` during initialization. The `RemoteMediator` uses the `RawgRepository` to fetch a new page each time the `Pager` requests it.

## Workers 👷

The project utilizes the `WorkManager` library to manage the tasks of updating and clearing the cache. Each worker class extends the `CoroutineWorker` and overrides the `doWork()` method. The available workers include `GameCacheWorker`, `GenreCacheWorker`, and `ClearCacheWorker`.

## UI Components 🖥️

The UI components are organized into folders based on their functionality. Each component has its own `ViewModel` for handling the logic. This separation of concerns enhances testability, improves state management, and ensures safer and more efficient data sharing.

### Screens 📺

The application features the following screens:

- `GenreSelectionScreen`: Displays a list of available genre categories. After selecting a genre, the user is redirected to the game list screen.
- `GameListScreen`: Shows a list of available games for the chosen genre. The user can sort the games by rating or release date.
- `GameDetails`: Provides additional information about a specific game, including the description, available platforms, and screenshots.
- `GameFavorite`: Displays a list of the user’s favorite games.
- `GameSearch`: Performs an online search and shows a list of games that match the search query by name.
- `AppSettings`: Offers options to return to genre selection and to clear the image cache.

Note:
- The app requires notifications to be enabled. To enable notifications on your android emulator device, navigate to the Android Settings menu > Apps > GameAppRawg > Notifications > Enable "All GameAppRawg notifications".


