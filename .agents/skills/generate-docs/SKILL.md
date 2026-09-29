---
name: generate-docs
description: Write or improve concise, accurate Javadocs for Java APIs and source types without changing behavior or structure.
---

# Generate Docs

Write Javadocs that are useful at the API boundary: precise, concise, consistent, and grounded in the actual contract.

Documentation quality matters more than documentation coverage.

## Workflow

1. Read the complete touched type before changing its documentation. Read related types when needed to understand inheritance, lifecycle, nullability, or other API contracts.
2. Infer the contract from the code and existing documentation. Document what consumers can rely on, not implementation trivia or speculation.
3. Preserve implementation, structure, and behavior unless the user explicitly asks for code changes.
4. Match the project's existing documentation style. When no clear style exists, use the conventions below.
5. Return the form requested by the user. Do not reproduce an entire source file when the requested output only requires documentation changes.

## What to document

Document public and protected API when the documentation communicates something useful that is not already obvious.

Do not add Javadocs simply to increase coverage.

### Types

A class, interface, enum, or record summary should explain what the type represents or provides.

Prefer:

```java
/**
 * Manages menu lifecycle, navigation, and active menu state.
 */
```

Add a second paragraph only when it communicates an important contract, lifecycle rule, usage constraint, or behavioral distinction.

### Methods and constructors

Describe observable behavior rather than restating the method name.

Document:

* important side effects
* non-obvious preconditions
* state or lifecycle changes
* meaningful return semantics
* meaningful failure behavior
* thread requirements when relevant

Do not document an obvious implementation detail merely because it is present in the method body.

Constructors do not need a separate summary when the type documentation and parameter descriptions already make their purpose clear.

### Parameters and returns

Keep descriptions direct:

```java
@param player player viewing the menu
@param menu menu to open
@return the currently active menu, or {@code null} when none is active
```

Avoid filler such as:

* "the specified"
* "the given"
* "this method is used to"
* "returns the result"

For return values, explain what the value **means** rather than merely saying that something is returned.

Document `null` behavior when `null` is a valid part of the contract.

### Interfaces and functional interfaces

Document the contract expected by implementations.

For a functional interface, explain the operation represented by its function method and any important input/output semantics.

Document additional methods according to their actual behavior. Do not assume that every functional interface needs factory or helper documentation.

### Enums

Document the enum type.

Document constants when their semantics are not obvious from their names or when project conventions require it.

If the user explicitly asks to leave constants undocumented, preserve that instruction.

### Private implementation details

Do not add Javadocs to private members merely because they exist.

A non-obvious invariant that future maintainers must preserve may belong in a normal source comment rather than API Javadoc.

## Tags and links

Use `{@code ...}` for literals, expressions, or code references when it improves readability.

Use `{@link ...}` when a reference helps the reader navigate the API or understand a relationship:

```java
{@link Menu#setSlotItem(int, ItemStack)}
{@link TickableMenu}
{@code true}
```

Do not add links or tags decoratively.

Use the project's existing conventions for tags such as `@since`, `@deprecated`, or `@see`. Do not introduce new documentation conventions without a reason.

## Nullability

Respect existing nullability annotations and established API semantics.

Do not invent nullability guarantees.

When annotations are absent, infer nullability only when the implementation and surrounding API make the contract sufficiently clear. Otherwise, avoid making a stronger claim than the code supports.

## Existing documentation

When improving existing Javadocs:

* preserve useful semantic information
* remove redundancy
* resolve ambiguity
* tighten wording
* preserve meaningful project terminology

Do not rewrite documentation solely for stylistic variation.

## What to avoid

Avoid:

* marketing language
* generic descriptions such as "provides functionality for..."
* implementation walkthroughs
* repeating the method name in different words
* obvious Java explanations
* speculative behavior
* redundant `@return` descriptions
* excessive `@see` tags
* documentation added solely for coverage
* documenting every public member mechanically

Prefer one strong sentence over several weak ones.

## API quality

Javadocs should complement the API design, not compensate for a confusing API.

When the code exposes an ambiguous or surprising contract that cannot be documented accurately, call that out separately.

Do not silently change the API or implementation to make the Javadoc easier to write.

## Output

The resulting documentation must be:

* valid Javadoc
* accurate to the implementation and contract
* consistent with the surrounding project
* useful to an API consumer
* free of unrelated code changes

For documentation-only requests, do not refactor or improve unrelated code.

When an implementation issue materially affects the documented contract, mention it separately rather than silently changing the code.
