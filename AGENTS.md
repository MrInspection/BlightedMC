# Agent Guidelines

These rules apply to every AI coding agent working in this repository, regardless of which tool is being used. See the end of this file for how each tool is wired to read it.

## Tone

- Objective, rigorous, concise, factual. No filler, no praise.
- Formal, slightly elevated register (for example, "hereby") in any language.
- Ask a clarifying question only if needed for accuracy; otherwise proceed.
- Never state you are an AI.

## Restricted content

- Give the closest compliant answer, then note the restriction. Do not refuse outright when a partial, compliant answer is possible.

## Coding

Read the touched code first, before proposing or writing anything. Then pick the first option below that fits — do not skip ahead to a later option because it feels more thorough:

1. Unneeded (YAGNI). Do not build it if nothing requires it yet.
2. Already in the codebase. Reuse it instead of writing a new version.
3. Standard library.
4. Native language or framework feature.
5. An already-installed dependency.
6. A one-line solution.
7. Minimum code otherwise.

Necessary complexity (safety, security, correctness) is kept even if longer. Mark it `// ponytail: kept`. Intentional simplifications are marked `// ponytail: <why>`. A change that adds complexity without one of these two markers has not justified itself.

## Scope

- Touch only what was asked. Do not refactor, rename, or otherwise improve adjacent code while making an unrelated change.
- Delete over add. Boring over clever.
- Question complex or ambiguous requests before implementing them.
- Early return over nesting. One responsibility per unit (function, class, module).
- Never skip trust-boundary validation, data-loss handling, security, or accessibility. These are never in scope for silent removal, even when simplifying.
- TypeScript: no `any` unless explicitly asked for.

## Naming

- Descriptive names, 50 characters or fewer.
- No abbreviations, with two exceptions: `id`, `url`, `http`.
- No filler words: `data`, `info`, `manager`, `helper`, `util` used as a bare suffix without a specific reason.
- The following abbreviations are explicitly banned. Write the full word instead:

  | Banned | Use instead |
      |---|---|
  | `br` | `branch` / `break` / `bracket` — spell out the actual meaning |
  | `btn` | `button` |
  | `msg` | `message` |
  | `ctx` | `context` |
  | `cfg` | `config` / `configuration` |
  | `mgr` | `manager` (only when `manager` itself is warranted; see filler-word rule above) |
  | `tmp` | `temporary` |
  | `val` | `value` |
  | `err` | `error` |
  | `idx` | `index` |
  | `arr` | `array` / the actual collection name |
  | `obj` | the actual type name, never the generic `object` |
  | `elem` | `element` |
  | `params` | `parameters` |
  | `args` | `arguments` |
  | `res` | `response` / `result` — spell out which one |
  | `req` | `request` |
  | `calc` | `calculate` / `calculation` |
  | `desc` | `description` / `descending` — spell out which one |
  | `curr` | `current` |
  | `prev` | `previous` |
  | `num` | `number` / the actual quantity name |
  | `str` | the actual type name, never the generic `string` |

  This list is illustrative, not exhaustive. The standing rule: if a reader unfamiliar with the codebase would have to guess what a name stands for, spell it out.

## Modern Java (project targets Java 25)

Check whether Java already does this before writing it manually. An older pattern is not "safer"; it is a missed native-feature step in the Coding ladder above. This section is not exhaustive. If unsure whether a feature is stable in 25, check before using it. Preview features never ship: "native feature" in the ladder means finalized, with no `--enable-preview`.

**Sequenced collections.** Do not index into first or last position manually.

Avoid:
```java
String first = list.get(0);
String last = list.get(list.size() - 1);
```
Prefer:
```java
String first = list.getFirst();
String last = list.getLast();
```
Applies to any `List`, `Deque`, `LinkedHashSet`, or `SortedSet`; all implement `SequencedCollection`.

**Pattern matching.** Destructure, do not cast.

Avoid:
```java
if (obj instanceof Player) {
Player player = (Player) obj;
greet(player);
}
```
Prefer:
```java
if (obj instanceof Player player) {
greet(player);
}
```
Record patterns destructure directly, including nested components:
```java
if (event instanceof DamageEvent(var attacker, var target, var amount)) {
log(attacker, target, amount);
}
```

**Switch expressions**, not fallthrough statements.

Avoid:
```java
switch (type) {
        case LEFT_CLICK:
        return handleLeft();
    case RIGHT_CLICK:
        return handleRight();
default:
        return handleAny();
}
```
Prefer:
```java
return switch (type) {
        case LEFT_CLICK -> handleLeft();
    case RIGHT_CLICK -> handleRight();
default -> handleAny();
};
```

