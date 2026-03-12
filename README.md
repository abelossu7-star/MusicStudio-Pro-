# MusicStudio-Pro-
A music production, music streaming and social media combination platform

GITHUB AI AGENT BUILD COMMAND

You are a senior software architect and mobile engineer.

Your task is to build a complete production ready Android application called:

MusicStudio Pro

You must follow the specification provided below and generate a full project structure.

Your responsibilities include:

1. Generate a complete Android project using Kotlin and Jetpack Compose.

2. Implement MVVM architecture.

3. Create all folders and modules defined in the specification.

4. Integrate Supabase backend.

5. Implement Supabase authentication.

6. Implement Supabase database queries.

7. Implement Supabase storage uploads and downloads.

8. Implement Supabase realtime messaging.

9. Create repository classes for data handling.

10. Implement API service classes.

11. Implement AI service integration.

12. Build UI screens for all pages.

13. Implement navigation between screens.

14. Implement audio playback using ExoPlayer.

15. Implement video recording using CameraX.

16. Implement media uploads.

17. Implement social features:

likes
comments
follows
feed

18. Implement messaging system with realtime updates.

19. Implement creator tipping system.

20. Implement notifications system.

21. Implement AI music generation workflow.

22. Build the studio page where users can:

generate beats
generate lyrics
record vocals
clone voice
generate full songs

23. Implement scalable code structure.

24. Create all required Kotlin models for database tables.

25. Create Supabase database service layer.

26. Implement clean architecture separation:

UI layer
ViewModel layer
Repository layer
Service layer

27. Implement error handling and logging.

28. Implement media compression before upload.

29. Implement feed recommendation logic.

30. Ensure the codebase is modular and scalable.

PROJECT OUTPUT REQUIREMENTS

The generated output must include:

Complete Android project
Gradle configuration
Kotlin source code
Navigation system
UI screens
Supabase integration
Repository classes
Service classes
ViewModels

The project must compile successfully in Android Studio.

CODE STYLE REQUIREMENTS

Use modern Kotlin best practices.

Use coroutines for asynchronous tasks.

Use dependency injection friendly architecture.

Use sealed classes for UI states.

Use data classes for models.

Use Compose UI components.

DO NOT provide explanations.

Only generate the project code and file structure.

Now use the following specification to build the project.

END OF BUILD COMMAND

MUSICSTUDIO PRO — COMPLETE SYSTEM ARCHITECTURE + BUILD SPECIFICATION

========================================================
PROJECT OVERVIEW

MusicStudio Pro is an AI powered music creation and social platform where users can:

• Generate full songs with AI
• Clone their voice for AI singing
• Generate beats and instrumentals
• Generate lyrics using AI
• Record vocals
• Upload songs
• Upload short videos
• Follow creators
• Message other users
• Tip creators
• Distribute music to streaming platforms

The platform combines functionality similar to:

BandLab
SoundCloud
TikTok
Suno AI

The system must support millions of users and scalable cloud architecture.

========================================================
TECH STACK

Frontend

Kotlin
Jetpack Compose
MVVM Architecture
Coroutines
Flow
Retrofit
ExoPlayer
CameraX

Backend

Supabase

Services

Supabase Auth
Supabase Postgres
Supabase Storage
Supabase Realtime
Supabase Edge Functions

AI APIs

ElevenLabs (voice cloning)
OpenAI (lyrics generation)
Music generation API (beat generation)

Payments

Stripe
Paystack

========================================================
ANDROID PROJECT STRUCTURE

MusicStudioPro

app

core
network
database
supabase
auth
utils

data
models
repository

features

auth
login
register

home
feed
trending

studio
ai_song_generator
beat_generator
lyrics_generator
vocal_recorder
voice_clone

upload
upload_song
upload_video

messaging
chat

profile
view_profile
edit_profile

search

notifications

tipping

ui
components
theme

services
ai_service
audio_service
video_service
recommendation_service

