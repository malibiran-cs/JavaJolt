"""Export the supplied one-frame RGBA Aseprite badge art as transparent PNGs.

Requires Python 3.9+ and Pillow. Reuses the team's existing sprite decoder.
Original canvas, artwork, and transparency are preserved.
"""
from pathlib import Path
from export_character_sprites import read_aseprite

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / 'art-source' / 'achievements'
OUT = ROOT / 'src' / 'edu' / 'batstateu' / 'javajolt' / 'ui' / 'swing' / 'assets' / 'achievements'
BADGES = {
    'first_steps': 'firstStep.aseprite',
    'steady_learner': 'steadyLearner.aseprite',
    'beginner_master': 'beginnerMaster.aseprite',
    'intermediate_master': 'intermediateMaster.aseprite',
    'advanced_master': 'advanceMaster.aseprite',
    'topic_specialist': 'topicSpecialist.aseprite',
    'topic_explorer': 'topicExplorer.aseprite',
    'xp_rookie': 'xpRookie.aseprite',
    'xp_veteran': 'xpIntermediate.aseprite',
    'xp_legend': 'xpAdvance.aseprite',
}

if __name__ == '__main__':
    OUT.mkdir(parents=True, exist_ok=True)
    for badge, filename in BADGES.items():
        read_aseprite(SRC / filename).save(OUT / f'{badge}.png', optimize=True)
    print(f'Exported {len(BADGES)} achievement badges.')
