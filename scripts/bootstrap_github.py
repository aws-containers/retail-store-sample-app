#!/usr/bin/env python3
"""Create labels, one issue per mission, one tracking issue per sprint, and fill docs/status.md.

Requires: GitHub CLI (`gh auth login` done), run from the repo root.
Usage:
  python3 scripts/bootstrap_github.py --dry-run
  python3 scripts/bootstrap_github.py --assign A=githubUserA B=githubUserB C=githubUserC
Run it ONCE (it does not check for existing issues).
"""
import argparse, csv, re, subprocess, sys

ap = argparse.ArgumentParser()
ap.add_argument("--repo", help="owner/name (default: current repo)")
ap.add_argument("--assign", nargs="*", default=[], help="A=user B=user C=user")
ap.add_argument("--dry-run", action="store_true")
a = ap.parse_args()
who = dict(x.split("=", 1) for x in a.assign)
repo = ["--repo", a.repo] if a.repo else []

def gh(*args):
    cmd = ["gh", *args, *repo]
    if a.dry_run:
        print("DRY:", " ".join(cmd[:6]), "..."); return "https://github.com/x/y/issues/0"
    return subprocess.run(cmd, check=True, capture_output=True, text=True).stdout.strip()

rows = list(csv.DictReader(open("docs/missions.tsv"), delimiter="\t"))
sprints = sorted({r["sprint"] for r in rows})
labels = {"blocked": "B60205", "type:task": "1D76DB", "type:sprint": "5319E7",
          "layer:base": "0E8A16", "layer:upgrade": "FBCA04", "layer:stretch": "C5DEF5"}
labels.update({f"sprint:{s}": "BFD4F2" for s in sprints})
for n, c in labels.items():
    gh("label", "create", n, "--color", c, "--force")

num = {}
def body(r, blocks=()):
    deps = [d for d in r["depends"].split(",") if d]
    t = f"> Source planning document: `{r['sprint_doc']}` (local ID **{r['id']}**)\n\n**Goal**\n{r['title']}\n\n**Done when**\n{r['done_when']}\n\n"
    t += "Depends on: " + (", ".join(f"{d} (#{num[d]})" for d in deps) or "nothing (gate only)") + "\n"
    t += "Blocks: " + (", ".join(f"{b} (#{num[b]})" for b in blocks) or "-") + "\n"
    return t

for r in rows:  # TSV is ordered so dependencies already exist
    deps = [d for d in r["depends"].split(",") if d]
    assert all(d in num or a.dry_run for d in deps), f"order problem at {r['id']}"
    ls = ["type:task", f"sprint:{r['sprint']}", f"layer:{r['layer']}"] + (["blocked"] if deps else [])
    args = ["issue", "create", "--title", f"{r['id']} - {r['title']}", "--body", body(r) if not a.dry_run else "x", "--label", ",".join(ls)]
    if r["owner"] in who: args += ["--assignee", who[r["owner"]]]
    url = gh(*args); num[r["id"]] = int(re.search(r"/(\d+)$", url).group(1))

# second pass: add "Blocks" lines
blocks = {r["id"]: [x["id"] for x in rows if r["id"] in x["depends"].split(",")] for r in rows}
if not a.dry_run:
    for r in rows:
        if blocks[r["id"]]:
            gh("issue", "edit", str(num[r["id"]]), "--body", body(r, blocks[r["id"]]))

# sprint tracking issues
for s in sprints:
    items = "".join(f"- [ ] #{num[r['id']]} {r['id']} {r['title']}\n" for r in rows if r["sprint"] == s)
    gh("issue", "create", "--title", f"{s} - Sprint tracking", "--label", f"type:sprint,sprint:{s}",
       "--body", f"Parent issue for sprint {s}. Source: `{rows[0]['sprint_doc'] if False else [r for r in rows if r['sprint']==s][0]['sprint_doc']}`\n\n{items}")

# update docs/status.md
if not a.dry_run:
    p = "docs/status.md"; t = open(p).read()
    for r in rows:
        t = re.sub(rf"\| {r['id']} \| \|", f"| {r['id']} | #{num[r['id']]} |", t)
    open(p, "w").write(t)
print("Done. Created", len(rows), "mission issues and", len(sprints), "sprint issues. Commit docs/status.md.")
