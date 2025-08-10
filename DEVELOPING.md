## Parallel multi-version development with git worktree

This document explains how to maintain multiple Minecraft versions in parallel using branches plus `git worktree`. It keeps common logic in one codebase while letting you build, run, and debug each supported version side by side.

### TL;DR (quick start)

```bash
# From the main repo
cd /Users/andrew/www/take-your-minestream

# Current version branches
git checkout 1.21.7        # Main branch (latest)
git checkout 1.21.4        # Minecraft 1.21.4 support
git checkout 1.21          # Minecraft 1.21 support
git checkout 1.21.1        # Minecraft 1.21.1 support

# Add parallel worktrees (separate folders mapped to branches)
git worktree add /Users/andrew/www/take-your-minestream-1.21.7 1.21.7
git worktree add /Users/andrew/www/take-your-minestream-1.21.4 1.21.4
git worktree add /Users/andrew/www/take-your-minestream-1.21 1.21
git worktree add /Users/andrew/www/take-your-minestream-1.21.1 1.21.1

# Enable Git conflict learning (saves time on repeated conflicts)
git config --global rerere.enabled true
```

Open each worktree folder in your IDE to build/run that version independently.

---

### Branch scheme

- `1.21.7` — main branch (latest Minecraft version)
- `1.21.4` — dedicated branch for Minecraft 1.21.4
- `1.21` — dedicated branch for Minecraft 1.21
- `1.21.1` — dedicated branch for Minecraft 1.21.1

Each branch maintains its own version-specific dependencies and configurations while sharing common codebase.

### Why git worktree

- Work on multiple branches at once without constant `git checkout`.
- Separate build/run directories per version; faster iteration.
- Efficient disk usage (shared object store).

### Daily workflow

1. Implement and commit in one branch (e.g., `1.21.7`).
2. Backport/forward-port the same change to other branches using `cherry-pick`:

   ```bash
   # In the target worktree/branch
   git cherry-pick -x <commit_sha>
   ```

   The `-x` flag records where the change came from. With `rerere` enabled, repeated conflict resolutions are auto-suggested next time.

3. Push each branch after resolving conflicts.

Tip: Split big changes into smaller commits: version-agnostic logic (easy to cherry-pick) separate from version-specific API/mixins.

### Helper script: backport loop

Create a small script to repeat cherry-picks safely:

```bash
#!/usr/bin/env bash
set -euo pipefail
SHA="$1"
for BR in 1.21.4 1.21 1.21.1; do
  git fetch origin "$BR"
  git checkout "$BR"
  if ! git cherry-pick -x "$SHA"; then
    echo "Resolve conflicts on $BR, then run: git cherry-pick --continue"
    exit 1
  fi
  git push origin "$BR"
done
```

Usage: `./backport.sh <commit_sha>`

### Running clients per version (Fabric Loom)

Run from each worktree folder to avoid cross-contamination:

```bash
# Example: 1.21.7 worktree
cd /Users/andrew/www/take-your-minestream-1.21.7
./gradlew runClient

# Example: 1.21.4 worktree
cd /Users/andrew/www/take-your-minestream-1.21.4
./gradlew runClient

# Example: 1.21 worktree
cd /Users/andrew/www/take-your-minestream-1.21
./gradlew runClient

# Example: 1.21.1 worktree
cd /Users/andrew/www/take-your-minestream-1.21.1
./gradlew runClient
```

Loom typically uses separate run dirs per project folder, so versions won’t clash.

### Handling conflicts efficiently

- Turn on Git’s conflict learning once: `git config --global rerere.enabled true`.
- Prefer small, well-named commits:
  - `feat(core): ...` — version-independent logic
  - `fix(1.20.1): ...` — version-specific fix
  - `backport: ...` — cherry-picked change indication
- Keep version differences thin and localized (mixins/adapters), so most commits apply cleanly.

### Listing and removing worktrees

```bash
# Show all linked worktrees
git worktree list

# Remove a worktree (ensure the folder is closed in your IDE)
git worktree remove /Users/andrew/www/take-your-minestream-1.20.1

# Clean stale records
git worktree prune
```

Note: The same branch cannot be checked out in two worktrees simultaneously.

### CI and releases

- Build each branch in a matrix job; upload separate JARs with correct `gameVersions` on Modrinth.
- Each branch builds its own JAR with version-specific naming (e.g., `tyms-1.21.4-1.1.0.jar`)
- Tag the repo once (e.g., `vX.Y.Z`); attach artifacts per branch/version line

### IDE tips

- Open each worktree as a separate project window.
- Assign run configurations per project (fast switching between versions).
- Use clear log tags (e.g., `[TYMS-121.7]`, `[TYMS-121.4]`, `[TYMS-121]`, `[TYMS-121.1]`) to spot version-specific issues quickly.

### Building multiple versions simultaneously

With the updated `build.gradle`, each branch now creates uniquely named JARs:

```bash
# Build all versions in parallel
git checkout 1.21.7 && ./gradlew build &  # Creates tyms-1.21.7-1.1.0.jar
git checkout 1.21.4 && ./gradlew build &  # Creates tyms-1.21.4-1.1.0.jar
git checkout 1.21   && ./gradlew build &  # Creates tyms-1.21-1.1.0.jar
git checkout 1.21.1 && ./gradlew build &  # Creates tyms-1.21.1-1.1.0.jar
wait
```

All JARs will be available in `build/libs/` without overwriting each other.

### FAQ

- "Can I use the same branch in two worktrees?" — No.
- "Can I convert an existing folder into a worktree?" — Create a new worktree and move files if needed; direct conversion isn’t supported.
- "Do worktrees duplicate the repo on disk?" — No, Git reuses objects; only working files are separate.
- "How do I manage different Minecraft versions?" — Use separate branches for each version, each with its own dependencies and configurations.
