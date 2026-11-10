$ErrorActionPreference = 'Stop'
$tutorialRepo = Split-Path $PSScriptRoot -Parent
$tutorialTemp = Join-Path ([System.IO.Path]::GetTempPath()) ('javajolt-tutorial-retuning-' + [Guid]::NewGuid())
$tutorialSource = Join-Path $tutorialTemp 'src'
$tutorialClasses = Join-Path $tutorialTemp 'classes'
New-Item -ItemType Directory -Path $tutorialTemp, $tutorialClasses | Out-Null
Copy-Item -LiteralPath (Join-Path $tutorialRepo 'src') -Destination $tutorialSource -Recurse

# Mutate only disposable source copies, preserving the real rules and their line endings.
function Set-TutorialTestConstant([string] $relativePath, [string] $original, [string] $replacement) {
    $tutorialFile = Join-Path $tutorialSource ('edu/batstateu/javajolt/' + $relativePath)
    $tutorialText = [System.IO.File]::ReadAllText($tutorialFile)
    if (-not $tutorialText.Contains($original)) {
        throw "Expected tuning declaration missing: $relativePath / $original"
    }
    [System.IO.File]::WriteAllText($tutorialFile, $tutorialText.Replace($original, $replacement),
            [System.Text.UTF8Encoding]::new($false))
}

Set-TutorialTestConstant 'service/LessonRunnerService.java' 'PASS_PERCENT = 70' 'PASS_PERCENT = 75'
Set-TutorialTestConstant 'service/LessonRunnerService.java' 'LESSON_COMPLETE_XP = 50' 'LESSON_COMPLETE_XP = 60'
Set-TutorialTestConstant 'service/ScoreService.java' 'BASE_POINTS = 10' 'BASE_POINTS = 11'
Set-TutorialTestConstant 'service/ScoreService.java' 'POINTS_PER_STREAK_STEP = 2' 'POINTS_PER_STREAK_STEP = 3'
Set-TutorialTestConstant 'service/AnswerTimerService.java' 'INTERMEDIATE_SINGLE_SECONDS = 20' 'INTERMEDIATE_SINGLE_SECONDS = 24'
Set-TutorialTestConstant 'service/AnswerTimerService.java' 'INTERMEDIATE_DAMAGE_DIVISOR = 2' 'INTERMEDIATE_DAMAGE_DIVISOR = 1'
Set-TutorialTestConstant 'data/EnemyRoster.java' 'ESCALATION_INTERMEDIATE = 3' 'ESCALATION_INTERMEDIATE = 4'
Set-TutorialTestConstant 'service/LevelRewardService.java' 'BASE_MAX_HP = 10' 'BASE_MAX_HP = 11'
Set-TutorialTestConstant 'service/PowerUpService.java' 'BEGINNER_HEAL_PERCENT = 20' 'BEGINNER_HEAL_PERCENT = 25'
Set-TutorialTestConstant 'service/PowerUpService.java' 'INTERMEDIATE_HEAL_PERCENT = 35' 'INTERMEDIATE_HEAL_PERCENT = 40'
Set-TutorialTestConstant 'service/PowerUpService.java' 'ADVANCED_HEAL_PERCENT = 50' 'ADVANCED_HEAL_PERCENT = 60'
Set-TutorialTestConstant 'enums/PowerUp.java' 'HINT("Hint", 5)' 'HINT("Hint", 6)'
Set-TutorialTestConstant 'enums/PowerUp.java' 'SHIELD("Shield", 10)' 'SHIELD("Shield", 11)'
Set-TutorialTestConstant 'enums/PowerUp.java' 'HEAL("Heal", 15)' 'HEAL("Heal", 16)'
Set-TutorialTestConstant 'enums/PowerUp.java' 'TIME_STOP("Time Stop", 20)' 'TIME_STOP("Time Stop", 22)'
Set-TutorialTestConstant 'enums/JoltMessage.java' 'SPARK_STREAK = 3' 'SPARK_STREAK = 2'
Set-TutorialTestConstant 'enums/JoltMessage.java' 'JOLT_STREAK = 6' 'JOLT_STREAK = 7'
Set-TutorialTestConstant 'enums/JoltMessage.java' 'SURGE_STREAK = 9' 'SURGE_STREAK = 10'
Set-TutorialTestConstant 'enums/JoltMessage.java' 'SHORT_CIRCUIT_MIN_STREAK = 1' 'SHORT_CIRCUIT_MIN_STREAK = 2'

$tutorialFiles = @(Get-ChildItem -LiteralPath $tutorialSource -Recurse -Filter '*.java' |
        ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' })
$tutorialFiles += '"' + (Join-Path $tutorialRepo 'tests/TutorialLogicCheck.java').Replace('\', '/') + '"'
$tutorialList = Join-Path $tutorialTemp 'sources.txt'
[System.IO.File]::WriteAllLines($tutorialList, $tutorialFiles)
& javac -encoding UTF-8 -d $tutorialClasses ('@' + $tutorialList)
if ($LASTEXITCODE -ne 0) { throw 'Retuned tutorial compilation failed.' }
& java -cp $tutorialClasses TutorialLogicCheck --retuned
if ($LASTEXITCODE -ne 0) { throw 'Retuned tutorial checks failed.' }
Write-Output 'PASS: Retuning changed only temporary source copies; project constants were untouched.'
