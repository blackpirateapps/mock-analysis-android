package com.mockanalysis.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mockanalysis.presentation.theme.ManropeFontFamily
import com.mockanalysis.presentation.theme.MockAnalysisColors

/**
 * Glass-morphism style top app bar matching the design system.
 * Features user avatar, app title, and notification button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockAnalysisTopBar(
    avatarUrl: String?,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = 0.8f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Avatar + App Name
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MockAnalysisColors.PrimaryFixed),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUrl != null) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "User profile photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = "U",
                            style = MaterialTheme.typography.labelMedium,
                            color = MockAnalysisColors.OnPrimaryFixed
                        )
                    }
                }
                
                // App Name
                Text(
                    text = "The Cognitive Architect",
                    fontFamily = ManropeFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = MockAnalysisColors.Primary
                )
            }
            
            // Right side: Notification button
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = MockAnalysisColors.Primary
                )
            }
        }
    }
}