viewmodels

========================================================
CONFIGURATION

1) Create a `local.properties` file in the project root (it is ignored by git in this repo).
2) Add your secrets (do NOT commit this file):

```properties
supabase.url=https://<your-project>.supabase.co
supabase.key=<your-supabase-anon-key>
elevenlabs.key=<your-elevenlabs-api-key>
```

3) Rebuild the app. The keys are injected into `BuildConfig` via Gradle and are accessible from Kotlin code.

========================================================
SUPABASE DATABASE SCHEMA

Enable extension

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

---

USERS

create table users (
id uuid primary key default uuid_generate_v4(),
username text unique,
email text unique,
profile_image text,
bio text,
verified boolean default false,
followers_count integer default 0,
following_count integer default 0,
total_tips_received decimal default 0,
created_at timestamp default now()
);

---

SONGS

create table songs (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
title text,
description text,
genre text,
mood text,
audio_url text,
cover_image text,
duration integer,
plays integer default 0,
likes integer default 0,
comments integer default 0,
created_at timestamp default now()
);

---

VIDEOS

create table videos (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
video_url text,
caption text,
likes integer default 0,
views integer default 0,
created_at timestamp default now()
);

---

COMMENTS

create table comments (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
post_id uuid,
comment text,
created_at timestamp default now()
);

---

LIKES

create table likes (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
post_id uuid,
created_at timestamp default now()
);

---

FOLLOWERS

create table followers (
id uuid primary key default uuid_generate_v4(),
follower_id uuid references users(id),
following_id uuid references users(id),
created_at timestamp default now()
);

---

MESSAGES

create table messages (
id uuid primary key default uuid_generate_v4(),
sender_id uuid references users(id),
receiver_id uuid references users(id),
message_text text,
image_url text,
read_status boolean default false,
created_at timestamp default now()
);

---

TIPS

create table tips (
id uuid primary key default uuid_generate_v4(),
sender_id uuid references users(id),
receiver_id uuid references users(id),
amount decimal,
created_at timestamp default now()
);

---

NOTIFICATIONS

create table notifications (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
type text,
reference_id uuid,
is_read boolean default false,
created_at timestamp default now()
);

---

PLAY HISTORY

create table play_history (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
song_id uuid references songs(id),
played_at timestamp default now()
);

---

VOICE SAMPLES

create table voice_samples (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
voice_url text,
created_at timestamp default now()
);

---

AI GENERATED SONGS

create table ai_generated_songs (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
prompt text,
genre text,
lyrics text,
audio_url text,
created_at timestamp default now()
);

---

PLAYLISTS

create table playlists (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
title text,
created_at timestamp default now()
);

---

PLAYLIST SONGS

create table playlist_songs (
id uuid primary key default uuid_generate_v4(),
playlist_id uuid references playlists(id),
song_id uuid references songs(id)
);

========================================================
SUPABASE STORAGE BUCKETS

profile_images

song_audio

song_covers

videos

voice_samples

ai_generated_music

chat_images

========================================================
AI MUSIC GENERATION PIPELINE

STEP 1

User enters prompt

Example

Afrobeats love song about long distance relationship

STEP 2

Lyrics generated using OpenAI API

STEP 3

Beat generated using music generation API

STEP 4

Voice cloned using ElevenLabs

STEP 5

AI singing generated

STEP 6

Song stored in ai_generated_music bucket

STEP 7

Song inserted into songs table

========================================================
STREAMING SYSTEM

Audio streaming handled by ExoPlayer.

Audio files stored in Supabase storage.

Streaming endpoint

/api/song/stream/{song_id}

Adaptive buffering enabled.

========================================================
RECOMMENDATION ALGORITHM

Feed ranking factors

song popularity
recent uploads
user listening history
following creators
song engagement

score formula

score = (plays * 0.4) + (likes * 0.3) + (comments * 0.2) + (recency * 0.1)

========================================================
API ROUTES

