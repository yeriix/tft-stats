# Downloads the TFT static data and icons used by the app (set 18).
# Source: TFT Data Dragon files distributed by Riot Games (community mirror).
$ErrorActionPreference = "Stop"
$ProgressPreference = "SilentlyContinue"

$base = "https://raw.githubusercontent.com/noxelisdev/TFT_DDragon/master"
$root = Split-Path -Parent $PSScriptRoot

$dataDir = Join-Path $root "backend\src\main\resources\static-data"
$spritesDir = Join-Path $root "frontend\public\tft\sprites"
$traitsDir = Join-Path $root "frontend\public\tft\traits"
New-Item -ItemType Directory -Force $dataDir, $spritesDir, $traitsDir | Out-Null

Write-Host "Downloading data files..."
foreach ($name in "champion", "item", "trait") {
    Invoke-WebRequest "$base/data/en_US/$name.json" -OutFile (Join-Path $dataDir "$name.json")
}

Write-Host "Downloading sprite sheets..."
$sheets = @()
foreach ($n in 9..11) { $sheets += "tft-champion$n.png" }
foreach ($n in 34..39) { $sheets += "tft-item$n.png" }
foreach ($sheet in $sheets) {
    Invoke-WebRequest "$base/img/sprite/$sheet" -OutFile (Join-Path $spritesDir $sheet)
}

Write-Host "Downloading trait icons..."
$traitIcons = @(
    "Trait_Icon_18_Adaptor.TFT_Set18.png", "Trait_Icon_18_ApexPredator.TFT_Set18.png",
    "Trait_Icon_18_Attuned.TFT_Set18.png", "Trait_Icon_18_Avatar.TFT_Set18.png",
    "Trait_Icon_18_Blossom.TFT_Set18.png", "Trait_Icon_18_BountySeeker.TFT_Set18.png",
    "Trait_Icon_18_Brawler.TFT_Set18.png", "Trait_Icon_18_Caustic.TFT_Set18.png",
    "Trait_Icon_18_Coven.TFT_Set18.png", "Trait_Icon_18_Defender.TFT_Set18.png",
    "Trait_Icon_18_Eclipse.TFT_Set18.png", "Trait_Icon_18_Elderwood.TFT_Set18.png",
    "Trait_Icon_18_EmeraldAspect.TFT_Set18.png", "Trait_Icon_18_Executioner.TFT_Set18.png",
    "Trait_Icon_18_Fae.TFT_Set18.png", "Trait_Icon_18_FloraFatalis.TFT_Set18.png",
    "Trait_Icon_18_Greenfather.TFT_Set18.png", "Trait_Icon_18_Hunter.TFT_Set18.png",
    "Trait_Icon_18_Inferno.TFT_Set18.png", "Trait_Icon_18_Invoker.TFT_Set18.png",
    "Trait_Icon_18_Juggernaut.TFT_Set18.png", "Trait_Icon_18_Lunar.TFT_Set18.png",
    "Trait_Icon_18_Monolith.TFT_Set18.png", "Trait_Icon_18_OldGod.TFT_Set18.png",
    "Trait_Icon_18_OldGrowth.TFT_Set18.png", "Trait_Icon_18_Primal.TFT_Set18.png",
    "Trait_Icon_18_Rapidfire.TFT_Set18.png", "Trait_Icon_18_Ravager.TFT_Set18.png",
    "Trait_Icon_18_Riftbeast.TFT_Set18.png", "Trait_Icon_18_Rival.TFT_Set18.png",
    "Trait_Icon_18_Solar.TFT_Set18.png", "Trait_Icon_18_Spellweaver.TFT_Set18.png",
    "Trait_Icon_18_Sprykin.TFT_Set18.png", "Trait_Icon_18_Summoner.TFT_Set18.png",
    "Trait_Icon_18_Vanguard.TFT_Set18.png", "Trait_Icon_18_ZyraOrigin.TFT_Set18.png"
)
foreach ($icon in $traitIcons) {
    Invoke-WebRequest "$base/img/trait/$icon" -OutFile (Join-Path $traitsDir $icon)
}

Write-Host "Done."