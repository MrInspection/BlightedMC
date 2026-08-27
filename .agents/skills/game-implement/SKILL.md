---
name: game-implement
description: >-
  Implements approved BlightedMC game-design content (bosses, mobs, items,
  abilities, loot) in code, following the codebase's existing conventions
  and design-pattern discipline. Use for "implement this boss", "code this
  mechanic", "build the [feature] from the design doc", or any request to
  turn a finished game-content design into working Java. NOT for
  brainstorming or balancing content — use game-design first and hand its
  output here. Pairs with abstraction-tax as an optional self-check when new
  content required extending a core system nontrivially.
---

# Game Implement

Takes a settled design (from `game-design`, or described directly) and
writes it into the BlightedMC codebase without duplicating an existing
system, without regressing what's already there, and without introducing
the kind of over-engineering `abstraction-tax` exists to catch. BlightedMC
is explicitly its author's practice ground for design patterns and clean
Java — the implementation quality is as much the point as the feature
itself.

## Process

### 1. Get the spec

Work from a finished design doc (concept, mechanics, numbers, progression
fit) — if one doesn't exist yet, either pull it from earlier in the
conversation or ask for the missing pieces rather than inventing balance
numbers mid-implementation. Numbers and mechanics are a design decision, not
an implementation one; don't silently make them up here.

### 2. Inventory existing systems before writing anything new

Read the relevant existing code first. BlightedMC already has, among
others: a generic loot pipeline (`LootTable`/`LootEntry`/`LootResult` with
pluggable selection strategies and feedback decorators), a custom entity
system (`BlightedEntity` and boss/creature ability hooks), an item registry
and builder (`ItemRegistry`/`ItemBuilder`), a hologram/display-attachment
system, and domain-specific systems for fishing and veinmining. Before
writing a new class, check whether the design's requirement is actually a
new instance of one of these (a new `LootResult` implementation, a new
ability hook, a new `ItemBuilder` configuration) rather than a reason for a
new parallel system. If it genuinely needs a new abstraction, that's fine —
but it should be because the existing systems demonstrably can't express it,
not because it wasn't checked.

### 3. Follow the project's coding discipline

- YAGNI first: implement what the design doc asks for, nothing speculative
  ("might need this later" is not a reason to add it now).
- Reuse before adding: stdlib → existing project utility → a new one-liner
  → new code, in that order of preference.
- Early return over nested conditionals; one responsibility per class/method.
- Descriptive names, no abbreviations except `id`/`url`/`http`.
- Never skip trust-boundary validation, null-safety on player/entity state
  that can legitimately be null (disconnects, despawns), or existing
  concurrency assumptions (e.g. RNG must come from the passed context, not
  `Math.random()`, matching the rest of the loot system).
- Where a variant concept exists (a boss's phases, an item's rarity tiers,
  a mutually-exclusive set of ability triggers), model it as a sealed
  hierarchy or enum rather than nullable fields with runtime-checked
  accessors — matching the direction already established in this codebase's
  own review history, not just as a personal style preference.
- Mark intentional simplifications and necessarily-kept complexity inline
  as the project's own convention expects, so a later reviewer (human or
  agent) doesn't "simplify" something that was already a deliberate choice.

### 4. Scope discipline

Implement only what the design doc specifies. If the code naturally suggests
an adjacent improvement or extra mechanic while you're in there, don't add
it — flag it back as a follow-up suggestion (for `game-design` to evaluate,
or `abstraction-tax` if it's a structural observation about existing code)
rather than expanding the diff beyond what was asked.

### 5. Protect what already works

If implementing this content requires touching a shared type (adding a case
to an existing enum/sealed type, changing a shared interface, extending
`LootContext` or similar), search for every existing call site of what
you're changing and confirm none of them break before considering the task
done. New content should never be the reason an unrelated existing feature
regresses.

### 6. Self-check before calling it done

If this task required extending a core system in a nontrivial way (new
interface, new decorator, new strategy, new overload set) rather than just
adding a new leaf implementation of an existing one, consider running
`abstraction-tax` against the part you just touched before presenting the
result — catching a freshly-introduced overload-sprawl or unconsumed
interface here is cheaper than catching it in a later audit pass.

## Output

Actual code, in the project's existing package structure and file
organization, ready to compile against the existing codebase — not a
sketch or pseudocode, unless explicitly asked for a rough pass first.