package com.msukno.gameapprawg.ui.screens.genre_selection


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msukno.gameapprawg.AppViewModelProvider
import com.msukno.gameapprawg.model.Genre
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.msukno.gameapprawg.R
import com.msukno.gameapprawg.model.GenreImage
import com.msukno.gameapprawg.ui.navigation.NavigationDestination
import com.msukno.gameapprawg.ui.screens.game_list.DisplayImageFromStorage
import com.msukno.gameapprawg.ui.screens.game_list.DisplayImageFromWeb
import com.msukno.gameapprawg.ui.theme.GameAppRawgTheme
import org.jsoup.Jsoup

object GenreSelectionDestination: NavigationDestination{
    override val route: String = "GenreSelection"
    override val titleResource: Int = R.string.genre_selection
}

@Composable
fun GenreSelectionScreen(
    genreViewModel: GenreSelectionViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onGenreSelect: (Int, String) -> Unit = { _,_ -> }
){
    val uiState = genreViewModel.genreUiState.collectAsState()
    val imageCache = genreViewModel.imagePathsCache


    GenreSelectionBody(
        uiState.value.genres,
        imageCache,
        onGenreSelect = onGenreSelect
    )
}

@Composable
fun GenreSelectionBody(
    genres: List<Genre>,
    imageCache: Map<Int, GenreImage> = mapOf(),
    onGenreSelect: (Int, String) -> Unit = { _,_ -> }
){
    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_extra_small))){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_small))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(dimensionResource(id = R.dimen.line_thick))
                    .background(Color.Gray)
            )
            Text(
                text = "GENRE SELECTION",
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(4f)
            )
            Box(
                modifier = Modifier
                    .weight(3f)
                    .height(dimensionResource(id = R.dimen.line_thick))
                    .background(Color.Gray)
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_extra_small)))
        LazyColumn(
        ){
            items(genres.size){
                val genre = genres[it]
                GenreCard(
                    genre,
                    imageCache,
                    onGenreSelect = onGenreSelect
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
            }
        }
    }
}

@Composable
fun GenreCard(
    genre: Genre,
    imageCache: Map<Int, GenreImage> = mapOf(),
    onGenreSelect: (Int, String) -> Unit = { _,_ -> }
) {
    var showBackground by remember { mutableStateOf(false) }
    val path = imageCache[genre.id]?.imagePath
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column{
            Box(
                modifier = Modifier.fillMaxWidth()) {
                if (path != null) DisplayImageFromStorage(path = path)
                else DisplayImageFromWeb(url = genre.imageBackground)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.4f))
                        .height(dimensionResource(id = R.dimen.back_image_height))
                        .clickable { onGenreSelect(genre.id, genre.name) }
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(dimensionResource(id = R.dimen.padding_extra_small))
                ) {
                    Text(
                        text = genre.name,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                }
            }
        }
        Column(
            modifier = Modifier.background(Color.Black)
        ){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen.padding_small),
                        end = dimensionResource(id = R.dimen.padding_small)
                    )

            ){
                Box(
                    modifier = Modifier
                        .background(Color.DarkGray, shape = MaterialTheme.shapes.small)
                        .border(1.dp, Color.Green, shape = MaterialTheme.shapes.small)
                        .padding(start = 6.dp, end = 6.dp, bottom = 1.dp)
                ) {
                    Text(
                        text = genre.gamesCount.toString(),
                        color = Color.Green,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                IconButton(
                    onClick = { showBackground = !showBackground },
                ) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size))
                    )
                }
            }
            if(showBackground){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen.padding_small))
                ){
                    Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                    Text(
                        text = Jsoup.parse(genre.description).text(),
                        style = MaterialTheme.typography.bodyLarge
                    )

                }
            }
        }
    }
}


@Preview
@Composable
fun GenreCardPreview(){
    GameAppRawgTheme {
        Surface {
            GenreSelectionScreen()
        }
    }
}

val genreDummy = Genre(
    id = 0,
    name = "Massively Multyplayer",
    gamesCount = 5000,
    imageBackground = "https://media.rawg.io/media/games/960/960b601d9541cec776c5fa42a00bf6c4.jpg",
    description = "The game is played from a third-person perspective and its world is\n" +
            "        navigated on foot or by vehicle. The open world design lets the player freely roam San\n" +
            "        Andreas, consisting of three metropolitan cities: Los Santos, San Fierro, and Las Venturas,\n" +
            "        based on Los Angeles, San Francisco, and Las Vegas, respectively. The narrative is based on\n" +
            "        multiple real-life events in Los Angeles, including the Bloods and Crips street gang rivalry,\n" +
            "        1990s crack epidemic, 1992 Los Angeles riots, and the Rampart scandal. The game was released\n" +
            "        in October 2004 for the PlayStation 2, and in 2005 for Windows and the Xbox. Enhanced versions\n" +
            "        were released in the 2010s, followed by a remastered version in 2021."
)