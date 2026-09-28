# Agent Guidelines

`AGENTS.md` is the source of truth for every coding agent working in this repository.

The goal is to produce code that is correct, maintainable, unsurprising, pleasant to use, and appropriate for the project.

These guidelines are principles for making engineering decisions, not a checklist to satisfy mechanically.

---

## Engineering principles

### Read before changing

Read the relevant code, tests, configuration, and existing abstractions before proposing or implementing a change.

Understand the responsibility and lifecycle of the code being changed.

Prefer extending an existing concept when it genuinely fits rather than creating a parallel abstraction.

### Optimize for the simplest correct design

Prefer the smallest solution that properly solves the problem.

In general:

1. Do not build what is not needed.
2. Reuse existing code and abstractions when appropriate.
3. Prefer the standard library and platform capabilities.
4. Reuse installed dependencies when they are already a good fit.
5. Otherwise, write the smallest clear implementation.

Do not optimize for eliminating every duplicated line.

Do not introduce an abstraction merely because two pieces of code look similar. Extract code when it represents a meaningful shared concept, invariant, ownership boundary, or evolution path.

A few duplicated lines are often cheaper than the wrong abstraction.

Do not use a more sophisticated solution simply because it is technically possible.

### Keep scope proportional

Touch what the task requires.

A small local cleanup is appropriate when it directly improves the code being changed and does not alter unrelated behavior. Do not turn a focused task into a broad refactor.

Delete unnecessary complexity before adding new complexity.

Prefer explicit, boring code over clever code when both express the intent equally well.

Early returns, small responsibilities, and simple control flow are useful defaults, not laws.

Correctness, security, validation, thread-safety, accessibility, and data-loss protection must not be weakened merely to reduce code.

### Document non-obvious trade-offs

Comments should explain decisions that are not obvious from the code.

`// ponytail:` is an optional convention for documenting a deliberate trade-off:

```java
// ponytail: kept — this operation must execute on Bukkit's main thread.
```

Do not add `// ponytail` comments merely because a piece of code is slightly complex or because a checklist demands it.

The absence of a `// ponytail` comment is not a problem by itself.

---

# API and Developer Experience

Public APIs are products.

Design them from the consumer's point of view: imagine the call site that someone unfamiliar with the implementation should be able to write after discovering the API.

A good API should be:

* Easy to discover through names, types, autocomplete, and documentation.
* Predictable and consistent with related APIs.
* Explicit about ownership, lifecycle, nullability, mutability, and failure.
* Small and convenient for the common case.
* Capable of supporting advanced use cases without forcing that complexity onto ordinary callers.
* Difficult to misuse without requiring unnecessary ceremony.

### Common case first

Optimize the normal call site.

The simplest use case should not require callers to understand internal implementation details, construct unnecessary configuration objects, or provide information the API can already determine.

Prefer:

```java
punishments.add(player, PunishmentType.MUTE, reason);
```

over exposing low-level parameters that callers should not need to coordinate.

This does not mean convenience APIs should hide important behavior. It means normal usage should require as little incidental knowledge as possible.

### Progressive disclosure

Make simple things simple and advanced things possible.

For example:

```java
spawn(entity, location);
```

can be the normal API while more specialized behavior can be available separately:

```java
spawn(entity, location, options);
```

Do not force every caller through a builder, DSL, configuration object, or generic abstraction merely because the API supports advanced customization.

Builders and fluent APIs are tools, not goals.

### Design from concepts, not syntax

Extract abstractions from domain concepts rather than repeated syntax.

This:

```java
List<SpawnableEntity> eligible = null;

for (SpawnableEntity entity : candidates) {
    if (!entity.canSpawnAt(location, world)) continue;

    if (eligible == null) {
        eligible = new ArrayList<>(candidates.size());
    }

    eligible.add(entity);
}
```

does not automatically deserve a shared helper simply because another spawning system contains the same loop.

If the surrounding systems have different responsibilities, lifecycles, caches, or future evolution, local implementations may be the better design.

By contrast, a shared probability-selection algorithm may deserve extraction when both systems genuinely share that domain behavior.

> Do not abstract by syntax alone.

### Strong types, where they help

Use types to communicate meaningful domain constraints.

Prefer:

```java
enum SpawnMode {
    INDEPENDENT,
    REPLACEMENT,
    HYBRID
}
```

when the concept has a real closed set of values.

Prefer a record when the type is genuinely an immutable data carrier:

