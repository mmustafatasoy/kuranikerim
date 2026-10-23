#!/usr/bin/env python3
"""Offline text-count integrity check. Not a theological/content validation."""
from pathlib import Path
import json

root = Path(__file__).resolve().parents[1] / "app" / "src" / "main" / "assets"
corpus = (root / "quran-uthmani.txt").read_text(encoding="utf-8")
lines = [x for x in corpus.splitlines() if x.strip() and not x.startswith("#")]
surahs = json.loads((root / "surahs.json").read_text(encoding="utf-8"))

assert len(lines) == 6236, f"Wrong ayah count: {len(lines)}"
assert len(surahs) == 114, f"Wrong surah count: {len(surahs)}"
assert sum(s["ayah_count"] for s in surahs) == 6236
assert [s["number"] for s in surahs] == list(range(1, 115))
assert all(len(verse) > 0 for verse in lines)
assert "PLEASE DO NOT REMOVE OR CHANGE THIS COPYRIGHT BLOCK" in corpus
assert "\u0650" in corpus and "\u064e" in corpus, "Expected vowel marks in Uthmani text"
assert lines[0].startswith("بِسْمِ")
print("PASS: 114 surahs, 6236 ayahs, Uthmani vowel marks and source notice present.")
