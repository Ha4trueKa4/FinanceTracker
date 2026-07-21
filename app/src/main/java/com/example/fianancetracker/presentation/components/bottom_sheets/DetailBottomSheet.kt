package com.example.fianancetracker.presentation.components.bottom_sheets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fianancetracker.presentation.components.common.PieChartSlice
import com.example.fianancetracker.presentation.utils.toThousand

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailBottomSheet(
    slices: List<PieChartSlice>,
    totalAmount: Double,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val coloredSlices = remember(slices) {
        slices.map { slice ->
            val hue = ((slice.label.hashCode() and Int.MAX_VALUE) % 360).toFloat()
            slice.copy(color = Color.hsl(hue, 0.6f, 0.55f))
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 32.dp, height = 4.dp)
                        .clip(RoundedCornerShape(100))
                        .background(Color(0xFFC4C7C5))
                )
            }

            Text(
                text = "Детализация",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1D1B20),
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            )

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val strokeWidth = size.width * 0.1f
                    val radius = (size.width - strokeWidth) / 2f
                    val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                    val arcSize = Size(radius * 2, radius * 2)

                    var startAngle = -90f
                    val total = coloredSlices.sumOf { it.amount }
                    coloredSlices.forEach { slice ->
                        val sweepAngle =
                            if (total > 0) (slice.amount / total * 360).toFloat() else 0f
                        drawArc(
                            color = slice.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth)
                        )
                        startAngle += sweepAngle
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Всего за период",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF49454F),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = toThousand(totalAmount),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1B20),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            coloredSlices.forEach { slice ->
                DetailCategoryRow(slice = slice, total = coloredSlices.sumOf { it.amount })
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailCategoryRow(
    slice: PieChartSlice,
    total: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(slice.color)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = slice.label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1D1B20),
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${toThousand(slice.amount)} (${String.format("%.0f", slice.percentage)}%)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF49454F)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(100))
                .background(Color(0xFFC4C7C5))
        ) {
            val fraction = if (total > 0) (slice.amount / total).toFloat() else 0f
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(100))
                    .background(slice.color)
            )
        }
    }
}