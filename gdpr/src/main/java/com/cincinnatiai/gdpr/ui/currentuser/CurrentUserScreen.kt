@file:OptIn(ExperimentalMaterial3Api::class)

package com.cincinnatiai.gdpr.ui.currentuser

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cincinnatiai.gdpr.model.RequestType
import com.cincinnatiai.gdpr.utils.invoke
import com.cincinnatiai.gdpr.utils.text
import com.cincinnatiai.gdpr.R.string as STRING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentUserScreen(
    viewModel: CurrentUserViewModel,
    onUserDeleted: (email: String) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var requestType by remember { mutableStateOf(RequestType.INFO) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showExistingRequestDialog by remember { mutableStateOf(false) }
    var showUserDeletedDialog by remember { mutableStateOf(false) }
    val state = viewModel.state.collectAsStateWithLifecycle()
    val requestOptions = RequestType.entries.toList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = STRING.current_user_screen_title.invoke(),
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { STRING.email_address.text() },
                placeholder = { STRING.email_address_placeholder.text() },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )
            )

            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = requestType.title,
                    onValueChange = { title ->
                        requestType = requestOptions.first { it.title.equals(title, true) }
                    },
                    readOnly = true,
                    label = { STRING.request_type.text() },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = isDropdownExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    requestOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.title) },
                            onClick = {
                                requestType =
                                    requestOptions.first { it.title.equals(option.title, true) }
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Information Section
            if (state.value.isLoading) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.White)

                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                if (state.value.infoRequestState != InfoRequestState.None) {
                    when (state.value.infoRequestState) {
                        is InfoRequestState.Error -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color.White)

                            ) {
                                Text("Error in trying to make a request")
                            }
                        }

                        InfoRequestState.Loading -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color.White)

                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        InfoRequestState.None -> {
                        }
                        InfoRequestState.Success ->
                            Toast.makeText(
                                LocalContext.current,
                                STRING.information_request_success,
                                Toast.LENGTH_LONG
                            ).show()

                        InfoRequestState.ExistingInfoRequest -> showExistingRequestDialog = true
                        InfoRequestState.ExistingInfoConfirmed -> {
                            showExistingRequestDialog = false
                            viewModel.clear()
                        }
                    }
                } else {
                    when (state.value.deleteRequestState) {
                        is DeleteRequestState.Error -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color.White)

                            ) {
                                Text("Error in trying to make a request")
                            }
                        }

                        DeleteRequestState.Loading -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .background(Color.White)

                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        DeleteRequestState.None -> {
                            // NOOP
                        }
                        is DeleteRequestState.Success -> {
                            showUserDeletedDialog = true
                        }
                        DeleteRequestState.ExistingDeleteRequest -> showExistingRequestDialog = true
                        is DeleteRequestState.DeleteSuccessAcknowledged -> {
                            onUserDeleted((state.value.deleteRequestState as DeleteRequestState.DeleteSuccessAcknowledged).email)
                        }

                        is DeleteRequestState.ExistingDeleteConfirmed -> {
                            onUserDeleted((state.value.deleteRequestState as DeleteRequestState.ExistingDeleteConfirmed).email)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (requestType == RequestType.INFO) {
                        viewModel.requestInformation(email)
                    } else {
                        showDeleteConfirmDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (requestType == RequestType.DELETE) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                Text(
                    text = STRING.submit.invoke(),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showUserDeletedDialog) {
        AlertDialog(
            onDismissRequest = {
                showUserDeletedDialog = false
                viewModel.onUserDeleteNoticeClicked()
            },
            title = {
                Text(
                    text = STRING.user_deleted_title.invoke(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column {
                    Text(
                        text = STRING.user_deleted_message.invoke(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showUserDeletedDialog = false
                        viewModel.onUserDeleteNoticeClicked()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    STRING.ok.text()
                }
            }
        )
    }

    if (showExistingRequestDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onExistingRequestConfirmed(email, requestType)
            },
            title = {
                Text(
                    text = STRING.existing_request_title.invoke(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(STRING.existing_request_message, email),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = STRING.existing_request_info.invoke(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onExistingRequestConfirmed(email, requestType)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    STRING.ok.text()
                }
            }
        )
    }
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = STRING.are_you_sure_title.invoke(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column {
                    Text(
                        text = STRING.delete_account_message.invoke(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = STRING.delete_account_warning.invoke(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.onUserDeleteRequestConfirmed(email)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    STRING.delete_account.text()
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmDialog = false }
                ) {
                    STRING.cancel.text()
                }
            }
        )
    }
}
