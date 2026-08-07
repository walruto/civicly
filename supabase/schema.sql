-- Civicly Supabase schema (matches app Models.kt)
-- Run in Supabase Dashboard -> SQL Editor

create extension if not exists "pgcrypto";

create table if not exists public.bill (
    id uuid primary key default gen_random_uuid(),
    bill_type text,
    title text not null,
    summary text,
    status text,
    full_text_url text,
    jurisdiction_level text,
    ocd_id text,
    source text not null,
    external_id text not null,
    sponsor_id uuid,
    election_id uuid,
    last_action_date timestamptz
);

create table if not exists public.news_article (
    id uuid primary key default gen_random_uuid(),
    source_domain text not null,
    title text not null,
    url text not null,
    published_at timestamptz,
    topic_tags text[],
    related_election_id uuid,
    related_bill_id uuid,
    summary text
);

create table if not exists public.bias_rating (
    source_domain text primary key,
    provider text,
    lean text not null,
    reliability_score double precision,
    updated_at timestamptz default now()
);

create table if not exists public.candidate (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    party text,
    office text,
    ocd_id text,
    incumbent boolean default false,
    positions jsonb
);

alter table public.bill enable row level security;
alter table public.news_article enable row level security;
alter table public.bias_rating enable row level security;
alter table public.candidate enable row level security;

drop policy if exists "anon read bill" on public.bill;
drop policy if exists "anon read news_article" on public.news_article;
drop policy if exists "anon read bias_rating" on public.bias_rating;
drop policy if exists "anon read candidate" on public.candidate;

create policy "anon read bill" on public.bill for select to anon using (true);
create policy "anon read news_article" on public.news_article for select to anon using (true);
create policy "anon read bias_rating" on public.bias_rating for select to anon using (true);
create policy "anon read candidate" on public.candidate for select to anon using (true);

insert into public.bill (title, summary, status, source, external_id, last_action_date)
select * from (values
    ('Clean Energy Investment Act', 'Allocates funding for renewable infrastructure and grid modernization.', 'In Committee', 'congress.gov', 'hr-1001', now() - interval '3 days'),
    ('Housing Affordability Reform', 'Expands tax credits for affordable housing development.', 'Introduced', 'congress.gov', 's-2044', now() - interval '7 days'),
    ('Digital Privacy Protection Act', 'Establishes federal standards for consumer data rights.', 'Passed Senate', 'congress.gov', 'hr-3302', now() - interval '14 days')
) as seed(title, summary, status, source, external_id, last_action_date)
where not exists (select 1 from public.bill limit 1);

insert into public.news_article (source_domain, title, url, published_at, summary)
select * from (values
    ('reuters.com', 'Congress debates new climate spending package', 'https://www.reuters.com', now() - interval '1 day', 'Lawmakers split on scope of proposed clean energy funding.'),
    ('apnews.com', 'Local housing costs rise ahead of election season', 'https://apnews.com', now() - interval '2 days', 'Voters cite affordability as a top concern in early polling.'),
    ('npr.org', 'What to know about the latest privacy bill', 'https://www.npr.org', now() - interval '4 days', 'A breakdown of provisions in the Digital Privacy Protection Act.')
) as seed(source_domain, title, url, published_at, summary)
where not exists (select 1 from public.news_article limit 1);

insert into public.bias_rating (source_domain, provider, lean, reliability_score)
values
    ('reuters.com', 'manual', 'center', 0.92),
    ('apnews.com', 'manual', 'center', 0.90),
    ('npr.org', 'manual', 'lean-left', 0.88)
on conflict (source_domain) do nothing;

insert into public.candidate (name, party, office, incumbent, positions)
select * from (values
    ('Alex Rivera', 'Independent', 'Mayor', false, '{"housing": "yimby", "transit": "expand"}'::jsonb),
    ('Jordan Lee', 'Democrat', 'City Council', true, '{"housing": "rent-control", "transit": "maintain"}'::jsonb),
    ('Sam Patel', 'Republican', 'City Council', false, '{"housing": "market-rate", "transit": "fiscal-restraint"}'::jsonb)
) as seed(name, party, office, incumbent, positions)
where not exists (select 1 from public.candidate limit 1);

notify pgrst, 'reload schema';
