package com.klivvr.assignment.ui.screens.city.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klivvr.assignment.R
import com.klivvr.assignment.ui.theme.ArialFamily
import com.klivvr.assignment.ui.theme.titleTextColor

@Composable
fun CityScreenHeader(itemCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.city_search),
            color = MaterialTheme.colorScheme.titleTextColor,
            fontSize = 28.sp,
            fontFamily = ArialFamily,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (itemCount > 0) "$itemCount cities" else "",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            // Center the text horizontally
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        )
    }
}