# SimpMusic integration plan

OpenTune is integrating the open-source SimpMusic project as an upstream foundation.

## Upstream

- Repository: https://github.com/maxrave-dev/SimpMusic
- Branch reviewed: `dev`
- License: GPL-3.0

## Important architecture note

SimpMusic is a multi-module Gradle project and its `core` directory is a git submodule. The upstream settings include modules such as `androidApp`, `composeApp`, `common`, `data`, `domain`, `ktorExt`, `kotlinYtmusicScraper`, `lyricsService`, `media3`, and others.

Because of this submodule structure, OpenTune should not copy only a few source folders through the GitHub web editor. The integration should preserve the upstream `core` submodule and then progressively rename/customize the Android application.

## Current OpenTune branches

- `feature/android-foundation` — existing Android foundation
- `backup/before-simpmusic` — safety backup
- `feature/simpmusic-integration` — working branch for this integration

## Next implementation stage

Clone the repositories locally, bring the SimpMusic source and its `core` submodule into the OpenTune working branch, then reconcile Gradle modules and application identity before making UI/branding changes.
