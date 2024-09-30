package ui.homebrew

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import model.Spell
import model.repositories.CharacterRepository
import ui.SpellDetailsView

@Composable
fun CastingModAndSpellsView(
    ability : MutableState<String>,
    spells: SnapshotStateList<Spell>,
    allSpells: Flow<List<Spell>>
) {
    val spellsExpanded = remember {
        mutableStateOf(false)
    }
    var expanded by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Row {
            Text(
                text = ability.value,
                modifier = Modifier
                    .padding(5.dp)
                    .weight(1f),
                style = MaterialTheme.typography.h5
            )

            Button(
                onClick = {
                    spellsExpanded.value= !spellsExpanded.value
                }
            ) {
                Text("ADD SPELLS")
            }
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        }
    ) {
        CharacterRepository.statNames.forEach {
            DropdownMenuItem(
                onClick = {
                    ability.value = it
                    expanded = false
                }
            ) {
                Text(it)
            }
        }
    }

    GenericSelectionPopupView(
        isExpanded = spellsExpanded,
        onItemClick = {
            if(spells.contains(it)) {
                spells.remove(it)
            } else {
                spells.add(it)
            }
        },
        items = allSpells.collectAsState(emptyList()).value,
        detailsView = {
            SpellDetailsView(spell = it)
        },
        getName = {
            it.name
        },
        isSelected = {
            spells.contains(it)
        }
    )
}