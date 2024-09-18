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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    totalPrice: Double,
    onCategorySelected: (Category) -> Unit,
    onSavedEventsClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(viewModel.categorySideEffects) {
        viewModel.categorySideEffects.collectLatest {
            when (it) {
                is CategorySideEffects.OpenCategoriesItemsScreen -> onCategorySelected(it.category)
                is CategorySideEffects.OpenSavedEventsScreen -> onSavedEventsClick()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setIntent(CategoriesIntent.FetchCategoriesFromAPI())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(AppSpacing.medium),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AddEventHintText(
            text = stringResource(R.string.event_builder), FontWeight.Bold
        )

        AddEventHintText(
            stringResource(R.string.add_to_your_event_to_view_our_cost_estimate), FontWeight.Normal
        )

        // Display the total price
        TotalPriceText(totalPrice = totalPrice)

        when (state) {
            is CategoriesState.Loading -> LoadingIndicator()
            is CategoriesState.CategoriesLoaded -> {
                (state as CategoriesState.CategoriesLoaded).categories?.let { categories ->
                    if (categories.isEmpty()) {
                        Text(stringResource(R.string.no_events_available))
                    } else {
                        CategoriesGrid(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            categories = categories,
                            viewModel = viewModel
                        )
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
                .padding(top = AppSpacing.medium),
            onSavedEventsClick
        )
    }
}

@Composable
fun AddEventHintText(text: String, fontWeight: FontWeight) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = fontWeight),
        color = Color.Black,
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
    modifier: Modifier, categories: List<Category>?, viewModel: CategoriesViewModel
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        modifier = modifier
    ) {
        items(categories ?: emptyList()) { category ->
            CategoryCard(
                category,
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
fun SaveButton(modifier: Modifier, onSavedEventsClick: () -> Unit) {
    Button(
        onClick = { onSavedEventsClick() },
        modifier = modifier,
        shape = RoundedCornerShape(AppSpacing.small)
    ) {
        Text(stringResource(R.string.save))
    }
}

@Composable
fun CategoryCard(category: Category, viewModel: CategoriesViewModel) {
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