package com.karam.mobilechallenge.ui.presentation.categoriesListScreen


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
import coil.compose.rememberAsyncImagePainter
import com.karam.mobilechallenge.R
import com.karam.mobilechallenge.contract.intent.CategoriesIntent
import com.karam.mobilechallenge.contract.sideEffects.CategorySideEffects
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.ui.presentation.evenItemsScreen.TotalPriceText
import com.karam.mobilechallenge.ui.theme.AppSpacing

@Composable
fun CategoriesListScreen(
    viewModel: CategoriesViewModel,
    innerPadding: PaddingValues,
    onCategorySelected: (Category) -> Unit,
    onSavedEventsClick: () -> Unit
) {
    val state by viewModel.viewState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.categorySideEffects.collect { sideEffect ->
            when (sideEffect) {
                is CategorySideEffects.OpenCategoriesItemsScreen -> onCategorySelected(sideEffect.category)
                is CategorySideEffects.OpenSavedEventsScreen -> onSavedEventsClick()
            }
        }
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
            text = stringResource(R.string.event_builder),
            FontWeight.Bold
        )

        AddEventHintText(
            stringResource(R.string.add_to_your_event_to_view_our_cost_estimate),
            FontWeight.Normal
        )

        TotalPriceText(totalPrice = state.totalPrice)

        CategoriesGrid(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            categories = state.categories,
            onCategoryClick = { category ->
                viewModel.handleIntent(CategoriesIntent.OpenEventsItemsScreen(category))
            }
        )

        // Display error message if available
        state.error?.let {
            ErrorText(it)
        }

        if (state.isLoading) {
            LoadingIndicator()
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
    modifier: Modifier,
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        modifier = modifier
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,

                onClick = { onCategoryClick(category)
                }
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
fun CategoryCard(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
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