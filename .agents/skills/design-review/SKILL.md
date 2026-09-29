---
name: design-review
description: Audit an existing system for unnecessary complexity, weak boundaries, abstraction mismatch, and developer-experience friction; propose proportionate improvements.
---

# Design Review

Evaluate whether the design fits the problem it actually solves.

This is a read-only review by default.

## Understand the system

Before judging individual abstractions, establish:

* What problem the system solves.
* What it intentionally centralizes.
* Who consumes it.
* Which parts are public or widely reused.
* Which constraints come from the platform or lifecycle.

Judge the design against those requirements, not against an abstract ideal of simplicity.

## Inspect the real usage

Trace interfaces, abstract classes, factories, builders, registries, overloads, generic types, context objects, and public APIs to their consumers.

Search before claiming something is unused, redundant, or removable.

A type that appears unnecessary locally may be important elsewhere.

## Look for friction

Consider:

* abstractions with no meaningful consumer
* duplicated domain logic above an existing abstraction
* APIs that make common operations unnecessarily verbose
* nullable state representing mutually exclusive concepts
* overloads or builders that create unnecessary complexity
* objects carrying responsibilities from unrelated consumers
* boundaries that leak implementation details
* abstractions whose flexibility is not justified by real use
* awkward call sites that could be made simpler without hiding important behavior

Do not treat duplication itself as a defect.

Do not treat abstraction itself as a defect.

The question is whether the design earns its complexity.

## Separate conclusions

Classify observations as:

### Keep

Complexity that is justified by real requirements, platform constraints, or meaningful reuse.

### Consider

A design choice that has a cost but may be appropriate depending on future requirements or project direction.

### Change

A concrete design problem with evidence and a proportionate improvement.

For every removal or consolidation proposal, include the usage evidence supporting it.

## Recommend proportionate changes

Prefer:

1. Removing unnecessary code.
2. Simplifying an existing abstraction.
3. Improving an existing API.
4. Moving a responsibility to a more appropriate owner.
5. Introducing a new abstraction only when the shared concept is real.

Do not replace one abstraction with a larger abstraction merely to make the design theoretically cleaner.

Consider the migration cost and call-site impact of every proposed change.

## API and DX

For public APIs, start with the call site.

Ask:

> What does a developer have to know to use this correctly?

Prefer APIs where the common case is concise, discoverable, and predictable.

Advanced configuration should be available without making the common case pay for it.

Use strong types where they communicate meaningful domain constraints.

Do not introduce types, builders, DSLs, or generic frameworks purely for aesthetic reasons.

## Output

Report findings as:

```text
## Finding: <short description>

Problem
<concrete problem>

Evidence
<usage and code evidence>

Impact
<maintenance, correctness, API, or DX cost>

Recommendation
<smallest proportionate change>
```

Order findings by practical leverage.

Finish with:

```text
## Leave alone

<parts of the system whose complexity is justified or whose current design should not be disturbed>
```

The "Leave alone" section is important. A good design review identifies unnecessary change as well as necessary change.

## Applying findings

Do not modify code unless explicitly asked.

When asked to apply changes, work from confirmed findings only, verify usages before changing public or shared abstractions, and re-run the relevant tests/build after each meaningful structural change.
