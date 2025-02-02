package com.smarttoolsdev.screenlightlamp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smarttoolsdev.screenlightlamp.data.Scene
import com.smarttoolsdev.screenlightlamp.data.ScenesViewModel
import com.smarttoolsdev.screenlightlamp.navigation.NavItem

@Composable
fun PresetsScreen(
    viewModel: ScenesViewModel,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentScene by viewModel.currentScene.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Scenes",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(viewModel.predefinedScenes) { scene ->
                SceneCard(
                    scene = scene,
                    isActive = scene.id == currentScene?.id,
                    onClick = {
                        if (scene.id == currentScene?.id) {
                            viewModel.stopScene()
                        } else {
                            viewModel.activateScene(scene)
                            onNavigateToHome()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SceneCard(
    scene: Scene,
    isActive: Boolean,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (scene.isAnimated) {
                        Brush.horizontalGradient(scene.colors)
                    } else {
                        Brush.horizontalGradient(listOf(scene.colors.first(), scene.colors.first()))
                    }
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    scene.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                if (isActive) {
                    Text(
                        "Active",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}