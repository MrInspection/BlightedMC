---
name: abstraction-tax
description: >-
  Audits an existing core system/module (not a single diff) for
  over-engineering, DX friction, and unnecessary abstraction, and proposes
  zero-regression fixes. Use for "is this over-engineered", "review this
  system's design", "find what's making this hard to use", "audit this for
  over-engineering", or when a user who previously flagged one system's
  abstraction as painful asks to look at a different, unrelated core system
  with the same lens. Also trigger for a "coderabbit-like" architectural
  review, or a report on necessary vs. accidental complexity. NOT for a
  single function, a single PR diff, or bugs unrelated to design/structure —
  use patch-bug or a plain review for that. Read-only by default: produces a
  findings report; only makes code changes or an agent fix-prompt on request.
---

# Abstraction Tax

Every abstraction has a carrying cost — indirection, extra call sites, more surface to learn — paid whether or not it earns its keep. This skill audits an existing system to find where that tax is being paid without a corresponding benefit, applying YAGNI-first, deletion-over-addition reasoning backwards onto code that already exists, rather than forwards onto code being written.

## What this skill is for

A system that solves real duplication (e.g. one loot pipeline instead of three) can still be over-engineered in specific, local ways — an interface with no polymorphic consumer, a sum type crammed into nullable fields with runtime guards, an overload set simulating a builder, duplicated logic that crept back in at a layer above the abstraction. The goal is to find *exactly those*, and nothing else — not to relitigate the whole design, and not to declare something unnecessary without proof.

## The one rule that matters most

**Never declare a method, overload, field, or type "unused" or "unnecessary" without an actual, exhaustive search of the codebase for call sites.** A constructor that looks like redundant sugar may be the only path to a registry lookup that produces real state (lore, NBT, config) nothing else provides. A "marker interface with no purpose" may have exactly one consumer three packages away. Guessing that something is dead because it *looks* dead from the file in front of you is the single most damaging failure mode this skill can produce — it turns an audit into a regression generator. Every "remove this" or "this is unnecessary" claim in the final report must cite what you searched for and what you found (including "zero matches found for X across the codebase" as explicit evidence, not silence).

If you cannot search the full codebase (only the pasted files are available, no repo access), say so explicitly in the report and downgrade every removal-flavored finding to a question: "this looks unused from what's shown — can you confirm no other call site depends on it?" Never phrase an unverified guess as a finding with the same confidence as a verified one.

## Process

### 1. Establish what the system is for

Before judging any individual class, state in one or two sentences what problem the system was built to solve and what it correctly centralized (the thing that would have to be reinvented per-domain without it). This is the baseline against which "over-engineered" is measured — a design isn't over-engineered relative to some abstract ideal, it's over-engineered relative to what its own requirements actually needed.

### 2. Inventory the abstractions

List every interface, abstract class, generic type, strategy/decorator/factory pattern, and multi-overload API surface in the system. For each one, ask:

- **Does something actually consume this polymorphically** (an `instanceof` check elsewhere, a collection typed to the interface, a factory returning the interface type and being passed around as such) — or does exactly one implementation exist and get used concretely everywhere? A single-implementation interface with no polymorphic call site is a candidate for removal, but only after the search from the rule above.
- **Does a sum-type-shaped concept (exactly one of several mutually exclusive kinds) get modeled as one class with nullable fields and runtime-exception accessors**, instead of a sealed hierarchy? This is almost always a real finding when the language supports sealed types/enums with data.
- **Does an overload set exist because of genuinely different construction paths** (e.g., "build from a registry ID" vs. "build from a raw value" — different *semantics*, not just different *types*), or because someone manually enumerated the cartesian product of optional parameters that a single flexible signature (varargs, a small options record, `Optional<T>`, a builder that already exists elsewhere in the codebase) would cover in one path?
- **Has duplicated logic reappeared at a layer the core abstraction was supposed to eliminate** — e.g. three near-identical decorator/handler/adapter classes across domains that differ only in a lookup table, when the abstraction below them already unified everything else? This is usually the highest-value finding: it means the abstraction is sound but incomplete.
- **Are there nullable/optional fields on a shared context or value object that force null-checks at many call sites**, and if so, is that the necessary cost of genuinely supporting multiple calling scenarios (keep, maybe note it), or a symptom of the object trying to serve one caller's needs plus every hypothetical future caller's needs (flag it)?

### 3. Separate findings into two buckets, explicitly

- **Necessary complexity.** State this list even though it's not the interesting part of the report — it prevents the fix step (yours or an agent's) from "helpfully" simplifying something load-bearing. Anything that produces meaningfully different runtime state (a registry lookup, a validated/normalized value, a security or trust-boundary check) belongs here even if it looks like sugar.
- **Actual findings.** Only things that survive the "did I search for usage" gate above. For each finding, use this shape:
    - **Problem** — the concrete symptom (duplication, runtime type-guard, overload sprawl, unconsumed polymorphism).
    - **Evidence** — what you searched for, and what you found (or didn't).
    - **Root cause** — why this happened (usually: DX designed before requirements were fully known, or one abstraction reused past its natural boundary).
    - **Fix** — a concrete, minimal proposal. Prefer collapsing to what already exists elsewhere in the codebase over inventing a new pattern.
    - **Call-site impact** — rough count/list of what would need to change if this fix were applied.

### 4. Order findings by leverage, not by file order

Put the finding that removes the most duplication or the most runtime-crash risk first, not the first class you happened to read. End with an explicit "leave alone" section naming the parts of the system that are correctly scoped — this matters as much as the findings, because it tells the reader (or a fix agent) where *not* to touch.

## Output modes

**Default: findings report**, written directly in the conversation (or as a doc/artifact if the codebase is large enough that the user will want to keep it) — no code changes, no file edits to the audited system. This is the deliverable most requests want.

**On request: zero-regression fix agent prompt.** If the user asks you to turn the findings into something an agent can execute, produce a structured prompt with:
- A role/scope statement restricting the agent to only the listed findings.
- Explicit non-negotiable constraints: search for every call site before changing a signature, one task at a time with verification between tasks, no unrelated reformatting, no behavior change beyond what each task states.
- One task per finding, ordered by dependency (a finding that changes a type other findings build on goes first), each with Problem / Fix / Acceptance criteria / known ambiguous cases to stop-and-report on rather than guess at.
- An explicit "out of scope" section — this must include anything from the "necessary complexity" bucket that might otherwise look temptingly simplifiable to an agent working file-by-file without the full picture.
- A final-report format requirement so the agent reports call sites touched and anything it couldn't verify, rather than silently asserting success.

**On request: apply the fixes directly.** Only if explicitly asked — follow the same task-by-task, verify-before-proceeding discipline as the agent prompt would specify, and stop to ask if a finding turns out to be more load-bearing than the audit assumed (this happens — it's not a failure, it's the search-before-removing rule doing its job mid-fix instead of before the report).

## What "done" looks like

A good audit report is disprovable — someone could take any single finding, go check the evidence cited, and either confirm or reject it without having to re-derive your reasoning from scratch. A report that says "X seems unnecessary" with no search evidence is not an audit, it's a guess with formatting.