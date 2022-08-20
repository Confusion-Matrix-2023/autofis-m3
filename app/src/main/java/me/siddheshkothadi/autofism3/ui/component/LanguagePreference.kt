package me.siddheshkothadi.autofism3.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.siddheshkothadi.autofism3.Constants
import me.siddheshkothadi.autofism3.R
import me.siddheshkothadi.autofism3.ui.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePreference(
    defaultLanguage: Language,
    onLanguageSelected: (Language) -> Unit
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf(defaultLanguage) }

    Column(Modifier.padding(18.dp)) {
        Text(
            text = stringResource(R.string.language),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { isDialogOpen = true }
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Translate,
                null,
                tint = MaterialTheme.colorScheme.onSurface.copy(0.69f)
            )
            Spacer(Modifier.width(16.dp))
            Column() {
                Text(
                    stringResource(R.string.chosen_language),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    defaultLanguage.langName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface.copy(0.69f)
                )
            }
        }

        if (isDialogOpen) {
            AlertDialog(
                onDismissRequest = {
                    isDialogOpen = false
                },
                icon = {
                    Icon(Icons.Filled.Translate, contentDescription = null)
                },
                title = {
                    Text(text = stringResource(id = R.string.select_language))
                },
                text = {
                    LazyColumn {
                        item {
                            Text(
                                text = stringResource(R.string.app_restart_note_after_language_selection),
                                Modifier.padding(bottom = 12.dp)
                            )
                            Divider()
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        items(Constants.availableLanguages) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedLanguage = it
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedLanguage == it,
                                    onClick = {
                                        selectedLanguage = it
                                    })
                                Text(
                                    it.langName,
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onLanguageSelected(selectedLanguage)
                        }
                    ) {
                        Text(stringResource(id = R.string.select))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isDialogOpen = false
                        }
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            )
        }
    }
}