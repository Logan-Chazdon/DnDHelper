package com.example.dndhelper.ui.character

import android.preference.PreferenceActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dndhelper.R
import com.example.dndhelper.repository.dataClasses.Spell

@ExperimentalFoundationApi
@Composable
fun CombatView(viewModel: CombatViewModel) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val character = viewModel.character?.observeAsState()
        HeathStatsView(
            character?.value?.currentHp ?: 0,
            character?.value?.maxHp ?: 0,
            character?.value?.tempHp ?: 0
        )

        Spacer(Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Card(
                modifier = Modifier.size(100.dp),
                elevation = 10.dp
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "AC"
                    )
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_armour_class),
                            "",
                            Modifier.size(75.dp)
                        )
                        val ac = character?.value?.equiptArmor?.getAC(character.value?.getStatMod("Dex") ?: 10)
                        Text(
                            text = "$ac",
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                    }
                }
            }

            CombatListView(name = "Conditions", list = viewModel.character?.observeAsState()?.value?.conditions)

            CombatListView(name = "Resistance", list = viewModel.character?.observeAsState()?.value?.resistances)

        }




       /* TODO Implement this when we have some spells and abilities
        val contacts = listOf("Eldritch Blast")
        // TODO: This ideally would be done in the ViewModel
        val grouped = contacts.groupBy { it.firstName[0] }

        @OptIn(ExperimentalFoundationApi::class)
        @Composable
        fun ContactsList(grouped: Map<Char, List<Contact>>) {
            LazyColumn {
                grouped.forEach { (initial, contactsForInitial) ->
                    stickyHeader {
                        CharacterHeader(initial)
                    }

                    items(contactsForInitial) { contact ->
                        ContactListItem(contact)
                    }
                }
            }
        } */



    }
}