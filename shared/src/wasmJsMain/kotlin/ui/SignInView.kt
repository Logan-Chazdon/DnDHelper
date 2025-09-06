package ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gmail.loganchazdon.dndhelper.shared.generated.resources.Res
import gmail.loganchazdon.dndhelper.shared.generated.resources.google_play
import gmail.loganchazdon.dndhelper.shared.generated.resources.transparent_icon
import kotlinx.browser.window
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import services.ApiUrl
import ui.accounts.GoogleSignInButton
import ui.character.VariableOrientationView
import ui.platformSpecific.getScreenHeight
import ui.platformSpecific.getScreenWidth
import ui.utils.AutoSizeAdjuster

@Composable
fun SignInView() {
    VariableOrientationView(
        modifier = Modifier
            .fillMaxSize().background(
                Brush.horizontalGradient(
                    listOf(
                        Color(
                            red = 57,
                            blue = 1,
                            green = 15,
                        ),
                        Color(
                            red = 38,
                            blue = 1,
                            green = 19,
                        )
                    ),
                    tileMode = TileMode.Decal,
                )
            ),
        isVertical = getScreenWidth() < getScreenHeight(),
    ) {
        Column(
            modifier = Modifier.padding(end = 175.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Text(
                    "D&D Helper",
                    overflow = TextOverflow.Visible,
                    maxLines = 1,
                    style = MaterialTheme.typography.h1.copy(
                        color = Color.Gray,
                        fontSize = AutoSizeAdjuster(
                            MaterialTheme.typography.h1.fontSize
                        ).value(),
                        shadow = Shadow(
                            color = Color.Black, offset = Offset(5f, 5f), blurRadius = 5f
                        ),
                    ),
                )

                Text(
                    text = "Character creation, management, and reference for fifth edition D&D",
                    style = MaterialTheme.typography.subtitle2.copy(
                        color = Color.White,
                        fontSize = AutoSizeAdjuster(
                            MaterialTheme.typography.subtitle2.fontSize
                        ).value()
                    ),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }



            Image(
                painter = painterResource(
                    Res.drawable.transparent_icon
                ),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(500.dp).offset(x = (-25).dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GoogleSignInButton {
                window.location.href = "http://$ApiUrl:8080/login"
            }

            Button(
                onClick = {
                    window.location.href =
                        "https://play.google.com/store/apps/details?id=gmail.loganchazdon.dndhelper&hl=en_US"
                },
                elevation = ButtonDefaults.elevation(defaultElevation = 2.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Column {
                        Text(
                            text = "GET IT ON",
                            color = MaterialTheme.colors.onPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Light
                        )

                        Text(
                            text = "Google Play",
                            color = MaterialTheme.colors.onPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Image(
                        painter = painterResource(Res.drawable.google_play),
                        contentDescription = "Google play Logo",
                        modifier = Modifier.size(24.dp).offset(y = 4.dp)
                    )
                }
            }

        }
    }
}


@Preview
@Composable
private fun preview() {
    SignInView()
}