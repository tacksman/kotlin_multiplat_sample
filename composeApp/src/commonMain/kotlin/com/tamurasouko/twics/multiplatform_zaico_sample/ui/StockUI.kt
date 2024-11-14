package com.tamurasouko.twics.multiplatform_zaico_sample.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamurasouko.twics.multiplatform_zaico_sample.Stock

@Composable
fun StockItem(stock: Stock, onClick: () -> Unit) {
    Row {
        Text(
            text = stock.title,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 8.dp, horizontal = 4.dp
                )
                .height(32.dp)
                .weight(1f)
                .clickable {
                    onClick()
                }
        )
    }
}