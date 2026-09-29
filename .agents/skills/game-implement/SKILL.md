---
name: game-implement
description: Implement approved BlightedMC game content using the existing game systems, APIs, and vanilla Minecraft mechanics.
---

# Game Implement

Turn a settled game-content design into working code.

The implementation should fit the existing architecture and feel native to Minecraft and BlightedMC.

## Process

### 1. Establish the design

Use the supplied design, issue, or clearly defined request as the source of truth.

Do not silently invent major mechanics, rewards, or progression decisions that belong in game design.

When a small implementation detail is unspecified, use the existing codebase's conventions and choose the simplest reasonable behavior.

### 2. Read the existing systems

Before creating new types, inspect the systems relevant to the feature.

Look for existing support for:

* entities and spawning
* abilities
* items
* loot
* registries
* progression
* displays
* configuration
* other related content

Prefer adding a new instance of an existing concept over creating a parallel mechanism.

A new abstraction is appropriate when the existing system genuinely cannot express the design.

### 3. Implement the smallest complete feature

Implement the approved behavior without speculative extensions.

Reuse existing APIs and domain concepts where they fit.

Keep game-specific logic close to the object or system that owns it.

Do not add infrastructure merely because a future feature might need it.

Preserve the project's existing behavior and contracts unless the design explicitly changes them.

### 4. Preserve vanilla conventions

When the design interacts with Minecraft behavior, prefer existing Minecraft mechanics and terminology over custom replacements.

Do not recreate vanilla functionality unnecessarily.

Custom behavior should integrate with the player's existing understanding of Minecraft unless the design intentionally introduces something new.

### 5. Protect existing behavior

When changing a shared system, inspect its consumers and relevant implementations before changing its contract.

Check that existing content still behaves correctly.

Pay particular attention to shared registries, interfaces, entity behavior, loot selection, and progression systems because a new feature can affect existing content indirectly.

### 6. Verify

Run the relevant build and tests.

For game behavior that cannot be meaningfully covered by automated tests, verify the important invariants that can be checked statically and state what remains dependent on in-game testing.

Do not claim a gameplay behavior was verified without actually testing it.

## Out of scope

Do not add unrelated cleanup, refactors, new mechanics, or speculative infrastructure.

Do not redesign the game while implementing it.

When implementation reveals a genuine design problem, separate it from the implementation and report it rather than silently changing the design.

## Result

Provide working code in the existing package and system structure.

The implementation should be understandable from the surrounding code without requiring a new framework or abstraction layer to explain it.
