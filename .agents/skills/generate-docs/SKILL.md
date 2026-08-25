---
name: generate-docs
description: Generate and improve concise, API-quality Javadocs for Java codebases. Use this skill whenever the user asks to add, improve, rewrite, clean up, or standardize Javadocs, Java API documentation, class/interface/enum documentation, constructor or method documentation, or documentation comments in Java source files. Preserve the code's existing behavior and structure unless a documentation issue exposes an actual API ambiguity that must be called out.
---

# Generate Docs

Produce Javadocs that read like they were written by a senior Java library developer: concise, precise, consistent, and useful at the API boundary.

## Workflow

1. Read the complete touched type before changing documentation. Inspect related types when needed to understand inherited behavior, lifecycle, or API contracts.
2. Infer the contract from the code rather than inventing behavior. Document what callers can rely on, not implementation trivia.
3. Preserve the existing code exactly unless the user explicitly asks for code changes.
4. Match the established documentation style in the project. When no style is established, use the conventions below.
5. Return the revised code unless the user asks for documentation-only snippets.

## Documentation style

Use short class-level summaries that state what the type represents or provides.

Prefer:

```java
/**
 * Manages menu lifecycle, navigation, and active menu state.
 */
```

Over verbose descriptions of obvious implementation details.

Use a second paragraph only when it adds an important contract, lifecycle detail, usage constraint, or behavioral distinction.

Keep parameter descriptions noun- or phrase-based and direct:

```java
@param player player viewing the menu
@param menu menu to open
```

Avoid filler such as "the specified", "the given", or "this method is used to" unless it improves precision.

Use `{@code ...}` for literal values, return values, booleans, and code identifiers when appropriate. Link related API members with `{@link ...}` when the reference is useful to the reader.

Document return behavior precisely, including `{@code null}` when null is a valid result.

Document exceptions only when the method actually declares or meaningfully guarantees them.

## API-level rules

- Document public and protected API members when documentation is requested for the type.
- Constructors should state what the created object represents and document their parameters.
- Methods should describe observable behavior, not restate the method name.
- Interfaces should describe the contract implemented by callers.
- Functional interfaces should explain the operation represented by the function and document each factory/helper method according to its actual filtering or transformation behavior.
- Enums should document the enum itself and each constant when the constants' semantics are not self-evident. If the user explicitly excludes enum values, do not add constant Javadocs.
- Do not document private implementation details unless they define a non-obvious invariant that future maintainers must preserve.
- Do not add documentation merely to increase coverage.

## Keep documentation concise

Prefer one clear sentence over several weaker sentences. Add a paragraph only when the second paragraph communicates a distinct contract.

Avoid:

- marketing language
- generic statements such as "This method provides functionality for..."
- explaining obvious Java syntax
- repeating parameter names in prose without adding meaning
- documenting private fields solely because they exist
- speculative behavior not established by the implementation
- excessive `@see` tags
- redundant `@return` text such as "returns the result"

## Links and references

Use links when they improve navigation or clarify an API relationship:

```java
{@link Menu#setSlotItem(int, ItemStack)}
{@link TickableMenu}
{@code true}
```

Do not add links merely for decoration. Resolve imports and fully qualified names correctly.

## Nullability and annotations

Respect existing nullability annotations such as JSpecify. Do not claim that a value may be null when the API marks it non-null, and do not invent annotations.

## Existing wording

When improving existing Javadocs, retain useful semantic information and remove only redundancy, ambiguity, or unnecessary verbosity. Do not rewrite documentation simply for stylistic variation.

## Output expectations

The result should be directly usable in the source file:

- valid Javadoc syntax
- correct `{@link}` targets
- accurate parameter and return descriptions
- no behavioral changes
- no unrelated formatting or refactoring

For a documentation-only request, do not propose unrelated code improvements. If the code itself contains a correctness issue that materially affects the documentation contract, mention it separately rather than silently changing it.
