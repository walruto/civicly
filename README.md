<div align="center">
  <img src="app/src/main/res/drawable/civicly_logo.png" alt="Civicly logo" width="220" />

  <h3>Civic engagement, simplified.</h3>

  <p>
    A modern Android app that makes local government, elections, legislation,
    officials, and community news easier to discover and understand.
  </p>

  <p>
    <img alt="Android" src="https://img.shields.io/badge/Android-24%2B-3DDC84?logo=android&logoColor=white" />
    <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?logo=kotlin&logoColor=white" />
    <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white" />
    <img alt="Supabase" src="https://img.shields.io/badge/Backend-Supabase-3FCF8E?logo=supabase&logoColor=white" />
  </p>
</div>

---

## About Civicly

**Civicly** is a local civic information app designed to reduce the friction between people and the government decisions that affect their everyday lives.

Instead of forcing users to jump between government portals, long ballot documents, news sites, and representative directories, Civicly brings important local information into one clean mobile experience. The app is built around readable summaries, source transparency, location-based relevance, and access to multiple viewpoints rather than steering users toward a political side.

The current project includes live Supabase-backed civic data alongside several seeded local-information screens used to demonstrate the broader product experience.

## Features

- **Personalized onboarding** — Collect a name, gender preference, and district/location to shape the local experience.
- **Local civic feed** — Surface current ballot measures and civic updates in a mobile-first feed.
- **Plain-language bill details** — Show summaries, election dates, jurisdiction, vote meaning, fiscal impact, topic tags, and original sources when available.
- **Unified search** — Search across loaded bills and news from one interface.
- **Bias context for news** — Associate news outlets with bias and factual-rating metadata so users can understand the source context themselves.
- **Local news discovery** — Browse civic news with summaries, location, topic, impact, imagery, and links to original reporting.
- **Officials directory** — Explore public officials and jump to official websites.
- **Ordinance browser** — Review local ordinances with short explanations and direct source links.
- **Community events** — Discover civic and community events in a dedicated view.
- **Profile experience** — Edit local profile information and preview saved-content functionality.
- **Candidate matching foundation** — Includes a five-topic questionnaire and client-side matching logic for comparing user answers with candidate position data.
- **Responsive Compose UI** — Custom Material 3 styling, animated onboarding, cards, chips, sheets, navigation, and image loading.

> **Current data status:** bills, article detail/search data, bias ratings, and candidate data are connected to Supabase. Some feed cards, profile actions, and the dedicated News / Officials / Ordinances / Events screens currently use seeded or demo content while their backend integrations are completed.

## Tech Stack

| Layer | Technology |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Android | minSdk 24, targetSdk 36 |
| Architecture | Compose screens + ViewModels + StateFlow |
| Networking | Retrofit 2 + OkHttp |
| JSON | Gson |
| Backend | Supabase Postgres + PostgREST |
| Images | Coil Compose |
| Database features | PostgreSQL, Row Level Security, `pgvector` extension |
| Build system | Gradle Kotlin DSL |

## App Flow

```text
Splash
  ↓
Personalization
  ↓
Home Feed ───────────────┐
  │                      │
  ├── Search             ├── Profile
  │    ├── Ordinances    │
  │    ├── Officials     │
  │    ├── News          │
  │    └── Events        │
  │                      │
  └── Article / Bill ────┘
```

The app currently uses a lightweight route state in `MainActivity.kt`, keeping navigation easy to follow while the product is still evolving.

## Architecture

Civicly separates UI, state, networking, and backend models into focused packages:

```text
app/src/main/java/com/example/civicly/
├── MainActivity.kt
├── data/
│   ├── ApiErrors.kt
│   ├── Models.kt
│   ├── SupabaseApi.kt
│   └── SupabaseClient.kt
├── debug/
│   └── DebugLog.kt
└── ui/
    ├── article/
    ├── bills/
    ├── district/
    ├── events/
    ├── feed/
    ├── match/
    ├── news/
    ├── newslist/
    ├── officials/
    ├── ordinances/
    ├── personalization/
    ├── profile/
    ├── search/
    ├── splash/
    └── theme/

supabase/
└── schema.sql
```

### Data flow

```text
Compose Screen
     ↓ observes
ViewModel / StateFlow
     ↓ requests
SupabaseApi (Retrofit)
     ↓
Supabase PostgREST
     ↓
PostgreSQL + RLS
```

`NewsViewModel` also combines article records with source bias-rating records before exposing them to the UI.

## Supabase Schema

The included [`supabase/schema.sql`](supabase/schema.sql) defines the current backend model, including:

- `jurisdiction`
- `users`
- `elections`
- `bills`
- `arguments`
- `sources`
- `key_dates`
- `candidates`
- `bias_ratings`
- `articles`
- `match_scores`