```java
record CooldownEntry(Class<?> owner, AbilityType type, long expiresAt) {}
```

Use a class when the type has identity, mutable state, lifecycle, richer encapsulation, inheritance requirements, or framework constraints that make a record inappropriate.

Do not create wrapper types for every primitive merely to make the code appear more strongly typed.

### Consistency is a feature

Related APIs should use consistent terminology, return conventions, and failure behavior.

A caller should be able to predict how an unfamiliar API works from nearby APIs.

Avoid surprising special cases hidden behind convenience methods.

### Documentation is part of the API

Public or reusable APIs should have Javadocs that explain what a consumer actually needs to know:

* What the operation does.
* Important assumptions or invariants.
* Return and absence semantics.
* Failure behavior.
* Thread or lifecycle requirements.
* Intended usage when it is not obvious.

Prefer examples that demonstrate consumer usage over descriptions of internal implementation.

---

# Modern Java

The project targets Java 25.

Use modern Java deliberately.

Before manually implementing something, check whether Java or the standard library already provides a clearer native capability.

Do not use a language or library feature merely because it is newer. Use it when it improves clarity, correctness, safety, or the resulting API.

### Prefer modern language constructs when they improve the code

Examples include:

* Pattern matching for `instanceof`.
* Pattern matching and expressions in `switch`.
* Record patterns when destructuring improves readability.
* Records for genuine data carriers.
* Sealed types when a domain is intentionally closed.
* Text blocks for genuinely multi-line text.
* Unnamed variables when a value is intentionally unused.
* Sequenced collection methods such as `getFirst()` and `getLast()` when the intent is specifically first/last access.
* Immutable collection factories when immutable snapshots are appropriate.

For example:

```java
if (event instanceof CreatureSpawnEvent spawnEvent) {
    handle(spawnEvent);
}
```

rather than manually casting after an `instanceof` check.

Likewise:

```java
return switch (type) {
    case LEFT_CLICK -> handleLeft();
    case RIGHT_CLICK -> handleRight();
    default -> handleAny();
};
```

when the expression form communicates the operation more clearly.

### Prefer intent over feature usage

Modern syntax is not inherently better.

A normal loop can be clearer than a stream.

An explicit type can be clearer than `var`.

A class can be more appropriate than a record.

A local implementation can be better than a generic abstraction.

Choose the construct that communicates intent best.

### `var`

Use `var` when the initializer makes the concrete type obvious and the declared type does not add useful semantic information.

Good:

```java
var players = new ArrayList<Player>();
```

Less useful when the abstraction itself matters:

```java
Map<EntityType, List<SpawnableEntity>> cache = createCache();
```

Do not use `var` simply to minimize characters.

### Collections

When the intent is specifically first or last element access:

```java
values.getFirst();
values.getLast();
```

Use indexed access when the index itself is meaningful to the algorithm.

Choose collection implementations according to their actual semantics and requirements.

Do not manually reimplement standard collection behavior without a reason.

### Streams

Streams are appropriate when they make a transformation or aggregation clearer.

Do not convert straightforward imperative logic into streams merely to appear modern.

A loop is often preferable when the operation has early exits, mutation, multiple conditions, or meaningful control flow.

### Virtual threads

Virtual threads do not change Bukkit's threading requirements.

Use the Bukkit scheduler for Bukkit work.

Virtual threads are appropriate for isolated workloads such as suitable blocking I/O when no Bukkit API is accessed from the virtual thread.

Do not move Bukkit API work to arbitrary background threads simply because virtual threads exist.

### Preview features

Do not introduce preview features unless the project explicitly enables and intends to use them.

When uncertain about the status or suitability of a Java 25 feature, verify it before using it.

---

# Minecraft and Spigot

This project targets **Spigot 26.2**, not Paper.

### Use the actual target API

Do not assume an API exists because it appears in Paper documentation, a Paper plugin, or a generic Minecraft tutorial.

Verify Bukkit/Spigot APIs against the actual Spigot dependency and version used by the project.

Do not silently introduce Paper-only APIs.

Examples of Paper-specific concepts that must not be assumed available include Paper-only events, schedulers, and APIs.

### Threading

Respect Bukkit's execution model.

Bukkit state and APIs that require the main server thread must remain on the appropriate thread.

Keep asynchronous work isolated and return to the Bukkit scheduler before interacting with thread-confined server state.

### Deprecated APIs

