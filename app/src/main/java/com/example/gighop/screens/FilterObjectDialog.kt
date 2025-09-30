package com.example.gighop.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.gighop.viewmodel.ObjectViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun FilterObjectDialog(
    objectViewModel: ObjectViewModel,
    onApplyFilters: (String, String, String, Float, Long?, Long?, Float) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    var author by remember { mutableStateOf(objectViewModel.currentFilters.author) }
    var selectedType by remember { mutableStateOf(objectViewModel.currentFilters.type) }
    var subject by remember { mutableStateOf(objectViewModel.currentFilters.subject) }
    var rating by remember { mutableStateOf(objectViewModel.currentFilters.rating.toFloat()) }
    var startDate by remember { mutableStateOf(objectViewModel.currentFilters.startDate) }
    var endDate by remember { mutableStateOf(objectViewModel.currentFilters.endDate) }
    var radius by remember { mutableStateOf(objectViewModel.currentFilters.radius) }


    var expanded by remember { mutableStateOf(false) }
    val types = listOf("Rock", "Rap", "Jazz", "Folk", "Techno", "TRap")

    Dialog(onDismissRequest = { onDismiss() })
    {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Gray),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- Title Row with Close Button ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search Live Music Events",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author(username)", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 18.sp
                    )
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Place", fontSize = 18.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    maxLines = 1,
                    textStyle = TextStyle(
                        fontSize = 18.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { expanded = true }) {
                        Text(
                            text = if (selectedType.isNotEmpty()) selectedType else "Type",
                            fontSize = 18.sp
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        types.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = type, fontSize = 18.sp) },
                                onClick = {
                                    selectedType = type
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Rating: $rating", fontSize = 18.sp)
                Slider(
                    value = rating,
                    onValueChange = { newValue ->
                        // Round to 1 decimal places
                        val rounded = (newValue * 10).toInt() / 10f
                        rating = rounded
                    },
                    valueRange = 1f..10f
                )

                Spacer(modifier = Modifier.height(16.dp))
                DateTimePicker(
                    label = "Start Date/Time",
                    selectedDateTime = startDate,
                    onDateTimeChange = { startDate = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                DateTimePicker(
                    label = "End Date/Time",
                    selectedDateTime = endDate,
                    onDateTimeChange = { endDate = it }
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Radius: $radius km", fontSize = 18.sp)
                Slider(
                    value = radius,
                    onValueChange = { newValue ->
                        // Round to 2 decimal places
                        val rounded = (newValue * 1000).toInt() / 1000f
                        radius = rounded
                    },
                    valueRange = 0f..10f
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                   Button(onClick = {
                        objectViewModel.resetFilterValues() // Reset filter in ViewModel-u
                        author = "" // Reset all values
                        selectedType = ""
                        subject = ""
                        rating = 0f
                        startDate = null
                        endDate = null
                        radius = 0.0f

                        onClearFilters()
                    }) {
                        Text("Clear", fontSize = 16.sp)
                    }
                    Button(onClick = {
                        onApplyFilters(
                            author,
                            selectedType,
                            subject,
                            rating,
                            startDate,
                            endDate,
                            radius
                        )
                    }) {
                        Text("Apply", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun DateTimePicker(
    label: String,
    selectedDateTime: Long?,
    onDateTimeChange: (Long?) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // DatePickerDialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)

                // When date is picked, show TimePickerDialog
                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        onDateTimeChange(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true // is24HourView
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Button(onClick = { datePickerDialog.show() }) {
        Text(
            text = if (selectedDateTime != null)
                dateFormat.format(Date(selectedDateTime))
            else label,
            fontSize = 16.sp
        )
    }
}
