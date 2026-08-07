package com.crome.forecastpoint.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crome.forecastpoint.data.GeocodeResult
import com.crome.forecastpoint.ui.theme.OnSurfaceMuted
import com.crome.forecastpoint.ui.theme.SurfaceDark

@Composable
fun SearchScreen(
    results: List<GeocodeResult>,
    searching: Boolean,
    onQueryChange: (String) -> Unit,
    onSelect: (GeocodeResult) -> Unit,
) {
    var query by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
    ) {
        Text(
            text = "Search for a U.S. city or place. Selecting a result adds it to your saved cities and loads the forecast.",
            color = OnSurfaceMuted,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                onQueryChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("City, state, or place") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
        )

        if (searching) {
            CircularProgressIndicator(Modifier.padding(16.dp))
        }

        LazyColumn(Modifier.padding(top = 12.dp)) {
            items(results) { result ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(SurfaceDark)
                        .clickable { onSelect(result) }
                        .padding(16.dp),
                ) {
                    Text(result.name, color = Color.White, fontSize = 16.sp)
                    Text(result.displayName, color = OnSurfaceMuted, fontSize = 12.sp)
                }
            }
        }
    }
}
