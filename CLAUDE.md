# CLAUDE.md

Финансовый трекер (FiananceTracker) — Android-приложение на Jetpack Compose для учёта доходов/расходов, работы со счетами и аналитики трат за период. Учебный проект (SHMR Android 2026). Данные берутся из реального REST API `https://shmr-finance.ru/api/v1/`.

## Поиск по коду

Всегда используй `ast-index` вместо grep для поиска по коду. После изменений в коде запускай `ast-index update`.

```
ast-index search <query>          # универсальный поиск (файлы + символы)
ast-index symbol <name>           # найти класс/интерфейс/функцию
ast-index outline <file>          # символы в файле
ast-index usages <symbol>         # использования
ast-index explore <query>         # релевантные символы + соседи + тесты
ast-index update                  # инкрементально обновить индекс
```

## Сборка и запуск

```
./gradlew assembleDebug           # сборка
./gradlew installDebug            # установка на устройство/эмулятор
./gradlew test                    # unit-тесты (JVM)
./gradlew connectedAndroidTest    # инструментальные тесты
```

Требуется токен API. Он читается из `local.properties` (ключ `TOKEN`) и прокидывается в `BuildConfig.TOKEN` через **`core/data/build.gradle.kts`** (модуль `:core:data`, namespace `com.example.fianancetracker.core.data`). Без валидного токена сетевые запросы вернут ошибку авторизации.

- `minSdk = 26`, `targetSdk = 36`, `compileSdk = 37`, Kotlin `2.2.10`, AGP `9.2.1` (built-in Kotlin — отдельный kotlin-android плагин не нужен), Compose BOM `2026.02.01`.
- **Многомодульный проект.** Версии зависимостей — в `gradle/libs.versions.toml`. Плагины объявлены в корневом `build.gradle.kts` c `apply false`, модули применяют их через `alias(...)` без версии.

## Архитектура

Многомодульная Clean Architecture: слои `core` + вертикальные `feature`. Граф зависимостей (стрелка = «зависит от»):

```
:app ──> :core:data, :core:ui, :feature:main, :feature:analytics
:feature:* ──> :core:ui, :core:domain
:core:data ──> :core:domain
:core:ui ──> :core:domain
```

- **`:core:domain`** (`...core.domain`) — бизнес-модели (`Account`, `Expense`, `Income`, `Transaction`, `Category`, `AnalyticsData`, `AnalyticsFilter`), `common/AppError.kt` (sealed-ошибки), интерфейсы репозиториев (`repository/`: `TransactionRepository`, `AccountRepository`, `AnalyticsRepository`). Без зависимостей от других модулей.
- **`:core:data`** (`...core.data`) — `network/api/FinanceApi.kt` (Retrofit), `AuthInterceptor`, `network/dto/`, `mapper/` (DTO → domain), `common/ApiCall.kt` (`apiCall {}` → `Result`, `bodyOrThrow`, `bodyOrNull`), `repository/*RepositoryImpl`, `di/NetworkModule.kt` (`networkModule` + `repositoryModule`). Держит `BuildConfig.TOKEN`.
- **`:core:ui`** (`...core.ui`) — тема, общие Compose-компоненты (`components/`), `model/ListItemModel`, `utils/` (`toThousand`, `toUserMessage`). Общий для всех фич.
- **`:feature:main`** (`...feature.main`) — **главный экран** под одну `MainViewModel`: `MainScreen` (единый `Scaffold` + нижние табы + FAB), `MainUiState` (`MainTab` + `TabContent` + `MainUiState`), `di/mainModule`. Табы Расходы/Доходы/Счета **не навигируют**, а меняют `selectedTab`; данные каждого таба грузятся **лениво** (первый заход) и кешируются. FAB → `addTransaction()` (заготовка, ждёт create-эндпоинт). Иконки `expenses.xml`/`accounts.xml` — в ресурсах этого модуля.
- **`:feature:analytics`** (`...feature.analytics`) — `AnalyticsScreen`/`ViewModel`/`State`/`UiState`, аналитико-специфичные `components/` (`bottom_sheets/`, `filters/`, `FinancePieChart`), ресурсы `ic_filter_*` + `di/analyticsModule`.
- **`:app`** (`com.example.fianancetracker`) — `FinanceApp` (Application, `startKoin { ... }` собирает все Koin-модули), `MainActivity` (поднимает `NavHost`), `presentation/navigation/` (`MainAppNavGraph`: `Splash → Main → Analytics`; `Screen`), `SplashScreen`, launcher-ресурсы. `MainScreen` принимает `onNavigateToAnalytics: () -> Unit` — навигация живёт в `:app`, чтобы `:feature:main` не зависел от `:feature:analytics`.

