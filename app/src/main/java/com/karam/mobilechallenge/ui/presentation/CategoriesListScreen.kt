
package com.karam.mobilechallenge.ui.presentation


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import coil.compose.rememberAsyncImagePainter
import com.karam.mobilechallenge.R
import com.karam.mobilechallenge.contract.intent.CategoriesIntent
import com.karam.mobilechallenge.contract.sideEffects.CategorySideEffects
import com.karam.mobilechallenge.contract.state.CategoriesState
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.ui.theme.AppSpacing
import com.karam.mobilechallenge.ui.viewmodel.CategoriesViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CategoriesListScreen(
    viewModel: CategoriesViewModel,
    innerPadding: PaddingValues,
    onCategorySelected: (Category) -> Unit
) {

    val state by viewModel.state.collectAsState()
    LaunchedEffect(viewModel.categorySideEffects) {
        viewModel.categorySideEffects.collectLatest {
            when (it){
                is CategorySideEffects.OpenCategoriesItemsScreen -> {
                   onCategorySelected(it.category)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setIntent(
            CategoriesIntent.FetchCategoriesFromAPI()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(AppSpacing.medium),
            verticalArrangement = Arrangement.Center, // Center vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center horizontally

    ) {
        AddEventHintText()

        when (state) {
            is CategoriesState.Loading -> LoadingIndicator()
            is CategoriesState.CategoriesLoaded -> {
                (state as CategoriesState.CategoriesLoaded).categories?.let { category ->
                    if (category.isEmpty()) {
                        Text(stringResource(R.string.no_events_available))
                    } else {
                        CategoriesGrid(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            category
                        ,viewModel)
                    }
                }

            }

            is CategoriesState.Error -> ErrorText((state as CategoriesState.Error).message)
            else -> { /* Handle other states if needed */
            }
        }

        SaveButton(
            Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.medium)
        )
    }
}

@Composable
fun AddEventHintText() {
    Text(
        text = stringResource(R.string.add_to_your_event_to_view_our_cost_estimate),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = Modifier
            .padding(bottom = AppSpacing.medium)

    )
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize(), // Ensures Box fills available space
        contentAlignment = Alignment.Center // Centers the content inside the Box
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CategoriesGrid(
    modifier: Modifier, categories: List<Category>? , viewModel: CategoriesViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        modifier = modifier
    ) {
        items(categories ?: emptyList()) { category ->
            CategoryCard(category,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
fun SaveButton(modifier: Modifier) {
    Button(
        onClick = { /* Handle save action */ },
        modifier = modifier
    ) {
        Text(stringResource(R.string.save))
    }
}

@Composable
fun CategoryCard(category: Category , viewModel: CategoriesViewModel ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
                viewModel.setIntent(CategoriesIntent.OpenEventsItemsScreen(category))
            },
        elevation = CardDefaults.cardElevation(defaultElevation = AppSpacing.extraSmall)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(category.image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = category.title,
                modifier = Modifier.padding(AppSpacing.small),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}