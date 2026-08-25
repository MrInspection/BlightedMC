---
name: bug-patch
description: Investigate a reported bug or "this feels off" behavior by tracing it to its actual root cause across the codebase — not just the file mentioned — then scan for the same pattern elsewhere (other classes extending the same base, other call sites doing the same operation), patch every confirmed issue directly, and report each one in a CodeRabbit-style write-up (Problem / Root cause / Fix diff). Use whenever the user reports a bug, describes behavior that changed or seems wrong after some edit, asks to diff two versions of a file to spot what broke, or asks to review/audit a plugin or module for bugs — even if they don't say "patch," "fix," or "investigate" explicitly. Especially relevant for shared engine/base-class systems (entity frameworks, listeners, plugin cores) where one bug in a shared layer surfaces as many unrelated-looking symptoms.
---

# Bug Patch

Real bugs rarely live where the symptom shows up. A boss's sword animation looks "off" — the actual bug is an attachment system two layers away. A boss bar won't disappear — the actual bug is that a command bypasses the death event a shared listener depends on. This skill is for finding that gap and closing it, everywhere it exists, not just in the one spot that got reported.

## Workflow

### 1. Read before diagnosing
Read the file(s) actually touched by the report. If the user gives two versions of the same file (or points at a git history), diff them — the introduced bug is very often visible in a small, innocuous-looking addition (one extra method call, one changed condition) buried in a much larger unrelated diff. Don't assume the bug is in the biggest or most obviously "new" chunk of the diff.

### 2. Trace to the actual mechanism
Don't stop at the first plausible cause. Ask what system the changed code depends on or feeds into — a scheduler, a listener, a shared base class — and go read *that*, even if it wasn't part of what the user shared. The symptom tells you where to look, not why it happens. If the file that would explain the "why" isn't in front of you, ask the user for it (name the specific file/class you need) rather than guessing at its behavior.

### 3. Confirm root cause before proposing anything
State the mechanism precisely: which two things are racing, which invariant got broken, which event never fires. If you can't explain in one or two sentences why the broken code produces the observed symptom, you haven't found the root cause yet — keep tracing.

### 4. Scan for the same pattern elsewhere
Once the root cause is confirmed, actively look for other places it also applies — before patching only the reported spot:
- Other classes extending/implementing the same base class or interface as the buggy one.
- Other call sites performing the same operation (e.g. every other place that removes/despawns an entity, every other place that spawns the same kind of helper object).
- Other usages of the same misused API or pattern within the module.

Use grep/search across the codebase for the specific method calls or patterns involved, not just the file that was reported. Report every confirmed instance, not just the original one — this is the main value this skill adds over a one-off fix.

### 5. Patch directly
Apply the minimal fix for every confirmed issue, directly to the files. Keep to what the bug requires:
- Match existing code style and conventions already present nearby — don't introduce new abstractions, helper classes, or patterns the codebase doesn't already use, unless the fix genuinely requires one.
- Touch only what's broken. Don't refactor, rename, or "improve" adjacent code while you're in there.
- If a fix has a non-obvious reason for its shape (e.g. why `cleanup()` and not `kill()`, why a guard exists), leave a short inline comment explaining it — future readers (and future patches) need that reasoning, not just the diff.

### 6. Report every patched issue, CodeRabbit-style
For each issue, regardless of how many were found, write one block using this structure:

```
## 🐛 Issue: <short, specific description>

**File(s):** <path(s)>

**Problem**
<what's observably wrong, in the user's terms if they reported a symptom>

**Root cause**
<the actual mechanism — precise enough that a reader who's never seen the code
understands exactly why the symptom happens>

**Fix**
```diff
<the actual diff applied>
```
```

Keep the report terse and factual — no padding, no restating the diff in prose. If more than one issue was found (per step 4), present them as separate blocks in order of how directly they relate to what the user asked about (the one they reported first, siblings after).

## When to only report instead of patching

Default is to patch and report (step 5 always runs unless told otherwise). Skip step 5 — investigate and write up only, no file edits — when the user asks for a prompt/handoff for a different agent or a different session to apply, or explicitly asks to review/audit without changing anything yet. In that case, replace the bare diff block with a **Constraints** section listing exactly what the executing agent may and may not touch (scope, out-of-scope-but-related files, what NOT to refactor), plus a short verification checklist — the same shape as a plan another model could execute with zero other context.

## Notes

- If root-cause tracing requires a file you don't have (a base class, a listener, a config), name it and ask for it rather than speculating about what it probably does — a wrong guess about a shared system produces a wrong fix that looks plausible.
- Shared/engine-layer bugs (base classes, listeners, framework glue) are worth the extra step of tracing even when the reported symptom looks purely cosmetic — that's exactly the kind of bug that recurs "from time to time" across unrelated features, because many things depend on the same broken invariant.
