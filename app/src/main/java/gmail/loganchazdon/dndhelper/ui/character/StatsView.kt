package gmail.loganchazdon.dndhelper.ui.character

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import gmail.loganchazdon.dndhelper.model.repositories.CharacterRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun StatsView(viewModel: StatsViewModel) {
    val character = viewModel.character.observeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        //Stats
        val stats = character.value?.getStats()

        val proficiencyBoxesExpanded =
            remember { mutableStateListOf<Boolean>(true, false, false, false, false, false) }
        val isVertical = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
        VariableOrientationView(isVertical = isVertical) {
            LazyVerticalGrid(
                contentPadding = PaddingValues(5.dp),
                columns = GridCells.Fixed(3),
                modifier = if(isVertical) {
                     Modifier
                } else {
                    Modifier.fillMaxWidth(0.5f)
                }
            ) {
                items(6) { item ->
                    val stat = stats?.get(CharacterRepository.shortStatNames[item]) ?: 10
                    val mod = (stat - 10) / 2
                    StatBoxView(stat = CharacterRepository.statNames[item], value = stat, mod = mod, onClick = {
                        proficiencyBoxesExpanded.replaceAll { false }
                        proficiencyBoxesExpanded[item] = true
                    })
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .height(85.dp)
                        .fillMaxWidth(0.9f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //Inspiration
                    Card(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(
                                MaterialTheme.colors.surface
                            )
                            .clickable {
                                GlobalScope.launch {
                                    viewModel.toggleInspiration()
                                }
                            },
                        elevation = 10.dp,
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Inspiration", Modifier.padding(5.dp))
                            Checkbox(
                                checked = viewModel.character.observeAsState().value?.inspiration
                                    ?: false,
                                onCheckedChange = null,
                                Modifier.size(30.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(20.dp))

                    //Passives
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        viewModel.character.observeAsState().value?.passives?.forEach {
                            PassiveStatView(passive = it.key, value = it.value)
                        }
                    }

                }

                viewModel.skills?.value?.let {
                    for (item in 0..5) {
                        if (proficiencyBoxesExpanded[item]) {
                            ProficienciesBoxView(
                                baseStat = CharacterRepository.statNames[item],
                                baseStatNum = viewModel.character.observeAsState()
                                    .value?.getStatMod(CharacterRepository.shortStatNames[item]) ?: 0,
                                profBonus = viewModel.character.observeAsState().value?.proficiencyBonus
                                    ?: 2,
                                stats = viewModel.checkForProficienciesOrExpertise
                                    (it[CharacterRepository.statNames[item]]!!) ?: mutableMapOf(),
                                modifier = Modifier.fillMaxSize(0.9f)
                            )
                        }
                    }
                }
            }
        }

    }
}

