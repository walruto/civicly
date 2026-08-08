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

  <p>
    <a href="#about-civicly">About</a> ·
    <a href="#features">Features</a> ·
    <a href="#architecture">Architecture</a> ·
    <a href="#current-development-status">Status</a> ·
    <a href="#implementation-priorities">Priorities</a> ·
    <a href="#development-roadmap">Roadmap</a>
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

## Product Vision

Civicly's long-term goal is to become more than a local news reader. The product should function as a **personalized civic dashboard for everything happening around a user's location**.

Instead of making users search across government websites, news outlets, ballot guides, meeting calendars, and representative directories, Civicly should answer four simple questions:

> **What is happening near me?**  
> **Why does it matter?**  
> **Who represents me?**  
> **What can I do next?**

The intended experience is:

```text
User
  ↓
Location + Interests
  ↓
Jurisdiction Resolution
  ├── City
  ├── County
  ├── State districts
  ├── Congressional district
  ├── School district
  └── Other local districts
  ↓
Relevant Civic Data
  ├── News
  ├── Bills & ordinances
  ├── Elections
  ├── Officials
  ├── Meetings & events
  └── Deadlines
  ↓
Personalized Feed + Alerts + Civic Actions
```

That location-driven model is the biggest architectural step between the current prototype and the full Civicly product.

## Implementation Priorities

These are the most important features to implement before expanding the product further.

| Priority | Feature | Goal |
| --- | --- | --- |
| **P0** | **Live data across every major screen** | Replace seeded Officials, Events, Ordinances, and dedicated News content with real backend/API data. |
| **P0** | **Location → jurisdiction engine** | Resolve a ZIP code or address into the user's city, county, districts, representatives, measures, elections, and other relevant local data. |
| **P0** | **Persistent user profile** | Save name, location, interests, preferences, followed topics, and other settings between sessions. |
| **P0** | **Functional article actions** | Wire source links, share, bookmark, feedback, and related actions to real behavior. |
| **P0** | **Working feed filters** | Make topic chips and category filters actually change the displayed feed. |
| **P1** | **Saved articles** | Connect bookmark controls and the Saved Articles section to persistent storage. |
| **P1** | **Officials database** | Show the user's real federal, state, county, city, school-board, and other applicable representatives. |
| **P1** | **Civic events backend** | Populate meetings, hearings, town halls, election dates, public-comment deadlines, and community events from real sources. |
| **P1** | **Bill & ordinance tracking** | Track legislation through introduction, committee, hearing, vote, passage, rejection, and implementation. |
| **P1** | **Notification system** | Deliver alerts for elections, followed issues, meetings, bills, deadlines, and urgent local updates. |
| **P1** | **Interest-based onboarding** | Let users choose topics such as Housing, Transit, Education, Elections, Environment, Budget, and Public Safety. |
| **P1** | **Candidate Match experience** | Surface the existing candidate-position and matching foundation through polished app navigation and comparison screens. |

### What should change first

The current product is roughly:

```text
User → UI → Generic Civic Data
```

The target architecture should become:

```text
User → Location + Interests → Jurisdictions → Relevant Civic Data → Personalized Experience
```

That change should drive most future development decisions.

## Recommended Product Features

Once the core data and persistence layers are solid, these features would make Civicly substantially more useful and differentiated.

### My Area

A single location-driven dashboard showing everything relevant to the user.

**Potential content:**

- City and county
- Congressional district
- State Assembly / Senate districts
- City Council district
- School district
- Representatives
- Current ballot measures
- Major local issues
- Upcoming meetings
- Upcoming elections
- Important civic deadlines

The goal is simple: **everything politically and civically relevant around the user, in one place.**

### Follow an Issue

Allow users to follow topics such as:

`Housing` · `Transit` · `Public Safety` · `Education` · `Environment` · `Budget`

Civicly can then personalize the feed and notify users when something meaningful changes.

Example:

> **Housing Update**  
> City Council will vote Tuesday on a proposed 310-unit housing development.

### Bill & Measure Timelines

Turn legislation into an understandable visual timeline rather than a static document.

```text
Introduced → Committee → Public Hearing → Vote → Passed / Rejected
```

Each page should clearly show the current stage, previous actions, next scheduled action, and linked official source.

### Why This Matters

Every major civic item should answer the same questions consistently:

| Section | Purpose |
| --- | --- |
| **What happened?** | Short plain-language explanation. |
| **Why it matters** | Practical impact on the community. |
| **Who it affects** | Residents, renters, students, drivers, businesses, voters, etc. |
| **What happens next** | Upcoming vote, meeting, deadline, implementation date, or next step. |
| **Official source** | Direct link to the underlying government document or trusted primary source. |

This format should become one of Civicly's core design patterns.

### Source Comparison

