package com.tamurasouko.twics.multiplatform_zaico_sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.tamurasouko.twics.multiplatform_zaico_sample.ui.StockItem

fun MainViewController() = ComposeUIViewController {
    AppAndroidPreview()
}

@Composable
fun AppAndroidPreview() {
    val stocks = remember {
        mutableStateListOf<Stock>()
    }
    for (i in 0..4) {
        stocks.add(Stock("item-$i"))
    }
    Column {
        StockList(stocks)
        Button(
            onClick = {
                stocks.add(Stock("new-item-${stocks.size}"))
            }
        ) {
            Text("Add")
        }
    }
}

@Composable
fun StockList(stocks: List<Stock>) {
    LazyColumn {
        items(stocks.size) {
            StockItem(stocks[it]) {

            }
        }
    }
}