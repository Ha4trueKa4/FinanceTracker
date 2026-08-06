package com.example.fianancetracker.feature.settings.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.fianancetracker.core.ui.components.NumericKeyboard
import com.example.fianancetracker.feature.settings.R
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private val SheetBg = Color(0xFFFFFFFF)
private val HandleColor = Color(0xFFCAC4D0)
private val TitleColor = Color(0xFF1D1B20)
private val SubtitleColor = Color(0xFF49454F)
private val AccentColor = Color(0xFF6750A4)

@Composable
fun PinScreen(
    mode: PinMode,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    showHandle: Boolean = true,
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
        showHandle = showHandle,
        modifier = modifier,
    )
}

@Composable
private fun PinContent(
    state: PinUiState,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    showHandle: Boolean,
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
        if (showHandle) {
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
        }

        Spacer(modifier = Modifier.height(if (showHandle) 0.dp else 24.dp))

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
            color = SubtitleColor,
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

        NumericKeyboard(onKeyClick = onDigit, onBackspace = onBackspace)
    }
}
