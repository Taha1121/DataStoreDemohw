package edu.farmingdale.datastoredemo.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.farmingdale.datastoredemo.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope

import edu.farmingdale.datastoredemo.data.local.LocalEmojiData
import edu.farmingdale.datastoredemo.ui.theme.DataStoreDemoTheme
import kotlinx.coroutines.launch

/*
 * Screen level composable
 */
@Composable
fun EmojiReleaseApp(
    emojiViewModel: EmojiScreenViewModel = viewModel(
        factory = EmojiScreenViewModel.Factory
    )
) {

    // THis collects the theme
    val isDarkTheme by emojiViewModel.isDarkTheme.collectAsState()
    // Create a courotine scope for suspend function
    val coroutineScope = rememberCoroutineScope()

    DataStoreDemoTheme(darkTheme = isDarkTheme) {
        EmojiScreen(
            uiState = emojiViewModel.uiState.collectAsState().value,
            selectLayout = emojiViewModel::selectLayout,
            toggleTheme = { isDark ->
                // Launch courintine
                coroutineScope.launch {
                    emojiViewModel.toggleTheme(isDark)
                }
            },
            isDarkTheme = isDarkTheme
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmojiScreen(
    uiState: EmojiReleaseUiState,
    selectLayout: (Boolean) -> Unit,
    toggleTheme: (Boolean) -> Unit,
    isDarkTheme: Boolean
) {
    val isLinearLayout = uiState.isLinearLayout
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.top_bar_name)) },
                actions = {
                    IconButton(
                        onClick = {
                            selectLayout(!isLinearLayout)
                        }
                    ) {
                        Icon(
                            painter = painterResource(uiState.toggleIcon),
                            contentDescription = stringResource(uiState.toggleContentDescription),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    //add switch to go back and forth between dark and light
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { toggleTheme(it)}
                    )

                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.inversePrimary
                )
            )
        }
    ) { innerPadding ->
        val modifier = Modifier
            .padding(
                top = dimensionResource(R.dimen.padding_medium),
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium),
            )
        if (isLinearLayout) {
            EmojiReleaseLinearLayout(
                modifier = modifier.fillMaxWidth(),
                contentPadding = innerPadding
            )
        } else {
            EmojiReleaseGridLayout(
                modifier = modifier,
                contentPadding = innerPadding,
            )
        }
    }
}

@Composable
fun EmojiReleaseLinearLayout(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
    ) {
        items(
            items = LocalEmojiData.EmojiList,
            key = { e -> e }
        ) { emoji ->
            Card(
                //Make it clickable and also add toast to present emoji when clicked
                modifier = Modifier
                    .clickable {
                        val message = "you clicked: $emoji"
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                    .padding(dimensionResource(R.dimen.padding_medium)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = emoji,
                    fontSize = 50.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium)),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EmojiReleaseGridLayout(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        items(
            items = LocalEmojiData.EmojiList,
            key = { emoji -> emoji }
        ) { emoji ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                //Make it clickable and also add toast to present emoji when clicked
                modifier = Modifier.height(110.dp)
                .clickable {
                    val message = "you clicked: $emoji"
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()            },
                shape = MaterialTheme.shapes.medium

            ) {
                Text(
                    text = emoji, fontSize = 50.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(dimensionResource(R.dimen.padding_small))
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
