package me.siddheshkothadi.autofism3.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.siddheshkothadi.autofism3.Constants
import me.siddheshkothadi.autofism3.MainViewModel
import me.siddheshkothadi.autofism3.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLanguage(
    mainViewModel: MainViewModel,
    recreateActivity: () -> Unit
) {
    val context = LocalContext.current
    val selectedLanguage by remember { mainViewModel.selectedLanguage }
    val availableLanguages = remember { Constants.availableLanguages }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
    ) {
        Text(
            stringResource(id = R.string.select_language),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(availableLanguages) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            mainViewModel.onRadioButtonSelected(context, it) {
                                recreateActivity()
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLanguage == it,
                        onClick = {
                            mainViewModel.onRadioButtonSelected(context, it) {
                                recreateActivity()
                            }
                        })
                    Text(
                        it.selectText,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
            Button(onClick = {
                mainViewModel.onLanguageChosen {
                    recreateActivity()
                }
            }) {
                Text(stringResource(id = R.string.select))
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Filled.ArrowRightAlt, "")
            }
        }
    }
}