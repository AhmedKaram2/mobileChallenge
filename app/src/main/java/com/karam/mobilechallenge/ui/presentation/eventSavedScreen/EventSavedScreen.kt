package com.karam.mobilechallenge.ui.presentation.eventSavedScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.karam.mobilechallenge.ui.theme.AppSpacing


@Composable
fun EventSavedScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(AppSpacing.savedCardSize),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Event Saved!",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "", // TODO: get from view model
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(AppSpacing.small))
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun PreviewEventSavedScreen() {
//    EventSavedScreen(23030.030)
//}