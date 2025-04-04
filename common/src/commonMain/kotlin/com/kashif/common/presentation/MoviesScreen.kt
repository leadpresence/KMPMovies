package com.kashif.common.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.kashif.common.domain.model.MoviesDomainModel
import com.kashif.common.paging.Result
import com.kashif.common.presentation.components.AsyncImage
import com.kashif.common.presentation.components.ShimmerStar
import com.kashif.common.presentation.theme.SunnySideUp
import kotlinx.coroutines.delay


@Composable
internal fun MoviesScreen(screenModel: HomeScreenViewModel, player:@Composable (String) -> Unit) {
    val pagerList by screenModel.popularMovies.collectAsState()
    val latestMovies by screenModel.latestMovies.first.collectAsState()
    val popularMovies by screenModel.popularMoviesPaging.first.collectAsState()
    val topRatedMovies by screenModel.topRatedMovies.first.collectAsState()
    val upcomingMovies by screenModel.upcomingMovies.first.collectAsState()
    val nowPlayingMovies by screenModel.nowPlayingMoviesPaging.first.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top) {
            player( "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
            Spacer(Modifier.height(32.dp))
            Header()
            Spacer(Modifier.height(8.dp))
            // HorizontalScroll()
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = rememberLazyListState(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                pager(pagerList)
                // latestMovies(latestMovies)
                popularMovies(popularMovies)
                topRatedMovies(topRatedMovies)
                upComingMovies(upcomingMovies)
                nowPlayingMovies(nowPlayingMovies, screenModel.nowPlayingMoviesPaging.second)
            }
        }
}

@Composable
internal fun HorizontalScroll(
    movies: List<MoviesDomainModel>,
    heading: String,
    onMovieClick: (MoviesDomainModel) -> Unit
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.Start) {
            Text(
                text = heading,
                style = MaterialTheme.typography.h3.copy(fontWeight = FontWeight.W700))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                    items(movies) { movie ->
                        MovieCardSmall(movie = movie, onClick = { onMovieClick(movie) })
                    }
                }
        }
}

internal fun LazyListScope.pager(pagerList: MoviesState) {
    item {
        when (pagerList) {
            is MoviesState.Error -> {}
            MoviesState.Idle -> {
                Text(text = "idle")
            }
            MoviesState.Loading -> {
                Text(text = "loading")
            }
            is MoviesState.Success -> {
                AutoScrollingHorizontalSlider(pagerList.movies) {}
            }
        }
    }
}

internal fun LazyListScope.latestMovies(latestMovies: Result<List<MoviesDomainModel>>) {
    when (latestMovies) {
        is Result.Loading -> {
            item { Text("idle") }
        }
        is Result.Error -> {
            item { Text(latestMovies.exception) }
        }
        is Result.Success -> {
            item {
                HorizontalScroll(
                    movies = latestMovies.data,
                    heading = "Latest Movies",
                    onMovieClick = { movie -> println(movie.title) })
            }
        }
    }
}

internal fun LazyListScope.popularMovies(popularMovies: Result<List<MoviesDomainModel>>) {
    when (popularMovies) {
        is Result.Loading -> {
            item { Text("idle") }
        }
        is Result.Error -> {
            item { Text(popularMovies.exception) }
        }
        is Result.Success -> {
            item {
                HorizontalScroll(
                    movies = popularMovies.data,
                    heading = "Popular Movies",
                    onMovieClick = { movie -> println(movie.title) })
            }
        }
    }
}

internal fun LazyListScope.topRatedMovies(popularMovies: Result<List<MoviesDomainModel>>) {
    when (popularMovies) {
        is Result.Loading -> {
            item { Text("idle") }
        }
        is Result.Error -> {
            item { Text(popularMovies.exception) }
        }
        is Result.Success -> {
            item {
                HorizontalScroll(
                    movies = popularMovies.data,
                    heading = "Top Rated Movies",
                    onMovieClick = { movie -> println(movie.title) })
            }
        }
    }
}

internal fun LazyListScope.upComingMovies(popularMovies: Result<List<MoviesDomainModel>>) {
    when (popularMovies) {
        is Result.Loading -> {
            item { Text("idle") }
        }
        is Result.Error -> {
            item { Text(popularMovies.exception) }
        }
        is Result.Success -> {
            item {
                HorizontalScroll(
                    movies = popularMovies.data,
                    heading = "Up Coming Movies",
                    onMovieClick = { movie -> println(movie.title) })
            }
        }
    }
}

