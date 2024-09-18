package com.karam.mobilechallenge.ui.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.TopAppBarDefaults
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
import com.karam.mobilechallenge.contract.intent.CategoryItemsIntent
import com.karam.mobilechallenge.contract.state.CategoryItemsState
import com.karam.mobilechallenge.data.model.Category
import com.karam.mobilechallenge.data.model.CategoryItems
import com.karam.mobilechallenge.ui.theme.AppSpacing
import com.karam.mobilechallenge.ui.theme.Typography
import com.karam.mobilechallenge.ui.viewmodel.CategoryItemsViewModel

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun CategoryItemsListScreen(
    viewModel: CategoryItemsViewModel,
    category: Category,
    totalPrice: Double,
    selectedItems: List<CategoryItems>,
    onSelectedItemsChange: (List<CategoryItems>) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = category) {
        viewModel.setIntent(CategoryItemsIntent.FetchCategoryItemsFromAPI(category.id))
    }


    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White),
            verticalArrangement = Arrangement.Center, // Center vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center horizontally

        ) {

            // AddEventHintText composable
            AddEventHintText(
                stringResource(
                    R.string.add_to_your_event_to_view_our_cost_estimate
                ), FontWeight.Normal
            )

            // Display total price
            TotalPriceText(totalPrice = totalPrice)

            when (state) {
                is CategoryItemsState.Loading -> LoadingIndicator()
                is CategoryItemsState.CategoryItemsLoaded -> {
                    (state as CategoryItemsState.CategoryItemsLoaded).items?.let { items ->
                        if (items.isEmpty()) {
                            Text(stringResource(R.string.no_items_available))
                        } else {
                            ItemsGrid(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                items = items,
                                selectedItems = selectedItems,
                                onItemSelected = { item ->
                                    val newList = selectedItems.toMutableList()
                                    if (newList.contains(item)) newList.remove(item)
                                    else newList.add(item)
                                    onSelectedItemsChange(newList)
                                }
                            )
                        }
                    }
                }

                is CategoryItemsState.Error -> ErrorText((state as CategoryItemsState.Error).message)
                CategoryItemsState.Idle -> LoadingIndicator()
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
    selectedItems: List<CategoryItems>,
    onItemSelected: (CategoryItems) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium),
        modifier = modifier
    ) {
        items(items) { item ->
            val isSelected = selectedItems.contains(item)
            CategoryItemCard(item, isSelected, onItemSelected)
        }
    }
}

@Composable
fun CategoryItemCard(
    item: CategoryItems,
    isSelected: Boolean,
    onItemSelected: (CategoryItems) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(AppSpacing.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = AppSpacing.extraSmall),
        onClick = { onItemSelected(item) }
    ) {
        Column {
            // Image with overlay for selection icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Ensures the image takes up most of the space
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
                    if (isSelected) {
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






