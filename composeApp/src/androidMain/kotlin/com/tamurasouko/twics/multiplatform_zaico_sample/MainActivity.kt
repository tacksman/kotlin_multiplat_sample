package com.tamurasouko.twics.multiplatform_zaico_sample

import android.os.Bundle
import android.print.PrintAttributes.Margins
import android.text.Spannable
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamurasouko.twics.multiplatform_zaico_sample.ui.StockItem
import kotlin.coroutines.coroutineContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppAndroidPreview()
        }
    }
}

@Preview
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
    val context = LocalContext.current
    LazyColumn {
        items(stocks.size) {
            StockItem(stocks[it]) {
                Toast.makeText(
                    context,
                    "${stocks[it].title} is clicked.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
