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
import androidx.compose.ui.res.painterResource
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

/**
 * Main composable for the Categories List Screen.
 * Displays a list of categories, total price, and handles user interactions.
 *
 * @param viewModel The ViewModel that manages the state and business logic for this screen.
 * @param innerPadding Padding to be applied to the main content.
 * @param onCategorySelected Callback for when a category is selected.
 * @param onSavedEventsClick Callback for when the save button is clicked.
 */
@Composable
fun CategoriesListScreen(
    viewModel: CategoriesViewModel,
    innerPadding: PaddingValues,
    onCategorySelected: (Category) -> Unit,
    onSavedEventsClick: () -> Unit
) {
    val state by viewModel.viewState.collectAsState()

    // Handle side effects (like navigation) from the ViewModel
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
            viewModel,
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

/**
 * Composable for displaying hint text for adding events.
 *
 * @param text The text to display.
 * @param fontWeight The font weight for the text.
 */
@Composable
fun AddEventHintText(text: String, fontWeight: FontWeight) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = fontWeight),
        color = Color.Black,
        modifier = Modifier.padding(bottom = AppSpacing.medium)
    )
}

/**
 * Composable for displaying a loading indicator.
 */
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Composable for displaying a grid of categories.
 *
 * @param modifier Modifier for styling and layout.
 * @param categories List of categories to display.
 * @param onCategoryClick Callback for when a category is clicked.
 */
@Composable
fun CategoriesGrid(
    viewModel: CategoriesViewModel,
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
                viewModel,
                category = category,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

/**
 * Composable for displaying error text.
 *
 * @param message The error message to display.
 */
@Composable
fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error
    )
}

/**
 * Composable for the save button.
 *
 * @param modifier Modifier for styling and layout.
 * @param onSavedEventsClick Callback for when the button is clicked.
 */
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

/**
 * Composable for displaying a single category card.
 *
 * @param category The category to display.
 * @param onClick Callback for when the card is clicked.
 */
@Composable
fun CategoryCard(viewModel: CategoriesViewModel, category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = AppSpacing.extraSmall)
    ) {
        Column {
            // Image with overlay for Selected Couunt
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
                    text = viewModel.getCategoriesSelectedItemsCount(category.id).toString(),
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

/**
 * Composable Draw Circle with text inside.
 *
 * @param text The text to display.
 * @param modifier modifier to add style.
 */
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