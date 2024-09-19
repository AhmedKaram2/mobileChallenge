package com.karam.mobilechallenge.ui.presentation.categoriesListScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.karam.mobilechallenge.R
import com.karam.mobilechallenge.ui.contract.intent.CategoriesIntent
import com.karam.mobilechallenge.ui.contract.sideEffects.CategorySideEffects
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
            selectedItemsCounts = state.selectedItemsCounts,
            onCategoryClick = { category ->
                viewModel.handleIntent(CategoriesIntent.OpenEventsItemsScreen(category))
            }
        )

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
        modifier = Modifier.padding(bottom = AppSpacing.medium)
    )
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun CategoriesGrid(
    modifier: Modifier,
    categories: List<Category>,
    selectedItemsCounts: Map<Int, Int>,
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
                selectedItemsCount = selectedItemsCounts[category.id] ?: 0,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    selectedItemsCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = AppSpacing.extraSmall)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(category.image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                CircularText(
                    text = selectedItemsCount.toString(),
                    modifier = Modifier
                        .padding(AppSpacing.medium)
                        .align(Alignment.TopEnd)
                        .size(AppSpacing.large)
                )
            }
            Text(
                text = category.title,
                modifier = Modifier.padding(AppSpacing.small),
                style = MaterialTheme.typography.titleMedium
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
fun CircularText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = (AppSpacing.large.value / 2).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}