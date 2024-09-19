package com.karam.mobilechallenge.ui.presentation.evenItemsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import coil.compose.rememberAsyncImagePainter
import com.karam.mobilechallenge.R
import com.karam.mobilechallenge.contract.intent.EventsItemsIntent
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.model.CategoryItems
import com.karam.mobilechallenge.ui.presentation.categoriesListScreen.AddEventHintText
import com.karam.mobilechallenge.ui.presentation.categoriesListScreen.ErrorText
import com.karam.mobilechallenge.ui.presentation.categoriesListScreen.LoadingIndicator
import com.karam.mobilechallenge.ui.theme.AppSpacing
import com.karam.mobilechallenge.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)

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
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                title = {
                    Text(text = category.title)
                }
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

@Composable
fun TotalPriceText(totalPrice: Double) {
    Text(
        text = stringResource(R.string.total_price, "%.2f".format(totalPrice)),
        style = Typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = AppSpacing.medium)
    )
}

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



