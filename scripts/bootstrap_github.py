#!/usr/bin/env python3
"""Create labels, one issue per mission (body = the mission doc), one tracking issue per sprint,
and fill docs/status.md. Needs GitHub CLI (`gh auth login`). Run from the repo root, ONCE.

  python3 scripts/bootstrap_github.py --dry-run
  python3 scripts/bootstrap_github.py --assign A=userA B=userB C=userC
"""
import argparse, csv, re, subprocess

ap = argparse.ArgumentParser()
ap.add_argument("--repo"); ap.add_argument("--assign", nargs="*", default=[]); ap.add_argument("--dry-run", action="store_true")
a = ap.parse_args()
who = dict(x.split("=", 1) for x in a.assign)
repo = ["--repo", a.repo] if a.repo else []

def gh(*args):
    cmd = ["gh", *args, *repo]
    if a.dry_run:
        print("DRY:", " ".join(cmd[:5]), "..."); return "https://github.com/x/y/issues/1"
    return subprocess.run(cmd, check=True, capture_output=True, text=True).stdout.strip()

rows = list(csv.DictReader(open("docs/missions.tsv"), delimiter="\t"))
sprints = sorted({r["sprint"] for r in rows})
labels = {"blocked": "B60205", "risk": "D93F0B", "type:task": "1D76DB", "type:sprint": "5319E7"}
labels.update({f"sprint:{s}": "BFD4F2" for s in sprints})
for n, c in labels.items(): gh("label", "create", n, "--color", c, "--force")

num = {}
def body(r, blocks=()):
    deps = [d for d in r["depends"].split(",") if d]
    doc = open(r["doc"]).read().split("\n", 1)[1].lstrip()   # drop the H1 (it is the issue title)
    head = f"> Source document (canonical): `{r['doc']}`\n\n"
    head += "**Depends on:** " + (", ".join(f"{d} (#{num.get(d,'?')})" for d in deps) or "nothing") + "  \n"
    head += "**Blocks:** " + (", ".join(f"{b} (#{num.get(b,'?')})" for b in blocks) or "-") + "\n\n"
    return head + doc

for r in rows:
    deps = [d for d in r["depends"].split(",") if d]
    ls = ["type:task", f"sprint:{r['sprint']}"] + (["blocked"] if deps else []) + (["risk"] if r["flags"] == "risk" else [])
    args = ["issue", "create", "--title", f"{r['id']} - {r['title']}", "--body", body(r), "--label", ",".join(ls)]
    if r["owner"] in who: args += ["--assignee", who[r["owner"]]]
    url = gh(*args); num[r["id"]] = int(re.search(r"/(\d+)$", url).group(1))

blocks = {r["id"]: [x["id"] for x in rows if r["id"] in x["depends"].split(",")] for r in rows}
if not a.dry_run:                       # second pass fills the real issue numbers + "Blocks"
    for r in rows: gh("issue", "edit", str(num[r["id"]]), "--body", body(r, blocks[r["id"]]))

for s in sprints:
    items = "".join(f"- [ ] #{num[r['id']]} {r['id']} {r['title']}\n" for r in rows if r["sprint"] == s)
    gh("issue", "create", "--title", f"{s} - Sprint tracking", "--label", f"type:sprint,sprint:{s}", "--body", f"Parent issue for sprint {s}.\n\n{items}")

if not a.dry_run:
    t = open("docs/status.md").read()
    for r in rows: t = re.sub(rf"\| {r['id']} \| \|", f"| {r['id']} | #{num[r['id']]} |", t)
    open("docs/status.md", "w").write(t)
print(f"Done: {len(rows)} mission issues, {len(sprints)} sprint issues. Commit docs/status.md.")