POST /api/auth/register

POST /api/auth/login

GET /api/feed

POST /api/song/upload

GET /api/song/{id}

POST /api/song/like

POST /api/comment/create

POST /api/follow

POST /api/message/send

GET /api/messages/{conversation_id}

POST /api/tip/send

POST /api/ai/generate_lyrics

POST /api/ai/generate_beat

POST /api/ai/generate_song

POST /api/video/upload

========================================================
VIDEO CREATION FEATURES

Video recording using CameraX

Editing tools

filters
beauty filter
lighting adjustment
stickers
speed control

========================================================
MESSAGING FEATURES

Realtime messaging powered by Supabase Realtime

Features

text messages
image messages
read receipts
typing indicator
push notifications

========================================================
CREATOR MONETIZATION

Users can earn from

tips
future subscription system
future beat marketplace

minimum tip allowed

$0.10

========================================================
SECURITY

Authentication handled by Supabase Auth

Supported login

email
google

JWT authentication used for APIs

========================================================
PERFORMANCE OPTIMIZATION

lazy loading
media compression
image caching
cdn delivery
database indexing
edge caching

========================================================
SCALABLE CLOUD ARCHITECTURE

Client App
↓

API Layer (Supabase Edge Functions)

↓

Database Layer (Postgres)

↓

Storage Layer (Supabase Storage)

↓

AI Service Layer

OpenAI
ElevenLabs
Music Generation API

========================================================
FUTURE FEATURES

AI mastering
AI beat marketplace
collaborative studio
music NFT minting
creator subscription system

========================================================
END OF COMPLETE SYSTEM SPECIFICATION

MUSICSTUDIO PRO — COMPLETE SUPABASE BACKEND ARCHITECTURE

========================================================
SUPABASE PROJECT STRUCTURE

supabase

config.toml

migrations

001_initial_schema.sql
002_indexes.sql
003_rls_policies.sql
004_triggers.sql
005_functions.sql

functions

auth-register
auth-login
song-upload
song-stream
feed
ai-generate-lyrics
ai-generate-beat
ai-generate-song
video-upload
message-send
tip-send
follow-user
notification-create

storage

profile_images
song_audio
song_covers
videos
voice_samples
ai_generated_music
chat_images

========================================================
DATABASE EXTENSIONS

create extension if not exists "uuid-ossp";
create extension if not exists "pgcrypto";

========================================================
CORE TABLES

USERS

create table users (
id uuid primary key default uuid_generate_v4(),
username text unique not null,
email text unique not null,
profile_image text,
bio text,
verified boolean default false,
followers_count integer default 0,
following_count integer default 0,
total_tips_received numeric default 0,
created_at timestamp default now()
);

---

SONGS

create table songs (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id) on delete cascade,
title text,
description text,
genre text,
mood text,
audio_url text,
cover_image text,
duration integer,
plays integer default 0,
likes integer default 0,
comments integer default 0,
is_ai_generated boolean default false,
created_at timestamp default now()
);

---

VIDEOS

create table videos (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id) on delete cascade,
video_url text,
caption text,
likes integer default 0,
views integer default 0,
created_at timestamp default now()
);

---

COMMENTS

create table comments (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
post_id uuid,
comment text,
created_at timestamp default now()
);

---

LIKES

create table likes (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
post_id uuid,
created_at timestamp default now()
);

---

FOLLOWERS

create table followers (
id uuid primary key default uuid_generate_v4(),
follower_id uuid references users(id),
following_id uuid references users(id),
created_at timestamp default now()
);

---

MESSAGES

create table messages (
id uuid primary key default uuid_generate_v4(),
sender_id uuid references users(id),
receiver_id uuid references users(id),
message_text text,
image_url text,
read_status boolean default false,
created_at timestamp default now()
);

---

TIPS

create table tips (
id uuid primary key default uuid_generate_v4(),
sender_id uuid references users(id),
receiver_id uuid references users(id),
amount numeric,
created_at timestamp default now()
);