For important stories, group coverage of the same event so users can choose what they want to read.

```text
Local housing proposal approved

LEFT        CENTER        RIGHT        OFFICIAL
Article A   Article B     Article C    City Council Record
```

Civicly should **show perspective rather than select a perspective for the user**. Source labels and bias metadata provide context; the user decides which coverage to open.

### Official-Source-First Mode

Add a source filter such as:

`All` · `News` · `Official Sources`

Official Sources could include:

- City and county agendas
- Election offices
- Legislation and ordinances
- Meeting minutes
- Ballot guides
- Public notices
- Government reports

This would make Civicly useful even for users who prefer primary sources over news coverage.

### Election Center

Create a dedicated election experience that activates around upcoming elections.

**Example:**

```text
November 3 Election
87 days remaining

My Ballot
├── Mayor
├── City Council
├── School Board
├── Local Measures
└── State Propositions
```

It should also include:

- Registration deadline
- Vote-by-mail information
- Voting locations
- Election Day information
- Candidate profiles
- Candidate comparisons
- Measure summaries
- Official election resources

### Candidate Comparison

Expand the existing matching foundation into direct side-by-side comparisons.

| Issue | Candidate A | Candidate B |
| --- | --- | --- |
| Housing | Position + source | Position + source |
| Transit | Position + source | Position + source |
| Public Safety | Position + source | Position + source |
| Taxes | Position + source | Position + source |
| Education | Position + source | Position + source |

Any position shown in Civicly should link back to the evidence used to represent that position.

### Upcoming Deadlines

Give users one chronological place to see what is approaching.

```text
Aug 12  City Council meeting
Aug 16  Public comment closes
Aug 22  Planning Commission hearing
Oct 19  Voter registration deadline
Nov 03  Election Day
```

Each eligible item should support **Remind Me** or **Add to Calendar**.

### Civic Actions

Civicly should help users move from reading to participating.

Relevant content can expose actions such as:

- **Read proposal**
- **View agenda**
- **Watch meeting**
- **Submit public comment**
- **Contact representative**
- **Add to calendar**
- **Get directions to polling location**

The goal is to make useful civic actions available at the exact point where the information becomes relevant.

## Development Roadmap

### Phase 1 — Make Every Existing Screen Real

- Connect Officials, Events, Ordinances, and News to live data.
- Remove hardcoded production-facing content.
- Complete article source, share, save, and feedback actions.
- Implement real feed filtering.
- Persist profile information.

### Phase 2 — Location Intelligence

- Accept ZIP code, city, current location, or full address.
- Resolve the user into applicable jurisdictions and districts.
- Use jurisdiction IDs throughout the data model instead of location-specific hardcoding.
- Filter feeds, officials, elections, ordinances, and events automatically by location.

### Phase 3 — Personalization

- Add topic selection during onboarding.
- Add followed issues.
- Build personalized feed ranking around location + interests.
- Save notification preferences.
- Persist bookmarks and followed content.

### Phase 4 — Alerts & Deadlines

- Add push notifications.
- Notify users about elections, meetings, bills, deadlines, and followed topics.
- Add reminder controls and calendar integration.
- Build a dedicated notifications inbox.

### Phase 5 — My Area & Election Center

- Build the My Area civic dashboard.
- Add all applicable representatives and districts.
- Add election countdowns and personalized ballot views.
- Add voting information, deadlines, candidate profiles, and ballot measures.

### Phase 6 — Source Transparency

- Group multiple articles about the same civic story.
- Add Left / Center / Right / Official source comparison.
- Surface publication time, source ownership, bias metadata, factual-rating metadata, and freshness where available.
- Add an Official Sources filter.

### Phase 7 — Candidate Match & Comparison

- Finish the Candidate Match UI.
- Connect matching to real candidate records.
- Add side-by-side issue comparisons.
- Require source attribution for candidate positions.
- Clearly distinguish factual candidate data from generated summaries or interpretations.

### Phase 8 — Civic Participation

- Add meeting streams and agendas.
- Link public-comment forms.
- Add representative contact actions.
- Add calendar actions and polling-location directions.
- Create clear next-step actions for relevant civic updates.

## Future Ideas

After the core Civicly experience is reliable, future expansions could include:

- Interactive civic maps
- School-board information
- City and county budget visualizations
- Campaign-finance and donor information
- Live election-result tracking
- Representative voting histories
- Public-meeting transcript summaries
- Public-comment summaries
- Local polling and surveys
- Shareable civic-information cards
- Home-screen widgets
- Offline article caching
- Dark mode
- Spanish and additional language support
- Accessibility-focused reading modes

## Product Principle

Civicly should not decide what users should believe.

It should make the available information **easy to find, easy to understand, easy to compare, and easy to verify**—then leave the decision to the user.

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
