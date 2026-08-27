---
name: patch-bug
description: Investigate a reported bug or "this feels off" behavior by tracing it to its real mechanism, hunting for every other place the same pattern recurs, patching all confirmed instances, and reporting each in a structured block (Problem / Root cause / Fix). Trigger on any bug report, "this changed after an edit," a diff-two-versions request, or a plugin/module bug review — even without the words "patch," "fix," or "investigate." Most valuable on shared engine/base-class systems, where one bug surfaces as many unrelated-looking symptoms.
---

# Patch Bug

A discipline for finding a bug's real cause and closing every place it recurs, not just the reported spot. Skip phases only when explicitly justified.

When exploring the codebase, read `CONTEXT.md` (if it exists) to get a clear mental model of the relevant modules, and check ADRs in the area you are tracing.

## Redact

If diagnosis requires showing command output, logs, or captured traces: redact every secret first. Write `<REDACTED>` in its place. Quote only the lines that carry the signal. If the redacted output is not enough to diagnose the bug, say so and ask the user rather than guessing at what was removed.

## Phase 1: Trace to the mechanism

**This is the skill.** Everything else is mechanical. A patch against the wrong mechanism is a patch against a coincidence; it will look plausible and still be wrong.

Read the file the user points at. If two versions are given, or a git history is available, diff them.

The introduced bug is rarely in the largest or most obviously "new" chunk of a diff. Assume a small, innocuous-looking addition, buried in an unrelated diff, before assuming the biggest hunk is the cause.

Ask what the changed code depends on or feeds into: a scheduler, a listener, a shared base class, a framework lifecycle. Go read that, even if it was not part of what the user shared. The symptom tells you where to look, not why it happens.

### When the mechanism is not obvious after one read-through

Generate two to three ranked candidate mechanisms before committing to one. Single-hypothesis fixation on the first plausible cause is how a wrong fix ships looking correct.

Each candidate must be falsifiable: state what would be true elsewhere in the codebase if it were the real cause.

> Format: "If <mechanism> is the cause, then <other call site / other file> must show <specific, checkable evidence>."

If a candidate cannot be stated this precisely, it is a guess, not a hypothesis: discard or sharpen it. Where the codebase owner is available, show the ranked list before committing — they often know which subsystem was touched recently, which rules out candidates instantly.

### Missing information

If the file that would confirm or rule out a mechanism is not available, name it and ask for it. Do not speculate about what a shared system probably does. A wrong guess about a shared system produces a wrong fix that still looks plausible.

### Completion criterion

Phase 1 is done when the mechanism can be stated in one or two sentences, precisely enough that a reader unfamiliar with the code understands exactly why the symptom occurs:

- [ ] The statement names which two things are interacting incorrectly (a race, a bypassed invariant, an event that never fires), not just where the symptom appears.
- [ ] The statement explains the full reported symptom, not a partial or adjacent one.
- [ ] Nothing in the statement depends on a file you have not actually read.

If you cannot write this statement, you do not have the root cause yet. Do not proceed to Phase 2.

## Phase 2: Scan for siblings

Once the mechanism is confirmed, search for every other place it also applies before patching only the reported spot. This is the phase that separates a real fix from a one-off patch.

Check, by name:

- [ ] Every other class extending or implementing the same base class or interface as the buggy one.
- [ ] Every other call site performing the same operation (every place that removes an entity the same way, every place that spawns the same kind of helper, every place that reads the same field).
- [ ] Every other usage of the same misused API or pattern anywhere in the module.

Grep for the specific method calls or patterns involved, not just the file that was reported. Enumerate every confirmed instance found; do not stop at the first additional occurrence.

Do not proceed to Phase 3 until this scan is complete and its result (even if the result is "no other instances found") is stated explicitly.

## Phase 3: Patch

Apply the minimal fix for every confirmed instance from Phase 2.

- [ ] Match the code style and conventions already present nearby. Do not introduce a new abstraction, helper, or pattern the codebase does not already use, unless the fix genuinely requires one.
- [ ] Touch only what is broken. Do not refactor, rename, or otherwise improve adjacent code while in the file.
- [ ] Where a fix has a non-obvious reason for its shape (why this guard, why this method and not a similar one), leave a short comment stating the reason. The diff shows what changed; the comment is for why it changed this way.

## Phase 4: Verify

If a build, test, lint, or run command exists that exercises the fixed code path, run it against the patch and confirm it passes. If the original symptom was reproducible through some command (a failing test, a script, a manual repro the user described), re-run it and confirm it no longer reproduces.

If nothing in the repository can verify this fix — no tests reach this path, no build catches this class of error, there is no way to run the affected code in isolation — say so explicitly. This is itself a finding worth reporting, not a gap to pass over silently.

## Phase 5: Report

Report every patched issue in one block per issue, using this structure:

```
## Issue: <short, specific description>

File(s): <path(s)>

Problem
<what is observably wrong, in the user's terms if they reported a symptom>

Root cause
<the mechanism from Phase 1, stated precisely>

Fix
<diff>
<the actual diff applied>
</diff>
```

Keep each block terse and factual. Do not restate the diff in prose. If more than one issue was found in Phase 2, present them as separate blocks, the originally reported issue first, siblings after.

### Report-only mode

If the user asked for a prompt or handoff for a different agent or session, or explicitly asked to review without changing anything yet: skip Phase 3 and Phase 4. Replace the Fix section with:

- A Constraints section naming exactly what the executing agent may and may not touch: files in scope, files that look related but are out of scope, and anything that must not be refactored while applying the fix.
- A verification checklist the executing agent can run after patching, in place of Phase 4.

The result should be a plan a model with no other context could execute correctly on its own.

## Required before declaring done

- [ ] The root-cause statement from Phase 1 explains the entire reported symptom, not a partial one.
- [ ] The sibling scan from Phase 2 was performed and its result is stated, even if no siblings were found.
- [ ] Every patch matches existing house style and touches only the broken lines.
- [ ] Phase 4 verification was run, or its absence was explicitly stated as a finding.
- [ ] The report follows the required block structure, one block per issue.