**Lambdas over anonymous classes**, for actual functional interfaces.

Avoid:
```java
items.sort(new Comparator<ItemStack>() {
  public int compare(ItemStack a, ItemStack b) {
    return a.getAmount() - b.getAmount();
  }
});
```
Prefer:
```java
items.sort((a, b) -> a.getAmount() - b.getAmount());
```
Exception: keep the anonymous class when it needs self-reference or more than one overridden method. State this in code, do not silently deviate:
```java
// ponytail: kept — BukkitRunnable must call cancel() on itself;
// a lambda has no `this` to call it on.
new BukkitRunnable() {
  @Override
  public void run() {
    if (done) { cancel(); return; }
    tick();
  }
}.runTaskTimer(plugin, 0L, 1L);
```

**Unnamed variables.** Use `_` for anything genuinely unused.

Avoid:
```java
map.forEach((key, value) -> counter.increment());
```
Prefer:
```java
map.forEach((_, value) -> counter.increment());
```
Avoid:
```java
try {
risky();
} catch (IOException e) {
        return fallback();
}
```
Prefer:
```java
try {
risky();
} catch (IOException _) {
        return fallback();
}
```

**`var`**, only when the right-hand side already states the type.

Prefer, since the type is obvious from the constructor:
```java
var players = new ArrayList<Player>();
```
Avoid, since it hides what is being iterated:
```java
var result = process(data);
```
Prefer keeping the explicit type here instead:
```java
ProcessResult result = process(data);
```

**Text blocks** for multi-line strings. Never manual concatenation.

Avoid:
```java
String json = "{\n" +
        "  \"id\": \"" + id + "\"\n" +
        "}";
```
Prefer:
```java
String json = """
        {
          "id": "%s"
        }
        """.formatted(id);
```

**Records for data carriers.** Never a hand-rolled POJO.

Avoid:
```java
class CooldownEntry {
  private final Class<?> manager;
  private final AbilityType type;
  private final long expiresAt;
  // plus constructor, getters, equals, hashCode, toString
}
```
Prefer:
```java
record CooldownEntry(Class<?> manager, AbilityType type, long expiresAt) {}
```

**Virtual threads.** Do not blanket-replace `runTaskAsynchronously`. Virtual threads are a JVM feature; they know nothing about Bukkit's main-thread requirement. Keep `Bukkit.getScheduler().runTaskAsynchronously` for anything that touches Bukkit API afterward. Reach for a virtual thread only for isolated I/O with zero Bukkit API inside it, and mark the choice either way, since the reason is not obvious from the diff:
```java
// ponytail: kept — plain async task, not a virtual thread: this callback
// re-enters Bukkit API (inventory access), which must stay off any thread
// Bukkit does not manage itself.
Bukkit.getScheduler().runTaskAsynchronously(plugin, this::fetchAndApply);
```

**Stream gatherers.** Niche, not a default. `Stream.gather(...)` (finalized Java 24) exists for custom intermediate operations a `Collector` cannot express: sliding windows, stateful folds. Reach for it only when a plain `.map()` / `.filter()` / `.collect()` chain genuinely cannot express the logic; otherwise it fails the "boring over clever" rule above.

## Minecraft and Spigot target

This project targets **Spigot 26.2**, built through BuildTools. It is not a Paper project. Do not assume Paper's extended API is available.

### Do not use Paper-only API

Vanilla Bukkit and Spigot expose a smaller surface than Paper. Before proposing any Bukkit API call, confirm it exists on the Spigot API specifically, not on Paper's fork of it. A method seen in a Paper plugin tutorial or a Paper Javadoc page is not evidence it exists here.

Known landmines to check for specifically:

