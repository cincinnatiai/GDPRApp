package com.cincinnatiai.gdpr.ui.accountdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cincinnatiai.cincinnatiaccountcommons.model.AccountWithProfileContract
import com.cincinnatiai.gdpr.model.RequestType
import com.cincinnatiai.gdpr.ui.component.AccountItem
import com.cincinnatiai.gdpr.utils.invoke
import com.cincinnatiai.gdpr.utils.text
import com.cincinnatiai.gdpr.R.string as STRING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    accountWithProfile: AccountWithProfileContract,
    onBackClick: () -> Unit,
    onRequestSubmit: (RequestType, String) -> Unit,
) {
    val account = accountWithProfile.account
    var showEmailDialog by remember { mutableStateOf(false) }
    var currentRequestType by remember { mutableStateOf<RequestType?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var pendingEmail by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account: ${account.title}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, STRING.back.invoke())
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AccountItem(accountWithProfile) {
                // NOOP
            }
            Text(
                text = STRING.privacy_requests.invoke(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        currentRequestType = RequestType.INFO
                        showEmailDialog = true
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = STRING.information_request.invoke(),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = STRING.information_request.invoke(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = STRING.request_copy.invoke(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        currentRequestType = RequestType.DELETE
                        showEmailDialog = true
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = STRING.delete_request.invoke(),
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = STRING.delete_request.invoke(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = STRING.request_deletion.invoke(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }

    if (showEmailDialog && currentRequestType != null) {
        EmailInputDialog(
            requestType = currentRequestType!!,
            onDismiss = {
                showEmailDialog = false
                currentRequestType = null
            },
            onConfirm = { email ->
                pendingEmail = email
                showEmailDialog = false

                if (currentRequestType == RequestType.DELETE) {
                    showDeleteConfirmation = true
                } else {
                    onRequestSubmit(currentRequestType!!, email)
                    currentRequestType = null
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            accountName = account.title,
            onDismiss = {
                showDeleteConfirmation = false
                currentRequestType = null
                pendingEmail = ""
            },
            onConfirm = {
                onRequestSubmit(RequestType.DELETE, pendingEmail)
                showDeleteConfirmation = false
                currentRequestType = null
                pendingEmail = ""
            }
        )
    }
}

@Composable
fun EmailInputDialog(
    requestType: RequestType,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (requestType) {
                    RequestType.INFO -> STRING.information_request.invoke()
                    RequestType.DELETE -> STRING.delete_request.invoke()
                    else -> ""
                }
            )
        },
        text = {
            Column {
                Text(
                    text = STRING.enter_email_to_submit.invoke(),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    },
                    label = { STRING.email_address.text() },
                    singleLine = true,
                    isError = !isEmailValid && email.isNotEmpty(),
                    supportingText = if (!isEmailValid && email.isNotEmpty()) {
                        { STRING.invalid_email_warning.text() }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(email) },
                enabled = email.isNotEmpty() && isEmailValid
            ) {
                Text(STRING.submit.invoke())
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(STRING.cancel.invoke())
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    accountName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = STRING.warning.invoke(),
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            STRING.confirm_account_deletion.text()
        },
        text = {
            Text(
                stringResource(STRING.confirm_account_deletion, accountName)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                STRING.delete_my_data.text()
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                STRING.cancel.text()
            }
        }
    )
}