Do not introduce deprecated Bukkit or Spigot APIs when a current replacement exists.

When an obsolete API is genuinely necessary because the target platform exposes no suitable alternative, document the reason near the usage.

### NMS and server internals

Prefer the public Spigot API.

Do not introduce mapping, remapping, or reobfuscation workflows for the project's current Minecraft target.

Do not parse Minecraft versions from NMS package names.

When internal server code is genuinely necessary, isolate it behind an appropriate project abstraction rather than spreading version-specific implementation details throughout the codebase.

---

# Naming

Names should communicate meaning without requiring the reader to decode them.

Prefer concise, descriptive names.

Avoid unnecessary abbreviations. The following are acceptable when they genuinely represent the intended concept:

* `id`
* `url`
* `http`

Do not use vague names such as `data`, `info`, `helper`, or `util` when a more specific name exists.

Do not shorten names merely to save characters.

Prefer:

```java
List<SpawnableEntity> candidates;
```

over:

```java
List<SpawnableEntity> arr;
```

Names should reflect the domain concept rather than generic implementation details.

---

# Nullability and absence

Do not use `null` casually, but do not replace every nullable internal value with ceremony.

Use an explicit absence mechanism when it makes an API clearer.

`Optional` is primarily useful for appropriate return values; it is not a requirement for fields, parameters, or every internal local.

A simple nullable local can be the clearest implementation when the value is only created when needed:

```java
List<SpawnableEntity> eligible = null;

for (SpawnableEntity entity : candidates) {
    if (!entity.canSpawnAt(location, world)) continue;

    if (eligible == null) {
        eligible = new ArrayList<>(candidates.size());
    }

    eligible.add(entity);
}
```

Do not introduce a generic helper solely to eliminate this pattern.

---

# Allocation and performance

Prefer correct, understandable code first.

Avoid unnecessary copies and allocations when ownership and lifecycle are already clear.

Use immutable snapshots when immutability provides a useful guarantee.

Do not perform micro-optimizations without a meaningful reason or workload.

A small allocation is not automatically a performance problem.

Likewise, do not sacrifice readability for a theoretical optimization that does not matter to the actual workload.

When performance genuinely matters, reason from the relevant workload and measure rather than guessing.

---

# Validation and errors

Validate external or untrusted input at the boundary where it enters the system.

Keep domain invariants close to the objects or methods responsible for enforcing them.

Prefer designs that make invalid states difficult to represent.

Do not duplicate validation everywhere when a trusted internal abstraction already guarantees the invariant.

Do not swallow exceptions silently.

Error behavior should be predictable and useful to the caller.

---

# Testing

Read existing tests before adding new ones and follow their established conventions.

Prefer tests of observable behavior and important invariants over implementation details.

When designing or changing an API, test the API from the consumer's perspective where practical.

A good API test should resemble the way another part of the plugin is actually expected to use the API.

Run the relevant build and test checks before submitting a change.

Never claim that a build or test passed unless it was actually run.

---

# Compatibility and public APIs

Treat public APIs as contracts.

Before changing or removing a public class, method, constructor, field, enum value, or configuration format, inspect its usages and determine whether compatibility matters.

Do not build speculative compatibility layers for consumers that do not exist.

When compatibility is required, prefer the simplest migration path that preserves the contract without introducing unnecessary architectural complexity.

A clean API for real consumers is more valuable than theoretical compatibility with imaginary ones.

---

# Version control

Agents must not create commits, stage files for committing, or push changes without explicit authorization from the user.

If a commit is requested, show the exact commit message and obtain approval before executing it.

Do not commit merely because a task is complete.

Agents may inspect diffs and draft commit messages without creating commits.

---

# Before submitting code

Verify that:

* The relevant existing code was read before changing it.
* The solution is proportional to the problem.
* Existing abstractions were reused where they genuinely fit.
* New abstractions solve a real problem rather than merely removing textual duplication.
* The public API is understandable from the consumer's point of view.
* The common use case is simple.
* Modern Java capabilities were considered where they materially improve the code.
* No preview feature was introduced unintentionally.
* Bukkit/Spigot APIs were verified against the actual target rather than assumed from Paper.
* No unnecessary deprecated API was introduced.
* Threading remains compatible with Bukkit's execution model.
* Tests and build checks relevant to the change were run.
* Unrelated code was not refactored without a reason.
* No commit, staging, or push occurred without explicit authorization.
