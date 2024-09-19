package com.karam.mobilechallenge.ui.presentation.evenItemsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter
import com.karam.mobilechallenge.R
import com.karam.mobilechallenge.ui.contract.intent.EventsItemsIntent
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.model.CategoryItems
import com.karam.mobilechallenge.ui.presentation.categoriesListScreen.*
import com.karam.mobilechallenge.ui.theme.AppSpacing
import com.karam.mobilechallenge.ui.theme.Typography

/**
 * Main composable for the Events Items List Screen.
 * Displays a list of items for a specific category and handles user interactions.
 *
 * @param viewModel The ViewModel that manages the state and business logic for this screen.
 * @param category The selected category for which items are displayed.
 * @param onBackClick Callback for when the back button is clicked.
 */
@Composable
fun EventsItemsListScreen(
    viewModel: EventsItemsViewModel,
    category: Category,
    onBackClick: () -> Unit
) {
    val state by viewModel.viewState.collectAsState()

    LaunchedEffect(category) {
        viewModel.handleIntent(EventsItemsIntent.FetchCategoryItemsFromAPI(category.id))
    }

    Scaffold(
        topBar = {
            CenteredTitleTopAppBar(
                title = category.title,
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AddEventHintText(
                    stringResource(R.string.add_to_your_event_to_view_our_cost_estimate),
                    FontWeight.Normal
                )

                TotalPriceText(totalPrice = state.totalPrice)

                state.items?.let { items ->
                    ItemsGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        items = items,
                        onItemCheckClick = { index ->
                            viewModel.handleIntent(EventsItemsIntent.ItemCheckedClick(index))
                        },
                    )
                }
            }

            state.error?.let {
                ErrorText(it)
            }

            if (state.isLoading) {
                LoadingIndicator()
            }
        }
    }
}

/**
 * Composable for displaying the total price.
 *
 * @param totalPrice The total price to display.
 */
@Composable
fun TotalPriceText(totalPrice: Double) {
    Text(
        text = stringResource(R.string.total_price, "%.2f".format(totalPrice)),
        style = Typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = AppSpacing.medium)
    )
}

/**
 * Composable for displaying a grid of category items.
 *
 * @param modifier Modifier for styling and layout.
 * @param items List of category items to display.
 * @param onItemCheckClick Callback for when an item is clicked.
 */
@Composable
fun ItemsGrid(
    modifier: Modifier,
    items: List<CategoryItems>,
    onItemCheckClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        modifier = modifier
    ) {
        itemsIndexed(items) { index, item ->
            CategoryItemCard(
                item = item,
                onItemCheckClick = {
                    onItemCheckClick(index)
                }
            )
        }
    }
}

/**
 * Composable for displaying a single category item card.
 *
 * @param item The category item to display.
 * @param onItemCheckClick Callback for when the item is clicked.
 */
@Composable
fun CategoryItemCard(
    item: CategoryItems,
    onItemCheckClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(AppSpacing.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = AppSpacing.extraSmall),
        onClick = onItemCheckClick
    ) {
        Column {
            // Image with overlay for selection icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(item.image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppSpacing.small),
                    contentAlignment = Alignment.TopEnd
                ) {
                    if (item.isSelected) {
                        Image(
                            painter = painterResource(id = R.drawable.item_added),
                            contentDescription = stringResource(R.string.selected),
                            modifier = Modifier.size(AppSpacing.large)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.add_item),
                            contentDescription = stringResource(R.string.add_item),
                            modifier = Modifier.size(AppSpacing.large)
                        )
                    }
                }
            }

            // Text content below the image
            Column(modifier = Modifier.padding(AppSpacing.medium)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$${item.minBudget}-${item.maxBudget}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Composable for the top app bar with a centered title.
 *
 * @param title The title to display in the app bar.
 * @param onBackClick Callback for when the back button is clicked.
 * @param modifier Modifier for styling and layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTitleTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        modifier = modifier
    )
}