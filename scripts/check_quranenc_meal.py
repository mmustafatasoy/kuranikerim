#!/usr/bin/env python3
"""Live regression guard: Nâs 1–6 must each carry its own Turkish meal."""
import json
from urllib.request import Request, urlopen

endpoint = "https://quranenc.com/api/v1/translation/sura/turkish_shaban/114"
request = Request(endpoint, headers={"Accept": "application/json", "User-Agent": "NurQuran-CI/0.5.1"})
with urlopen(request, timeout=25) as response:
    payload = json.load(response)
if isinstance(payload, dict):
    data = payload.get("result", payload.get("data"))
else:
    data = payload
assert isinstance(data, list), "QuranEnc response missing ayah list"
assert len(data) == 6, "Nâs must contain six ayahs"
text = []
for idx, item in enumerate(data, 1):
    assert int(item["sura"]) == 114, f"Wrong sura at index {idx}"
    assert int(item["aya"]) == idx, f"Wrong ayah number at index {idx}"
    meal = item["translation"].strip()
    assert meal, f"Empty translation at index {idx}"
    text.append(meal)
assert len(set(text)) == 6, "Repeated combined Nâs surah meal is not per-ayah"
assert "ilahına" in text[2].casefold() or "ilâhına" in text[2].casefold(), (
    "Third ayah must mention the deity of mankind"
)
print("OK: QuranEnc Nâs 1–6: six distinct, correctly numbered Turkish ayahs.")
print("114:3 =", text[2])
