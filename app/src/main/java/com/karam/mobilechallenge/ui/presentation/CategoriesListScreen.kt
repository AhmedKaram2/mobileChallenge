@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

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
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.karam.mobilechallenge.contract.intent.CategoriesIntent
import com.karam.mobilechallenge.contract.sideEffects.CategorySideEffects
import com.karam.mobilechallenge.contract.state.CategoriesState
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.ui.viewmodel.CategoriesViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CategoriesListScreen(
    viewModel: CategoriesViewModel,
    innerPadding: PaddingValues,
    navigator: ThreePaneScaffoldNavigator<Any>
) {

    val state by viewModel.state.collectAsState()
    LaunchedEffect(viewModel.categorySideEffects) {
        viewModel.categorySideEffects.collectLatest {
            when (it){
                is CategorySideEffects.OpenCategoriesItemsScreen -> {
                   navigator.navigateTo(
                       pane = ListDetailPaneScaffoldRole.Detail,
                       content = it.categoryId
                   )
                }
                else -> {}
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
            .padding(16.dp)
    ) {
        AddEventHintText() // Extract to a separate Composable

        when (state) {
            is CategoriesState.Loading -> LoadingIndicator()
            is CategoriesState.CategoriesLoaded -> {
                (state as CategoriesState.CategoriesLoaded).categories?.let { category ->
                    if (category.isEmpty()) {
                        Text("No categories available")
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
                .padding(top = 16.dp)
        )
    }
}

@Composable
fun AddEventHintText() {
    Text(
        text = "Add to your event to view our cost estimate.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = Modifier
            .padding(bottom = 16.dp)
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(categories ?: emptyList()) { category ->
            CategoryCard(category, viewModel = viewModel)
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
        Text("Save")
    }
}

@Composable
fun CategoryCard(category: Category , viewModel: CategoriesViewModel ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
              viewModel.setIntent(CategoriesIntent.OpenEventsItemsScreen(category.id))
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}