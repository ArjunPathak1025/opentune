# OpenTune Architecture

## Principles

1. Start as a modular monorepo rather than a distributed system.
2. Keep PostgreSQL for metadata and references, not audio binaries.
3. Store audio on a filesystem or S3-compatible object store.
4. Move expensive media processing into background jobs.
5. Keep APIs explicit and versionable.
6. Make self-hosting a first-class requirement.
7. Prefer boring, well-supported technologies over unnecessary complexity.

## System overview

```text
Browser
   |
   v
Web App -----> API -----> PostgreSQL
   |             |
   |             +------> Redis / Job Queue
   |                              |
   v                              v
Audio Stream <------------- Worker + FFmpeg
   |
   v
Object Storage
```

## Planned workspace

- `apps/web`: Next.js web client and music player
- `services/api`: authentication, library, playlists, playback and streaming APIs
- `services/worker`: metadata extraction, artwork processing and media jobs
- `packages/ui`: reusable UI primitives
- `packages/types`: shared API/domain types
- `packages/database`: Drizzle schema and migrations
- `packages/audio`: audio and metadata helpers
- `infra`: Docker Compose and deployment assets

## Storage boundary

Audio files and artwork are stored outside PostgreSQL. Database records contain object keys and metadata so storage can move from local volumes to S3-compatible systems without changing the domain model.

## Initial API domains

- `/api/v1/auth`
- `/api/v1/library`
- `/api/v1/artists`
- `/api/v1/albums`
- `/api/v1/tracks`
- `/api/v1/playlists`
- `/api/v1/favorites`
- `/api/v1/history`
- `/api/v1/stream`

The API should remain focused on user-owned/authorized music and avoid bundling copyrighted catalogs into the project.
