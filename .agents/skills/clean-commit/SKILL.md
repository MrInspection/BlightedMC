---
name: clean-commit
description: >-
  Writes commit messages in `category(scope): message` conventional-commit
  format, in the voice of a senior dev who wrote it in ten seconds because
  it was obvious — not in AI-generated-commit voice (marketing adjectives,
  restated diffs, emoji, exclamation points). Use whenever the user asks to
  write, generate, or clean up a commit message, or asks to commit staged
  changes. Also use when the user pastes a commit convention/category list
  from a CONTRIBUTING doc and wants messages that follow it. Reads the
  actual diff before writing — never invents a message from a vague
  description alone. NOT for PR descriptions or changelogs — those need
  more context than a commit subject line and are a different deliverable.
---

# Clean Commit

A commit message's job is to tell the next person (often future-you) *why* a
change happened, in one line, using the fewest words that don't lose
information. AI-generated commit messages fail this by explaining the diff
back to the reader in marketing prose. This skill writes the other kind.

## Process

### 1. Read the actual diff

Never write a commit message from a task description or file list alone —
run `git diff --staged` (or the equivalent for what's being committed) and
read it. The subject line describes the *effect* of the change, which is
often not obvious from "what files changed."

If nothing is staged, ask what to inspect rather than inventing content.

### 2. Match the repo's existing convention, don't invent one

- Check `git log --oneline -20` for the categories and scope granularity
  already in use. If the repo's `CONTRIBUTING`/convention doc lists aliases
  (`feat` vs `feature`), use whichever one recent history actually uses; if
  neither is established yet, default to the shorter form (`feat`, not
  `feature`).
- Scope should match a real module/directory/component name already used in
  prior commits or in the repo's own folder structure — never a vague
  placeholder like `core`, `misc`, or `stuff`, and never invented naming
  that doesn't match what the codebase calls that area.
- Pick exactly one category. If the diff genuinely spans two (a fix bundled
  with an unrelated refactor, a feature bundled with its docs), say so and
  suggest splitting into separate commits — do not pick the closest category
  and silently bury the rest of the change under it.

### 3. Write the subject line like it cost nothing to write

- Imperative present tense: `add`, `fix`, `remove`, `rename` — not `added`,
  `adding`, or `adds`.
- Lowercase after the `category(scope): ` prefix, no trailing period.
- Describes what changed and, if it's not obvious from that alone, why —
  never restates the diff mechanically ("update file to add new function").
  A senior dev's commit answers "what would confuse someone reading `git
  blame` on this line in a year" — often that's a constraint or a bug
  symptom, not a description of the edit itself.
- Target ~50–72 characters. If the real reason needs more room, put it in
  the body — don't cram it into the subject or drop it.

### 4. Body — only when the subject line isn't enough

Add a body only if there's a *why* that isn't inferable from the subject and
the diff: a tradeoff made, a constraint that ruled out the obvious approach,
a bug's root cause, a reference to the report/issue that prompted this.

- Prose or short bullets, but never a bullet list that mirrors the diff
  hunks one-for-one ("- changed X to Y", "- added import Z") — that's
  restating information the diff already carries losslessly. If you find
  yourself doing this, delete the body; the diff is a better changelog of
  itself than a paraphrase of it.
- Wrap at ~72 chars if the repo's history does; match existing style rather
  than imposing one.
- Use a `BREAKING CHANGE:` footer only for actual breaking changes, per the
  Conventional Commits / Angular convention — not for every notable change.

### 5. Strip every AI-commit tell before presenting the result

Check the draft against this list and remove anything that matches:

- Marketing adjectives: "comprehensive," "robust," "seamless," "powerful,"
  "enhanced," "improved" (without a number backing it up).
- Throat-clearing: "This commit adds...", "This change introduces...",
  "This PR...". The subject line already says what it does — don't
  re-announce it in the body.
- Emoji, exclamation points, title-case subject lines ("Add New Feature For
  Avatar Component").
- Restating the obvious: if the diff adds one function, the message doesn't
  need a paragraph describing what the function does — that's what the code
  and its own comments are for.
- `Co-Authored-By` / "Generated with [tool]" trailers, unless the user
  explicitly asks for attribution.
- Hedging or uncertainty ("this should fix...", "attempts to..."). State
  what the commit does.

## Example

Diff: adds a `size` prop to an `Avatar` component, used by a new compact
list view elsewhere in the same PR.

**AI-generated (reject this shape):**
```
feat(components): ✨ Enhance Avatar component with new size prop for better flexibility!

This commit introduces a comprehensive update to the Avatar component by
adding a new `size` prop that allows developers to easily customize the
dimensions of the avatar. This change improves the overall flexibility and
usability of the component across the application.
```

**Senior dev (write this shape):**
```
feat(avatar): add size prop

defaults to md; needed for the compact list view in settings
```

Second example — a fix, where the *why* is the bug's actual mechanism, not
a restatement that "a bug was fixed":

**Reject:**
```
fix(auth): fix bug with authentication
```

**Write:**
```
fix(auth): don't retry token refresh on expired-session 401

retry logic treated all 401s as transient; expired sessions
need a re-login, not a retry loop
```

## Output

Present the finished message in a fenced block, ready to paste into
`git commit -m` or `-F`. Don't narrate the category/scope reasoning back to
the user unless asked — show the result, not the process.