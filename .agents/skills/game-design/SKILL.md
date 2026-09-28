---
name: game-design
description: Design and balance BlightedMC game content—mobs, bosses, items, abilities, loot, and progression—with a vanilla-first Minecraft approach.
---

# Game Design

Design content that feels like an extension of Minecraft rather than a separate game layered on top of it.

The goal is not to reproduce Hypixel SkyBlock. Use proven game-design principles where useful, but adapt them to BlightedMC's actual scope, progression, and vanilla-friendly identity.

## Core principles

### Vanilla first

Start with what Minecraft already teaches the player.

Prefer existing:

* mobs and combat conventions
* items and resources
* crafting and progression patterns
* dimensions and world mechanics
* terminology and player expectations

A custom mechanic should earn its complexity.

Do not recreate a vanilla system when the existing system already provides the intended behavior.

Custom content may extend or bend vanilla mechanics when that creates a meaningful experience, but it should remain recognizable as Minecraft unless the design explicitly calls for something more distinct.

### Design for the actual game

Every proposal should fit BlightedMC's current scope.

Do not import large MMO systems, currencies, menus, progression layers, or infrastructure simply because they exist in another game.

Ask:

* What player problem or fantasy does this solve?
* Where does it fit into existing progression?
* What does the player already know that can support it?
* What new complexity does it introduce?
* Is that complexity worth the resulting experience?

### Reuse existing concepts

Inspect the existing game systems before inventing mechanics.

A new feature should extend an existing concept when appropriate rather than creating a parallel system with a different vocabulary or rules.

This applies to both game design and implementation: a mechanic that requires unnecessary new infrastructure is often a sign that the design itself can be simplified.

## Design lenses

Use these lenses when relevant rather than simulating separate personalities.

### Player readability

Players should be able to understand what is happening and why.

For combat and abilities, distinguish meaningful skill from information the player was never given.

Prefer mechanics based on recognizable cues, positioning, timing, resource management, or preparation over opaque formulas.

### Reward and effort

Rewards should justify the effort required to obtain them.

Consider:

* time investment
* difficulty
* repetition
* rarity
* usefulness
* progression impact

Avoid grind whose primary purpose is delaying an already obvious reward.

### Progression

Place the content deliberately within progression.

Identify:

* intended player stage
* prerequisites
* what the content rewards
* what those rewards enable
* whether it invalidates nearby content

Avoid adding progression layers simply because they are available.

### Balance

Start with an intended experience, then derive numbers from it.

For example:

```text
Target:
A geared mid-game player should survive one mistake but
cannot ignore the mechanic.

Initial tuning:
Boss HP: ~X
Ability cooldown: ~Y
Expected fight duration: ~Z
```

Exact values are useful when the design is ready for tuning, but do not invent false precision during early exploration.

State assumptions when numbers are provisional.

## Process

### 1. Understand the request

Identify the content being designed and its intended player-facing purpose.

For a vague request, narrow the goal before producing a large design.

### 2. Inspect existing content

Read the relevant systems and existing game content.

Check what already exists for:

* related mobs or bosses
* items and rewards
* abilities
* loot
* progression
* relevant vanilla mechanics

Avoid proposing a mechanic that already exists under another name.

### 3. Explore the design

Develop the concept using the design lenses above.

Consider alternatives when there are meaningful trade-offs.

Do not manufacture multiple options when one straightforward solution already fits the request.

### 4. Stress-test when necessary

For substantial designs, examine likely failure modes:

* the reward does not justify the effort
* the mechanic is difficult without being readable
* progression bypasses or invalidates existing content
* the system introduces complexity without enough player value
* repeated play becomes tedious
* the mechanic depends on information the player cannot reasonably infer

Small design decisions do not require a formal stress-test.

### 5. Produce the design

For a design substantial enough to implement, provide:

```text
Concept
<what it is and why it exists>

Gameplay
<what the player actually does>

Balance
<initial numbers or targets, with assumptions where needed>

Progression
<where it fits and what it affects>

Vanilla fit
<which existing Minecraft concepts it builds on>

Existing systems
<relevant BlightedMC systems to reuse>

Open questions
<only unresolved decisions that materially affect implementation>
```

Keep the design proportional to the feature.

## Implementation boundary

This skill designs content. It does not implement it.

Once the design is sufficiently settled, the result should be concrete enough for an implementation task without requiring the implementer to invent core mechanics or balance decisions.
