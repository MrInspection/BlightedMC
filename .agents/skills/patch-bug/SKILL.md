---
name: patch-bug
description: Trace a bug to its root cause, check for relevant sibling occurrences, apply the smallest correct fix, and verify it.
---

# Patch Bug

Fix the mechanism, not merely the symptom.

## Investigate

Read the reported code and the surrounding implementation before changing anything.

If a diff, previous version, or recent edit is relevant, inspect it.

Follow the behavior through the code that actually controls it: callers, shared abstractions, listeners, schedulers, lifecycle hooks, registries, and framework APIs.

Do not guess about code you have not read.

When the cause is unclear, form a small number of concrete hypotheses and test them against the codebase. Do not commit to the first plausible explanation without evidence.

State the root cause precisely enough to explain the reported behavior.

## Check for recurrence

Once the mechanism is understood, look for other places where the same mechanism genuinely applies.

Search relevant implementations, call sites, and usages rather than assuming the reported location is unique.

Do not mechanically search or refactor every superficially similar piece of code. The goal is to catch confirmed sibling bugs, not eliminate textual duplication.

## Fix

Apply the smallest change that fixes the confirmed cause.

Follow the existing architecture and API conventions.

Do not introduce an abstraction merely because similar code exists.

Do not refactor unrelated code while fixing the bug.

Add a short comment only when the reason for an otherwise unusual implementation would not be obvious from the code.

## Verify

Run the most relevant tests, build, lint, or reproduction available.

Verify both the original behavior and any sibling cases fixed by the same change when practical.

If the repository cannot verify the affected behavior, state that clearly rather than implying verification.

## Report

Report:

```text
## Issue: <description>

Problem
<observable behavior>

Root cause
<precise mechanism>

Fix
<what changed and why>

Verification
<tests/checks run, or why verification was unavailable>
```

Keep the report focused on evidence and behavior.

## Report-only mode

When asked to investigate or prepare a handoff without modifying code, stop after diagnosis and recurrence analysis.

Provide:

* Root cause
* Confirmed affected locations
* Proposed fix
* Verification plan
* Explicit out-of-scope areas
