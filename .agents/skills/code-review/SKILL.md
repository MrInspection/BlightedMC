---
name: code-review
description: Review a diff for correctness, scope, maintainability, API and developer experience, and alignment with the repository's requirements.
---

# Code Review

Review the change as code that will be maintained and consumed, not as a checklist exercise.

## 1. Establish the change

Identify the comparison point and inspect the complete diff.

Read the surrounding code for changed areas. A diff is not sufficient context when behavior depends on callers, shared abstractions, configuration, lifecycle, or framework APIs.

Identify the originating issue, task, or specification when one is available.

Do not require a spec when none exists.

## 2. Review the change

Look for problems in these areas:

### Correctness

Check behavior, edge cases, state transitions, error handling, concurrency, lifecycle, and integration with surrounding code.

### Requirements

Check whether the requested behavior is implemented completely and whether the change introduces behavior outside the requested scope.

A missing requirement and unnecessary scope are both findings.

### Design

Check whether responsibilities, abstractions, and boundaries fit the actual problem.

Do not flag duplication merely because code is duplicated.

Do not recommend extraction unless the code represents a meaningful shared concept or the duplication creates a real maintenance problem.

Likewise, do not praise abstraction merely because it is reusable.

### API and DX

For public or reusable APIs, inspect the consumer-facing call site.

Look for:

* unnecessary parameters or ceremony
* surprising behavior
* unclear names
* inconsistent conventions
* weak type constraints
* awkward common-case usage
* abstractions that expose implementation details unnecessarily

Prefer APIs that make normal usage obvious without preventing advanced usage.

### Platform and project conventions

Check the repository's documented standards and the actual platform/dependency versions.

Do not assume a framework API exists from another fork, version, or ecosystem.

### Tests

Check whether important new or changed behavior is covered appropriately.

Do not demand tests that add little value, but do identify meaningful untested behavior.

## 3. Judge findings

Only report findings supported by the code.

Distinguish:

* **Bug** — demonstrably incorrect behavior.
* **Requirement** — missing or incorrect requested behavior.
* **Design** — maintainability, architecture, or API problem.
* **Nit** — low-impact improvement that is clearly worthwhile.

Do not turn subjective preferences into violations.

For design findings, explain the concrete cost and the smallest reasonable improvement.

## 4. Report

Order findings by severity and practical impact.

Use:

```text
## Findings

### [Bug] <short description>
`path/to/File.java:42`

Problem
<what is wrong>

Evidence
<why the code demonstrates it>

Fix
<smallest reasonable fix>

### [Design] <short description>
...
```

Do not report issues that are purely hypothetical unless they represent a clear design risk.

Finish with:

```text
## Summary

<brief assessment of the change, including important areas reviewed and any remaining uncertainty>
```

Do not provide an overall score or arbitrary quality rating.

## Review-only means review-only

Do not modify the repository unless the user explicitly asks to apply the findings.