- `Player#sendActionBar(Component)` and the `Audience` interface are Paper-only. On Spigot, action bars go through `player.spigot().sendMessage(ChatMessageType.ACTION_BAR, ...)` using `net.md_5.bungee.api.chat` types.
- Paper's own events (for example `AsyncChatEvent`, `ServerTickManager`-related events) do not exist on Spigot; use the vanilla Bukkit equivalents.
- `paper-plugin.yml` and Paper's plugin-loading extensions do not apply; this project uses `plugin.yml`.
- PaperLib and any Paper-exclusive registry or scheduler extensions (including Folia's region-aware scheduler variants) are unavailable.

If a task appears to require Paper-only functionality, state this explicitly and ask whether the target should change, rather than silently writing Paper-specific code against a Spigot build.

### Do not use deprecated API without justification

Check whether a Bukkit or Spigot method is annotated `@Deprecated` before using it. If a current, non-deprecated replacement exists, use it. If a deprecated method is genuinely the only path available, mark the call `// ponytail: kept — <reason, and what would replace it if the API existed>`.

### Do not reintroduce NMS mapping or remapping workflows

Minecraft ships unobfuscated as of version 26.1. Server-internal (NMS) classes carry their real, readable names directly; there is no obfuscated intermediate layer left to map. Do not add or suggest:

- BuildTools `--remapped` / Mojang-mappings development workflows.
- Reobfuscation build steps (for example `specialsource-maven-plugin` or equivalent remap plugins) to convert Mojang-mapped code back to Spigot mappings before packaging.
- Any mapping file, mapping dependency, or remap-at-compile-time configuration.

These solved a problem — obfuscated internals — that no longer exists for this target version. Treat any of the above appearing in a proposed change as a sign the version target has been assumed incorrectly; verify against 26.2 before proceeding.

Do not parse the Minecraft version out of an NMS package name (a pre-existing anti-pattern even before 26.1, since Paper had already stopped relocating the CraftBukkit package by 1.20.5). Use `Bukkit.getBukkitVersion()` or `Bukkit.getServer().getVersion()`, or reuse a version-detection utility already in the codebase if one exists, per the Coding ladder above.

## Version control

Agents may draft a commit message. Agents must never create a commit, stage
files for the purpose of committing, or push, without the user's explicit,
turn-by-turn approval. This is a hard gate, not a courtesy, and it does not
weaken based on how small or routine the change looks.

- **Drafting is not committing.** A request such as "commit this" or "write
  a commit message for these changes" authorizes inspecting the diff and
  producing a message to review. It does not authorize running `git commit`.
- **No proactive commits.** Finishing a task, reaching a good stopping
  point, or the user moving on to a new request is never, by itself,
  grounds to draft or run a commit. Only an explicit ask starts this flow.
- **Executing requires a second, specific approval** of the exact message
  shown — for example "yes, commit it" or "use that." Agreement with the
  underlying change, or silence, is not approval to run the command. If
  there is any ambiguity about whether the user approved the *message shown*
  versus just the idea of committing, ask before running anything.
- This applies uniformly across tools (Claude Code, Cursor, Copilot,
  Windsurf, Cline, or any other agent reading this file) and across commit
  message conventions the repository may adopt.

## Required before submitting code

- [ ] The touched code was read before anything was proposed.
- [ ] Every added line of complexity is marked `// ponytail: kept` or `// ponytail: <why>`.
- [ ] Nothing outside the requested scope was refactored, renamed, or otherwise "improved."
- [ ] No banned abbreviation from the Naming table appears in any new identifier.
- [ ] Every new Java construct checked against the Modern Java section; no manually-written equivalent of a listed native feature was introduced without a stated reason.
- [ ] Every new Bukkit or Spigot API call was verified against the Spigot API, not assumed from Paper documentation or examples.
- [ ] No deprecated API was used without a `// ponytail: kept` justification.
- [ ] No mapping, remapping, or reobfuscation workflow was added or suggested.
- [ ] No commit was created, staged for committing, or pushed without the user's explicit, message-specific approval in this turn.

---

## Cross-agent setup (for repository maintainers, not agents)

This file (`AGENTS.md`) is the single source of truth. Every other tool-specific rules file is a thin pointer to it, never a copy, to avoid drift.

- **Claude Code** does not read `AGENTS.md` directly. Create `CLAUDE.md` at the repository root containing only:
  ```
  @AGENTS.md
  ```
  Claude Code resolves the `@path` import and loads the full content.

- **Cursor**: create `.cursor/rules/main.mdc` as a symlink to `AGENTS.md` (or, on Windows, or if symlinks are not practical, a file containing "Read and follow AGENTS.md at the repository root before responding.").

- **GitHub Copilot**: symlink `.github/copilot-instructions.md` to `AGENTS.md`.

- **Windsurf**: symlink `.windsurfrules` to `AGENTS.md`.

- **Cline**: symlink `.clinerules` to `AGENTS.md`.

Symlink setup, run once from the repository root:
```bash
echo '@AGENTS.md' > CLAUDE.md        # CLAUDE.md must be an import, not a symlink
mkdir -p .cursor/rules && ln -sfn ../../AGENTS.md .cursor/rules/main.mdc
mkdir -p .github && ln -sfn ../AGENTS.md .github/copilot-instructions.md
ln -sfn AGENTS.md .windsurfrules
ln -sfn AGENTS.md .clinerules
```

Commit all of the above to version control so every contributor and every agent gets the same rules regardless of which tool they run.