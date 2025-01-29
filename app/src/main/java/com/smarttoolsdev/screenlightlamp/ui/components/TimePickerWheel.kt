package com.smarttoolsdev.screenlightlamp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimePickerWheel(
    hours: Int,
    minutes: Int,
    onHoursChanged: (Int) -> Unit,
    onMinutesChanged: (Int) -> Unit
) {
    val hourList = (0..23).toList()
    val minuteList = (0..59).toList()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PickerWheel(
            items = hourList,
            selectedItem = hours,
            onItemSelected = onHoursChanged,
            label = "Hours"
        )
        Spacer(Modifier.width(8.dp))
        PickerWheel(
            items = minuteList,
            selectedItem = minutes,
            onItemSelected = onMinutesChanged,
            label = "Minutes"
        )
    }
}

@Composable
private fun PickerWheel(
    items: List<Int>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    label: String
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = items.indexOf(selectedItem))

    LaunchedEffect(selectedItem) {
        listState.scrollToItem(items.indexOf(selectedItem))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier.height(120.dp).width(60.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                itemsIndexed(items) { _, item ->
                    Text(
                        text = "%02d".format(item),
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (item == selectedItem) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
        listState.firstVisibleItemIndex.let {
            onItemSelected(items[it])
        }
    }
}
