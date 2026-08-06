package com.example.fianancetracker.feature.main.add.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fianancetracker.feature.main.R

@Composable
internal fun Handle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFCAC4D0))
        )
    }
}

@Composable
internal fun ConfirmFab(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.size(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = FabColor,
        shadowElevation = 8.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(R.string.cd_done),
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

internal val AmountColor = Color(0xFF1C1B1F)
internal val CursorColor = Color(0xFFB69DF8)
internal val LineColor = Color(0xFFC4C7C5)
internal val ErrorColor = Color(0xFFB3261E)
internal val FabColor = Color(0xFF1C1B1F)
internal val KeyboardBg = Color(0xFFE6E0E9)

internal val KeyHeight = 56.dp
