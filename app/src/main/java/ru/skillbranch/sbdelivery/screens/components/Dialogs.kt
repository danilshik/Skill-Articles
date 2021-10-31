package ru.skillbranch.sbdelivery.screens.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.screens.root.ui.AppTheme

const val ABOUT_TEXT =
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas consequat malesuada libero, in pharetra felis tristique non. Duis ac fringilla ligula. Maecenas pretium et turpis id tincidunt. Nulla sodales cursus volutpat. Proin sed massa placerat, pharetra turpis sed, scelerisque nulla. Quisque ultrices lectus vel nibh porta, et euismod lacus semper. Nulla sagittis erat eget sem fringilla ullamcorper. Suspendisse id ligula sem. Ut odio eros, vestibulum ut convallis at, pellentesque imperdiet nisi. Cras lobortis mattis felis quis condimentum. Praesent tincidunt bibendum elit, a finibus ex efficitur ut. Pellentesque egestas sit amet nunc sit amet vehicula. Donec elementum, erat vitae ullamcorper pharetra, nisi massa varius felis, gravida sollicitudin ex ante eget leo. Mauris interdum eros eu leo commodo, ut ultrices libero lobortis. Praesent orci elit, luctus eget tristique ut, aliquam non elit. Vivamus iaculis turpis at odio pretium, vel malesuada nulla dignissim."

@Composable
fun AboutDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
//        backgroundColor = Color.White,
        contentColor = MaterialTheme.colors.primary,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "О приложении SBDelivery")
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onDismiss,
                    content = {
                        Icon(
                            tint = MaterialTheme.colors.secondary,
                            painter = painterResource(R.drawable.ic_baseline_close_24),
                            contentDescription = "Close"
                        )
                    })
            }
        },
        text = { Text(text = ABOUT_TEXT) },
        buttons = {
            Row {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ок", color = MaterialTheme.colors.secondary)
                }
            }
        }

    )
}

@Preview
@Composable
fun PreviewAbout() {
    AppTheme {
        AboutDialog(onDismiss = {})
    }
}