---

NOTIFICATIONS

create table notifications (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
type text,
reference_id uuid,
is_read boolean default false,
created_at timestamp default now()
);

========================================================
AI MUSIC TABLES

VOICE SAMPLES

create table voice_samples (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
voice_url text,
created_at timestamp default now()
);

---

AI GENERATED SONGS

create table ai_generated_songs (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
prompt text,
genre text,
lyrics text,
audio_url text,
created_at timestamp default now()
);

========================================================
MUSIC ORGANIZATION

PLAYLISTS

create table playlists (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
title text,
created_at timestamp default now()
);

---

PLAYLIST SONGS

create table playlist_songs (
id uuid primary key default uuid_generate_v4(),
playlist_id uuid references playlists(id),
song_id uuid references songs(id)
);

---

PLAY HISTORY

create table play_history (
id uuid primary key default uuid_generate_v4(),
user_id uuid references users(id),
song_id uuid references songs(id),
played_at timestamp default now()
);

========================================================
INDEXES FOR PERFORMANCE

create index idx_songs_user on songs(user_id);
create index idx_songs_created on songs(created_at);
create index idx_videos_user on videos(user_id);
create index idx_messages_sender on messages(sender_id);
create index idx_messages_receiver on messages(receiver_id);

========================================================
ROW LEVEL SECURITY

alter table users enable row level security;
alter table songs enable row level security;
alter table videos enable row level security;
alter table messages enable row level security;

---

create policy "Public users readable"
on users
for select
using (true);

create policy "User insert own profile"
on users
for insert
with check (auth.uid() = id);

create policy "User update own profile"
on users
for update
using (auth.uid() = id);

---

create policy "Public read songs"
on songs
for select
using (true);

create policy "Users upload own songs"
on songs
for insert
with check (auth.uid() = user_id);

---

create policy "Users send messages"
on messages
for insert
with check (auth.uid() = sender_id);

========================================================
TRIGGERS

FOLLOW COUNT UPDATE

create or replace function update_follow_count()
returns trigger
language plpgsql
as $$
begin
update users
set followers_count = (
select count(*) from followers where following_id = new.following_id
)
where id = new.following_id;
return new;
end;
$$;

create trigger trigger_follow_count
after insert on followers
for each row
execute procedure update_follow_count();

========================================================
RECOMMENDATION ALGORITHM FUNCTION

create or replace function calculate_song_score(
plays integer,
likes integer,
comments integer
)
returns numeric
language plpgsql
as $$
begin
return (plays * 0.4) + (likes * 0.3) + (comments * 0.2);
end;
$$;

========================================================
SUPABASE STORAGE BUCKETS

profile_images

song_audio

song_covers

videos

voice_samples

ai_generated_music

chat_images

========================================================
EDGE FUNCTIONS

auth-register

Handles user signup.

auth-login

Handles authentication requests.

---

song-upload

Uploads music files to storage and creates song record.

---

song-stream

Handles streaming requests.

---

feed

Returns recommended songs.

---

ai-generate-lyrics

Calls OpenAI API.

Returns generated lyrics.

---

ai-generate-beat

Calls music generation API.

Returns instrumental beat.

---

ai-generate-song

Pipeline:

1. Generate lyrics
2. Generate beat
3. Clone voice
4. Generate AI singing
5. Store audio

---

video-upload

Uploads video content.

---

message-send

Stores messages in database and triggers realtime event.

---

tip-send

Processes payments using Stripe or Paystack.

========================================================
REALTIME FEATURES

Supabase Realtime enabled for

messages
notifications
likes
comments

========================================================
SCALING STRATEGY

Use CDN for media delivery.

Separate storage for audio and videos.

Use database indexes.

Use edge caching.

Use Supabase edge functions for heavy operations.

========================================================
END OF SUPABASE BACKEND SPECIFICATION
