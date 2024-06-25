package com.wngud.locationalarm.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    title: String,
    hasBackButton: Boolean,
    onBackNavClicked: () -> Unit = {}
) {
    if(hasBackButton) {
        val navigationIcon: @Composable () -> Unit = {
            IconButton(onClick = { onBackNavClicked() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        }
        TopAppBar(
            title = {
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(start = 4.dp)
                )
            },
            navigationIcon = navigationIcon
        )
    } else {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    modifier = Modifier
                        .padding(start = 4.dp)
                )
            }
        )
    }

}

@Preview(showBackground = true)
@Composable
fun AppBarViewPreview() {
//    AppBar("제목", {})
}