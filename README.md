# GiphyChili

An Android application for browsing and searching GIFs using the **Giphy public API**.  
This project demonstrates clean architecture with **Jetpack Compose**, **Hilt**, and **Paging 3**, including **fully tested pagination logic**.

---

## Overview

GiphyChili enables users to:
- Search for GIFs through the Giphy API.
- Browse results with infinite scrolling using Paging 3.
- View any GIF in full size on a separate detail screen.
- Navigate back seamlessly using Material 3 components.
- Experience smooth image loading and caching via Coil.

---

## Tech Stack

| Layer | Technology |
|-------|-------------|
| **Language** | Kotlin |
| **UI** | Jetpack Compose (Material 3, LazyColumn, AsyncImage with Coil) |
| **Architecture** | MVVM + Clean Architecture principles |
| **Dependency Injection** | Hilt |
| **Networking** | Retrofit + OkHttp |
| **Pagination** | Paging 3 |
| **Image Loading** | Coil |
| **Testing** | JUnit4 + MockWebServer |
| **Build System** | Gradle (Kotlin DSL) |

---

## Key Components

| Component | Description |
|------------|-------------|
| `GiphyApi` | Retrofit interface for searching GIFs via the Giphy API. |
| `GiphyPagingSource` | Custom PagingSource managing offset/count-based pagination. |
| `SearchViewModel` | Handles search logic, paging flow, and state management via `StateFlow`. |
| `MainActivity` | Entry point hosting the search route and ViewModel. |
| `DetailActivity` | Displays selected GIFs with a top app bar and back navigation. |

---

## Testing

Unit tests ensure correctness of pagination and data loading.
