package com.example.fianancetracker.feature.settings.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.feature.settings.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val SheetBg = Color(0xFFFFFFFF)
private val HandleColor = Color(0xFFCAC4D0)
private val TitleColor = Color(0xFF1D1B20)
private val SubtitleColor = Color(0xFF49454F)
private val AccentColor = Color(0xFF6750A4)
private val KeyboardBg = Color(0xFFE6E0E9)
private val KeyText = Color(0xFF1C1B1F)
private const val KEY_BACKSPACE = "⌫"

@Composable
fun PinScreen(
    mode: PinMode,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PinViewModel = koinViewModel(parameters = { parametersOf(mode) }),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.success) {
        if (state.success) onSuccess()
    }

    PinContent(
        state = state,
        onDigit = viewModel::onDigit,
        onBackspace = viewModel::onBackspace,
        modifier = modifier,
    )
}

@Composable
private fun PinContent(
    state: PinUiState,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val subtitleRes = when (state.mode) {
        PinMode.SETUP -> R.string.pin_setup_title
        PinMode.CONFIRM -> R.string.pin_confirm_title
        PinMode.VERIFY -> R.string.pin_verify_title
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(SheetBg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(HandleColor),
            )
        }

        Text(
            text = stringResource(R.string.settings_pin),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TitleColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(subtitleRes),
            fontSize = 16.sp,
            color = if (state.error) AccentColor else SubtitleColor,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(PIN_LENGTH) { index ->
                val filled = index < state.entered.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .then(
                            if (filled) Modifier.background(AccentColor)
                            else Modifier.border(2.dp, AccentColor, CircleShape),
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (state.error) {
                stringResource(
                    if (state.mode == PinMode.VERIFY) R.string.pin_error_wrong
                    else R.string.pin_error_mismatch,
                )
            } else {
                ""
            },
            fontSize = 14.sp,
            color = AccentColor,
            modifier = Modifier.height(18.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Keypad(onDigit = onDigit, onBackspace = onBackspace)
    }
}

@Composable
private fun Keypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", KEY_BACKSPACE),
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(KeyboardBg)
            .padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                row.forEach { key ->
                    Box(modifier = Modifier.weight(1f)) {
                        when (key) {
                            "" -> Spacer(modifier = Modifier.fillMaxWidth().height(56.dp))
                            KEY_BACKSPACE -> Key(onClick = onBackspace) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                                    contentDescription = stringResource(R.string.cd_delete),
                                    tint = KeyText,
                                    modifier = Modifier.size(24.dp),
                                )
                            }

                            else -> Key(onClick = { onDigit(key) }) {
                                Text(text = key, fontSize = 24.sp, color = KeyText)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Key(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 1.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            content()
        }
    }
}