### Поток данных

`Screen (Compose, :feature)` → наблюдает `StateFlow<…UiState>` из `ViewModel` → `ViewModel` вызывает `Repository` (интерфейс из `:core:domain`) → `RepositoryImpl` (`:core:data`) дёргает `FinanceApi`, оборачивает в `Result` и мапит DTO в domain-модели.

## Конвенции

- **DI — Koin, по модулю на компонент.** Каждый модуль объявляет свой Koin-модуль (`:core:data` → `networkModule` + `repositoryModule`; каждая фича → свой `xxxModule` во `di/`), а `FinanceApp.startKoin { ... }` в `:app` собирает их все. Репозитории — `single<Interface> { Impl(get()) }`, ViewModel'и — `viewModel { ... }`, на экране `koinViewModel()`. Новая фича: свой Koin-модуль + подключить его в `FinanceApp`.
- **Главный экран — одна `MainViewModel` на весь `MainScreen`.** Табы (`MainTab`) переключают состояние списка, а не навигацию; данные таба грузятся **лениво** (`TabContent.NotLoaded` → `loadTab`) и кешируются в `MainUiState.tabs`. FAB и будущий add-флоу — тоже в этой VM. Остальные экраны (аналитика) — отдельные VM.
- **Состояния экрана — свой sealed-тип на фичу** (`…UiState` / `TabContent` с `Loading`/`Empty`/`Error(message)`/`Content(...)`), без общего дженерика. ViewModel выставляет `Loading`, дергает репозиторий и разбирает `Result` через `.onSuccess { }.onFailure { }`. Держись этого паттерна в новых ViewModel'ях.
- **Composable — «тупые», только рендер.** Никакой трансформации данных внутри composable: маппинг domain → `ListItemModel`, суммы/`toThousand`, форматирование дат, тексты бэйджей, эмодзи по типу — всё считает ViewModel и кладёт в state готовым (см. `MainViewModel`, `AnalyticsViewModel.applyFilters`). Composable принимает готовое состояние + колбэки и рисует. Исключения: локальное эфемерное UI-состояние виджетов ввода (боттом-шиты с выбором — `remember { mutableStateOf(...) }`) и статичные подписи/иконки. Эмодзи счёта — единый `core.ui.utils.accountEmoji(type)`.
- **Репозитории возвращают `Result<T>`.** Внутри — `apiCall { ... }` (из `:core:data`, пакет `...core.data.common`), тело ответа берётся через `bodyOrThrow()` (кидает `AppError.Server` при неуспехе) или `bodyOrNull()` (мягкий пропуск). Ошибки классифицируются в `AppError` (`NoInternet`/`Server`/`Unknown`). Маппинг в domain — только через функции-расширения из `:core:data` `mapper/`.
- **Ошибка → текст:** `Throwable.toUserMessage()` (`:core:ui`, `...core.ui.utils.ErrorMessages`) — единственное место, где `AppError` переводится в русскую строку. Не хардкодь тексты ошибок в ViewModel'ях.
- **Ресурсы живут в модуле-владельце.** `R` импортируй из namespace модуля (напр. `com.example.fianancetracker.feature.analytics.R`), а не из `:app`. Общие для всех фич иконки/строки — в `:core:ui`.
- **Cross-module smart-cast:** `val` из другого модуля не приводится по smart-cast (напр. `filter.customStartDate: LocalDate?`) — присвой в локальную `val` перед использованием.
- **Навигация:** роуты объявлены в `Screen` (sealed class). Нижняя навигация — `Screen.bottomNavItems()`, видимость бара определяется `Screen.isBottomNavRoute()`.
- **Строки UI на русском** (проект русскоязычный); сообщения об ошибках тоже.
- Расходы/доходы различаются по `category.isIncome` у транзакции — расход это `!isIncome`.

## Замечания

- `local.properties` и токен не коммитятся (в `.gitignore`). Не хардкодь токен в коде.
- Опечатка `Fianance` в namespace/имени проекта — намеренная (историческая), не «исправляй» её.
