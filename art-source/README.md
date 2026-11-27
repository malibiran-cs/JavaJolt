# Team-authored Aseprite artwork

`characters/` holds all 24 original `.aseprite` files supplied by the team.
The adjacent preview sheet displays each character with its three emotions.

Each character has a matching runtime PNG in:
`src/edu/batstateu/javajolt/ui/swing/assets/characters/`

The project uses **idle**, **correct**, and **hurt**. For older file naming,
`Happy` means `correct` and `Ouch`/`damaged` means `hurt`.

To regenerate PNGs from the Aseprite sources, use:

```bash
python -m pip install pillow
python tools/export_character_sprites.py
```

The exporter supports the present source pack's single-frame RGBA layer layout.
It also removes the white exterior background of the main mascot sprites.

## Achievement badges

`achievements/` holds the ten original team-authored Aseprite badge files.
Runtime PNGs are in `src/edu/batstateu/javajolt/ui/swing/assets/achievements/`.
Regenerate with `python tools/export_achievement_badges.py` (Pillow required only for export).
