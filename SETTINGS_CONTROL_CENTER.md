# Settings Control Center

Built on JavaJolt-main-17-Achievement-Badges. The existing achievement art, unlock rules, quizzes, and saved-player files are retained.

- SettingsScreen: two-column layout, section shortcuts, mascot dialogue, active preference summary, theme preview cards, audio preview, pinned reset/status footer, vertical scrolling at smaller sizes.
- ToggleSwitch, ThemeOptionCard, VolumeSlider: reusable Swing components with native button/slider models and keyboard focus indicators.
- CharacterPortraitView: reduced motion suppresses floating, shakes, and glow effects while retaining the emotion sprites.
- MainWindow: refreshes settings from existing AppSettings preferences when opened. Preference keys and player save format are unchanged.
- Reset requires confirmation and resets application preferences only.

## Validation
Java 21 compilation passed. All ten suites passed: settings integration, progress consistency, content coverage, mechanics, tutorial logic, Jolt sound rules, Swing navigation (57), timer flow (54), enemy artwork, and achievement badge integration. Rendered settings inspected at 1280x800 in both themes and at 900x620 (scrollable). The environment has no compatible audio mixer; actual audible preview and volume should be checked on Windows. Settings-only visual refinement, not a full audit of light mode across all other screens. Switch states update immediately rather than using sliding animations.
