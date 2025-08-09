## Parallel multi-version development with git worktree

This document explains how to maintain multiple Minecraft versions in parallel using branches plus `git worktree`. It keeps common logic in one codebase while letting you build, run, and debug each supported version side by side.

### TL;DR (quick start)

```bash
# From the main repo
cd /Users/andrew/www/take-your-minestream

# Create version branches (example scheme)
# 1.21.x covers 1.21 / 1.21.1 / 1.21.5 / 1.21.6 unless a break occurs
git checkout -b mc/1.21.x 1.21.7-8
git checkout -b mc/1.20.1
git checkout -b mc/1.16.5

# Add parallel worktrees (separate folders mapped to branches)
git worktree add /Users/andrew/www/take-your-minestream-1.21.x mc/1.21.x
git worktree add /Users/andrew/www/take-your-minestream-1.20.1 mc/1.20.1
git worktree add /Users/andrew/www/take-your-minestream-1.16.5 mc/1.16.5

# Enable Git conflict learning (saves time on repeated conflicts)
git config --global rerere.enabled true
```

Open each worktree folder in your IDE to build/run that version independently.

---

### Branch scheme

- `mc/1.21.x` — mainline for 1.21.\* (single JAR if compatible across patches)
- `mc/1.20.1` — dedicated branch for 1.20.1
- `mc/1.16.5` — dedicated branch for 1.16.5

If a 1.21.\* patch introduces breaking changes, split further (e.g., `mc/1.21.0-1.21.1` and `mc/1.21.5+`).

### Why git worktree

- Work on multiple branches at once without constant `git checkout`.
- Separate build/run directories per version; faster iteration.
- Efficient disk usage (shared object store).

### Daily workflow

1. Implement and commit in one branch (e.g., `mc/1.21.x`).
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
for BR in mc/1.20.1 mc/1.16.5; do
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
# Example: 1.21.x worktree
cd /Users/andrew/www/take-your-minestream-1.21.x
./gradlew runClient

# Example: 1.20.1 worktree
cd /Users/andrew/www/take-your-minestream-1.20.1
./gradlew runClient

# Example: 1.16.5 worktree
cd /Users/andrew/www/take-your-minestream-1.16.5
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
- For 1.21.x, try a single artifact targeting `~1.21` if testing confirms compatibility.
- Tag the repo once (e.g., `vX.Y.Z`); attach artifacts per branch/version line.

### IDE tips

- Open each worktree as a separate project window.
- Assign run configurations per project (fast switching between versions).
- Use clear log tags (e.g., `[TYMS-121]`, `[TYMS-120]`) to spot version-specific issues quickly.

### FAQ

- "Can I use the same branch in two worktrees?" — No.
- "Can I convert an existing folder into a worktree?" — Create a new worktree and move files if needed; direct conversion isn’t supported.
- "Do worktrees duplicate the repo on disk?" — No, Git reuses objects; only working files are separate.