Public civic datasets use read-only Row Level Security policies, while user and match-score tables include owner-based policies using `auth.uid()`.

The schema also enables PostgreSQL's `vector` extension for future preference-vector and similarity-based civic matching work.

## Getting Started

### Prerequisites

Make sure you have:

- **Android Studio** with Android SDK support
- **JDK 11+**
- An Android emulator or physical Android device running **Android 7.0 / API 24+**
- Internet access for Gradle dependencies and Supabase requests

### 1. Clone the repository

```bash
git clone <YOUR_REPOSITORY_URL>
cd civicly
```

### 2. Open the project

Open the repository root in **Android Studio** and allow Gradle to sync.

You can also build from the terminal:

```bash
./gradlew assembleDebug
```

On Windows:

```powershell
gradlew.bat assembleDebug
```

### 3. Run the app

Select an emulator or connected Android device in Android Studio and press **Run**.

## Configuring Your Own Supabase Project

A schema is included so you can recreate the backend in your own Supabase project.

1. Create a new project in Supabase.
2. Open **SQL Editor**.
3. Run [`supabase/schema.sql`](supabase/schema.sql).
4. Add or import your civic data.
5. Update the Supabase project URL and anon key used by `SupabaseClient.kt`.

For a production deployment, keep environment-specific configuration outside source files—for example through `local.properties`, generated `BuildConfig` fields, or another secure configuration workflow.

> A Supabase **anon** key is intended for client-side use, but database access still depends on correctly configured Row Level Security policies. Never place a Supabase `service_role` key inside the Android app.

## API Endpoints Used

The Android client currently reads from these Supabase REST endpoints:

```text
GET /rest/v1/bills
GET /rest/v1/articles
GET /rest/v1/bias_ratings
GET /rest/v1/candidates
```

Retrofit automatically maps snake_case database fields into the app's camelCase Kotlin models through Gson's `LOWER_CASE_WITH_UNDERSCORES` naming policy.

## Civic Data Philosophy

Civicly is designed around a few core principles:

1. **Make the source visible.** Users should be able to reach the original government document or reporting source.
2. **Summarize without hiding context.** Plain-language explanations should make civic information easier to understand, not replace the underlying record.
3. **Show perspective instead of choosing one.** Source and bias context should help users decide what they want to read rather than push them toward a side.
4. **Prioritize local relevance.** Civic information becomes more useful when it is connected to the user's district, city, and county.
5. **Keep the experience approachable.** Government information should feel as easy to browse as any modern consumer app.

## Current Development Status

Civicly is an active prototype. The core UI and Supabase read layer are in place, with a mix of production-connected and demonstration features.

| Area | Status |
| --- | --- |
| Splash & onboarding | ✅ Implemented |
| Personalization UI | ✅ Implemented |
| Supabase bills | ✅ Connected |
| Article / bill details | ✅ Connected |
| Search across bills + articles | ✅ Connected |
| News bias metadata | ✅ Connected |
| Candidate data + matching logic | 🟡 Foundation implemented |
| Dedicated local news screen | 🟡 Seeded content |
| Officials directory | 🟡 Seeded content |
| Ordinances | 🟡 Seeded content |
| Events | 🟡 Seeded content |
| Civic polls / town hall cards | 🟡 UI prototype |
| Saved articles / profile persistence | 🟡 UI prototype |
| Full authentication | ⬜ Planned |
| Push notifications | ⬜ Planned |

## Roadmap

Planned directions for the project include:

- Connect all local-information screens to live civic data sources.
- Add authentication and persistent user profiles.
- Resolve ZIP codes / addresses into real jurisdictions and representatives.
- Add user-selected interests and notification preferences.
- Add election dates, local deadlines, and civic alerts.
- Expand candidate comparison and matching.
- Persist saved articles and followed issues.
- Add source verification and freshness metadata throughout the UI.
- Improve navigation architecture as the screen graph grows.
- Add unit, integration, and Compose UI test coverage for production flows.

## Contributing

Contributions are welcome. For substantial changes, open an issue first so the implementation and data model can be discussed before development begins.

A good contribution should:

- Keep civic summaries neutral and source-backed.
- Prefer official or clearly attributable data sources.
- Avoid hardcoding location-specific behavior when it can be modeled as data.
- Preserve the existing Compose design language.
- Include tests when adding non-trivial business logic.

## Disclaimer

Civicly is an independent civic-information project and is **not affiliated with, endorsed by, or operated by any government agency**.

Information shown in the app should be verified against the linked official source before it is used for voting, legal, financial, or other high-impact decisions.

## License

No license file is currently included in this repository. Until a license is added, the source code remains under the default copyright protections of its owner.

---

<div align="center">
  <strong>Civicly</strong><br />
  Stay informed. Understand what matters locally. Make your own decision.
</div>
