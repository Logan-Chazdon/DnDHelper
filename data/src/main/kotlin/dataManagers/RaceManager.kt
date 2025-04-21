package dataManagers

import model.Race

class RaceManager(
    val insertRace: suspend (Race) -> Unit,
    val insertRaceSubraceCrossRef: (
        subraceId: Int,
        raceId: Int
    ) -> Unit,
    val insertRaceFeatureCrossRef: (
        raceId: Int,
        featureId: Int
    ) -> Unit,
)