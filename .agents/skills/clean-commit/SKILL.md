---
name: clean-commit
description: Draft a concise conventional commit message from the actual diff and the repository's existing commit style. Use only when explicitly asked for a commit message or to commit.
---

# Clean Commit

Write the commit message from the actual change, not from the task description.

## Inspect

Read the staged diff when one exists.

If nothing is staged, inspect the relevant working-tree diff or ask what change the message should describe.

Check recent commit history for the repository's established category, scope, and formatting conventions.

Do not invent a commit convention when the repository already has one.

## Write

Use:

```text
category(scope): message
```

unless the repository clearly uses another convention.

The subject should:

* use imperative present tense
* stay concise
* describe the meaningful effect of the change
* avoid restating obvious implementation details
* avoid marketing language, emoji, and filler

Add a body only when the reason for the change is not clear from the subject and diff.

Useful body content includes:

* the underlying bug mechanism
* an important design trade-off
* a constraint that shaped the implementation
* a meaningful migration or compatibility note

Do not write the body as a paraphrased diff.

## Output

Return the finished message in a fenced block:

```text
fix(spawn): cap replacement probability at one
```

Do not run `git add`, `git commit`, or `git push`.

Execution requires separate explicit authorization after the exact message has been reviewed.