internal fun LazyListScope.nowPlayingMovies(
    popularMovies: Result<List<MoviesDomainModel>>,
    loadMovies: () -> Unit
) {
    when (popularMovies) {
        is Result.Loading -> {
            item { Text("idle") }
        }
        is Result.Error -> {
            item { Text(popularMovies.exception) }
        }
        is Result.Success -> {
            item {
                Text(
                    text = "Now Playing Movies",
                    style = MaterialTheme.typography.h3.copy(fontWeight = FontWeight.W700),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start)
            }
            items(popularMovies.data) { movie ->
                if (popularMovies.data.last() == movie) {
                    loadMovies()
                }
                MovieCard(movie = movie, onClick = {})
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun Header() {

    var query by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier.fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.PlayCircleFilled,
                        contentDescription = "TMDb Logo",
                        tint = SunnySideUp,
                        modifier = Modifier.size(48.dp))
                    Text(
                        text = "Composable",
                        style = MaterialTheme.typography.h1.copy(fontWeight = FontWeight.W500),
                        color = SunnySideUp)
                }
            Spacer(Modifier.height(32.dp))
            val keyboardController = LocalSoftwareKeyboardController.current
            OutlinedTextField(
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                value = query,
                shape = MaterialTheme.shapes.large,
                onValueChange = { search ->
                    if (search.contains("\n")) {
                        keyboardController?.hide()
                    } else query = search
                },
                placeholder = {
                    Text(
                        "Search for movies, TV shows, people...",
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.body2)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "search-icon",
                        tint = MaterialTheme.colors.onSurface)
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = "clear-icon",
                            tint = MaterialTheme.colors.onSurface,
                            modifier =
                                Modifier.clickable {
                                    keyboardController?.hide()
                                    query = ""
                                })
                    }
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }))
        }
}

@Composable
internal fun MovieCard(movie: MoviesDomainModel, onClick: () -> Unit) {

    Card(
        modifier =
            Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() }.animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.width(130.dp).fillMaxHeight()) {
                    AsyncImage(
                        url = movie.posterPath,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Box {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.h6,
                            fontSize = 18.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = movie.releaseDate,
                            style = MaterialTheme.typography.caption,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                        Spacer(modifier = Modifier.height(16.dp))
                        RatingRow(movie = movie)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = movie.overview,
                            style = MaterialTheme.typography.body2,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
}

@Composable
internal fun RatingRow(movie: MoviesDomainModel) {

    val ratingColor = getRatingColor(movie.voteAverage)

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier =
                Modifier.size(28.dp).clip(CircleShape).background(ratingColor).padding(2.dp)) {
                Text(
                    text = movie.voteAverage.toString(),
                    style = MaterialTheme.typography.caption,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.Center))
            }
        Spacer(modifier = Modifier.width(8.dp))
        val animatedProgress = remember { Animatable(0f) }
        LaunchedEffect(animatedProgress) {
            animatedProgress.animateTo(
                movie.voteAverage / 10f, animationSpec = tween(durationMillis = 1000))
        }
        CircularProgressIndicator(
            progress = animatedProgress.value,
            color = ratingColor,
            strokeWidth = 4.dp,
            modifier = Modifier.size(24.dp))

        Spacer(modifier = Modifier.width(8.dp))
        ShimmerStar(
            isShimmering = true,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = movie.voteCount,
            style = MaterialTheme.typography.caption,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f))
    }
}

@Composable
internal fun getRatingColor(rating: Float): Color {
    val green = Color(0xFF00C853)
    val yellow = Color(0xFFFFD600)
    val red = Color(0xFFFF1744)

    return when (rating) {
        in 0.0..5.0 -> red
        in 5.0..7.0 -> yellow
        else -> green
    }
}

@Composable
internal fun MovieCardSmall(movie: MoviesDomainModel, onClick: () -> Unit) {
    Card(
        modifier =
            Modifier.height(200.dp).width(150.dp).clickable { onClick() }.animateContentSize(),
        shape = RoundedCornerShape(8.dp),
        elevation = 8.dp) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(url = movie.posterPath, modifier = Modifier.fillMaxSize())
            }
        }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
internal fun AutoScrollingHorizontalSlider(
    movies: List<MoviesDomainModel>,
    onMovieClick: (MoviesDomainModel) -> Unit
) {
    val pagerState = rememberPagerState(1)

    LaunchedEffect(key1 = pagerState) {
        while (true) {
            val nextPage = (pagerState.currentPage + 1) % movies.size
            tween<Float>(durationMillis = 1000, easing = LinearEasing)
            pagerState.animateScrollToPage(page = nextPage, pageOffset = 0f)
            delay(2000) // Adjust this value to control the time between auto-scrolls
        }
    }

    Box(
        modifier =
            Modifier.background(MaterialTheme.colors.background).fillMaxWidth().height(360.dp)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.align(Alignment.Center),
                count = movies.size) { page ->
                    AutoScrollingMovieCard(
                        movie = movies[page], onClick = { onMovieClick(movies[page]) })
                }
        }
}

@Composable
internal fun AutoScrollingMovieCard(movie: MoviesDomainModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(280.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
                Box {
                    AsyncImage(
                        url = movie.backdropPath,
                        modifier = Modifier.fillMaxWidth().height(280.dp),
                        contentScale = ContentScale.Crop)

                    Box(
                        modifier =
                            Modifier.fillMaxWidth()
                                .height(280.dp)
                                .background(
                                    brush =
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black),
                                            startY = 0.4f * 280.dp.value)),
                        contentAlignment = Alignment.BottomStart) {
                            Text(
                                text = movie.title,
                                style = MaterialTheme.typography.h3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(8.dp))
                        }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
}
