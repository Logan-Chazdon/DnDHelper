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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dndhelper.R
import com.example.dndhelper.repository.dataClasses.Spell

@ExperimentalFoundationApi
@Composable
fun CombatView(i: Int) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        HeathStatsView(10, 10, 20)

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
                        Text(
                            text = "20"
                        )
                    }
                }
            }

            //TODO move these into a separate composable
            Card(
                modifier = Modifier.size(100.dp),
                elevation = 10.dp
            ) {
                Column() {
                    Text("Conditions")
                    LazyColumn(
                        Modifier.padding(4.dp)
                    ) {
                        items(2) {
                            Text("Blinded")
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.size(100.dp),
                elevation = 10.dp
            ) {
                Column() {
                    Text("Resistances")
                    LazyColumn(
                        Modifier.padding(4.dp)
                    ) {
                        items(2) {
                            Text("Fire")
                        }
                    }
                }
            }

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