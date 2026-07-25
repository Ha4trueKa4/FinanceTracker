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

Требуется токен API. Он читается из `local.properties` (ключ `TOKEN`) и прокидывается в `BuildConfig.TOKEN` через `app/build.gradle.kts`. Без валидного токена сетевые запросы вернут ошибку авторизации.

- `minSdk = 26`, `targetSdk = 36`, Kotlin `2.2.10`, Compose BOM `2026.02.01`.
- Единственный модуль — `:app`. Namespace: `com.example.fianancetracker`. Версии зависимостей — в `gradle/libs.versions.toml`.

## Архитектура

Clean Architecture в трёх слоях внутри одного модуля. Пакет `com.example.fianancetracker`:

- **`domain/`** — бизнес-модели (`Account`, `Expense`, `Income`, `Transaction`, `Category`, `AnalyticsData`), `common/AppError.kt` (sealed-ошибки) и интерфейсы репозиториев (`domain/repository/`). Слой без зависимостей от Android/сети.
- **`data/`** — реализация:
  - `network/api/FinanceApi.kt` — Retrofit-интерфейс к API.
  - `network/api/AuthInterceptor.kt` — добавляет Bearer-токен.
  - `network/dto/` — DTO под ответы API (`AccountDto`, `CategoryDto`, `TransactionResponseDto`).
  - `mapper/` — маппинг DTO → domain (`TransactionMappers.kt`, `AccountMappers.kt`).
  - `common/ApiCall.kt` — `apiCall {}` (оборачивает в `Result`, классифицирует ошибки в `AppError`), `bodyOrThrow`, `bodyOrNull`.
  - `repository/` — `*RepositoryImpl`, реализуют интерфейсы из `domain/repository/`.
- **`presentation/`** — UI на Compose:
  - `MainActivity.kt` — точка входа, здесь же `startKoin { ... }` (нет кастомного `Application`-класса).
  - `screens/` — экраны (`TransactionsScreen` — общий для расходов и доходов, `AccountsScreen`, `AnalyticsScreen`, `MainScreen`, `SplashScreen`).
  - `navigation/` — `MainAppNavGraph` (NavHost) и `Screen` (роуты + элементы нижней навигации).
  - `viewmodel/` — ViewModel'и + свой sealed-`*UiState` на каждую фичу (`TransactionsUiState`, `AccountsUiState`, `AnalyticsUiState`). Общего дженерик-`UiState<T>` нет — сознательно, чтобы состояние было строго типизировано под экран.
  - `components/` — переиспользуемые Compose-компоненты: `common/`, `bottom_sheets/`, `filters/`.
  - `theme/`, `model/`, `utils/`.
- **`di/`** — Koin-модули: `networkModule` (OkHttp/Retrofit/API), `repositoryModule` (репозитории), `viewModelModule`.

### Поток данных

`Screen (Compose)` → наблюдает `StateFlow<UiState<T>>` из `ViewModel` → `ViewModel` вызывает `Repository` (domain-интерфейс) → `RepositoryImpl` дёргает `FinanceApi` и мапит DTO в domain-модели.

## Конвенции

- **DI — Koin.** Репозитории регистрируются как `single<Interface> { Impl(get()) }`, ViewModel'и — через `viewModel { ... }`. VM с параметром — через `viewModel { params -> Vm(params.get(), get()) }`, а на экране получается как `koinViewModel { parametersOf(...) }` (см. `TransactionsViewModel` — один класс на экраны расходов и доходов, различается флагом `isIncome`).
- **Состояния экрана — свой sealed `*UiState` на фичу** (`Loading` / `Content(...)` / `Empty` / `Error(message)`), без общего дженерика. ViewModel выставляет `Loading`, дергает репозиторий и разбирает `Result` через `.onSuccess { }.onFailure { }`. Держись этого паттерна в новых ViewModel'ях.
- **Репозитории возвращают `Result<T>`.** Внутри — `apiCall { ... }` (из `data/common/`), тело ответа берётся через `bodyOrThrow()` (кидает `AppError.Server` при неуспехе) или `bodyOrNull()` (мягкий пропуск). Ошибки классифицируются в `AppError` (`NoInternet`/`Server`/`Unknown`). Маппинг в domain — только через функции-расширения из `data/mapper/`.
- **Ошибка → текст:** `Throwable.toUserMessage()` (`presentation/utils/ErrorMessages.kt`) — единственное место, где `AppError` переводится в русскую строку. Не хардкодь тексты ошибок в ViewModel'ях.
- **Навигация:** роуты объявлены в `Screen` (sealed class). Нижняя навигация — `Screen.bottomNavItems()`, видимость бара определяется `Screen.isBottomNavRoute()`.
- **Строки UI на русском** (проект русскоязычный); сообщения об ошибках тоже.
- Расходы/доходы различаются по `category.isIncome` у транзакции — расход это `!isIncome`.

## Замечания

- `local.properties` и токен не коммитятся (в `.gitignore`). Не хардкодь токен в коде.
- Опечатка `Fianance` в namespace/имени проекта — намеренная (историческая), не «исправляй» её.
