-- Civicly Supabase schema (matches app Models.kt)
-- This reflects the live schema on the "civicly" Supabase project.
-- Run in Supabase Dashboard -> SQL Editor if setting up a fresh project.

create extension if not exists vector;

create table if not exists jurisdiction (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  level text not null check (level in ('State','Regional','County','Local')),
  county text,
  city text,
  unique (name, level, county, city)
);

create table if not exists users (
  id uuid primary key default gen_random_uuid(),
  email text unique,
  created_at timestamptz not null default now(),
  preference_vector vector(5)
);

create table if not exists elections (
  id uuid primary key default gen_random_uuid(),
  election_date date not null,
  jurisdiction_id uuid references jurisdiction(id),
  description text,
  unique (election_date, jurisdiction_id)
);

-- Ballot measures / bills. id is text (e.g. 'Prop 2', 'ALA-ALAMEDA-GO'), not uuid.
create table if not exists bills (
  id text primary key,
  bill_type text not null default 'ballot_measure' check (bill_type in ('ballot_measure','legislative_bill')),
  election_date date not null,
  county text,
  city text,
  jurisdiction_level text not null check (jurisdiction_level in ('State','Regional','County','Local')),
  official_title text,
  plain_summary text,
  yes_vote_means text,
  no_vote_means text,
  fiscal_impact_summary text,
  topic_tags text[],
  source_url text,
  data_status text,
  last_verified date
);
create index if not exists bills_election_date_idx on bills(election_date);
create index if not exists bills_topic_tags_idx on bills using gin(topic_tags);

create table if not exists arguments (
  id uuid primary key default gen_random_uuid(),
  measure_id text not null references bills(id) on delete cascade,
  side text not null check (side in ('for','against')),
  argument_text text,
  submitted_by text,
  source_url text,
  last_verified date
);
create index if not exists arguments_measure_id_idx on arguments(measure_id);

create table if not exists sources (
  id uuid primary key default gen_random_uuid(),
  source_name text not null,
  url text,
  covers text,
  last_checked date,
  notes text
);

create table if not exists key_dates (
  id uuid primary key default gen_random_uuid(),
  event text not null,
  event_date date not null,
  county_jurisdiction text
);

-- positions: jsonb map of quiz issueKey -> answer value, e.g. {"housing":"yimby",...}
-- preference_vector: numeric encoding of the same, reserved for future pgvector similarity scoring.
create table if not exists candidates (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  office text,
  jurisdiction_id uuid references jurisdiction(id),
  party text,
  bio text,
  preference_vector vector(5),
  positions jsonb
);

create table if not exists bias_ratings (
  id uuid primary key default gen_random_uuid(),
  source_name text not null unique,
  bias_rating text,
  factual_rating text,
  rating_source text
);

create table if not exists articles (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  url text,
  source_name text references bias_ratings(source_name),
  published_at timestamptz,
  summary text,
  related_measure_id text references bills(id)
);

create table if not exists match_scores (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references users(id) on delete cascade,
  candidate_id uuid not null references candidates(id) on delete cascade,
  score numeric not null,
  computed_at timestamptz not null default now(),
  unique (user_id, candidate_id)
);

-- Row Level Security: public data is readable by anyone, users/match_scores are owner-only.
alter table jurisdiction enable row level security;
alter table users enable row level security;
alter table elections enable row level security;
alter table bills enable row level security;
alter table arguments enable row level security;
alter table sources enable row level security;
alter table key_dates enable row level security;
alter table candidates enable row level security;
alter table bias_ratings enable row level security;
alter table articles enable row level security;
alter table match_scores enable row level security;

drop policy if exists "Public read access" on jurisdiction;
drop policy if exists "Public read access" on elections;
drop policy if exists "Public read access" on bills;
drop policy if exists "Public read access" on arguments;
drop policy if exists "Public read access" on sources;
drop policy if exists "Public read access" on key_dates;
drop policy if exists "Public read access" on candidates;
drop policy if exists "Public read access" on bias_ratings;
drop policy if exists "Public read access" on articles;

create policy "Public read access" on jurisdiction for select using (true);
create policy "Public read access" on elections for select using (true);
create policy "Public read access" on bills for select using (true);
create policy "Public read access" on arguments for select using (true);
create policy "Public read access" on sources for select using (true);
create policy "Public read access" on key_dates for select using (true);
create policy "Public read access" on candidates for select using (true);
create policy "Public read access" on bias_ratings for select using (true);
create policy "Public read access" on articles for select using (true);

drop policy if exists "Users can view own row" on users;
drop policy if exists "Users can insert own row" on users;
drop policy if exists "Users can update own row" on users;
create policy "Users can view own row" on users for select using (auth.uid() = id);
create policy "Users can insert own row" on users for insert with check (auth.uid() = id);
create policy "Users can update own row" on users for update using (auth.uid() = id) with check (auth.uid() = id);

drop policy if exists "Users can view own match scores" on match_scores;
drop policy if exists "Users can insert own match scores" on match_scores;
drop policy if exists "Users can update own match scores" on match_scores;
drop policy if exists "Users can delete own match scores" on match_scores;
create policy "Users can view own match scores" on match_scores for select using (auth.uid() = user_id);
create policy "Users can insert own match scores" on match_scores for insert with check (auth.uid() = user_id);
create policy "Users can update own match scores" on match_scores for update using (auth.uid() = user_id) with check (auth.uid() = user_id);
create policy "Users can delete own match scores" on match_scores for delete using (auth.uid() = user_id);

notify pgrst, 'reload schema';
