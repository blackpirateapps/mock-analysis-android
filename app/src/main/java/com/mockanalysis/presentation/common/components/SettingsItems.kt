package com.mockanalysis.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mockanalysis.presentation.theme.MockAnalysisColors
import com.mockanalysis.presentation.theme.MockAnalysisCorners

/**
 * Toggle setting item matching the design system.
 */
@Composable
fun ToggleSettingItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(end = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MockAnalysisColors.OnSurfaceVariant
            )
        }
        
        // Custom toggle switch
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(50))
                .background(
                    if (isEnabled) MockAnalysisColors.Primary
                    else MockAnalysisColors.OutlineVariant
                )
                .clickable { onToggle(!isEnabled) }
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .align(if (isEnabled) Alignment.CenterEnd else Alignment.CenterStart)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

/**
 * Navigation setting item with chevron.
 */
@Composable
fun NavigationSettingItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MockAnalysisColors.SurfaceContainerHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MockAnalysisColors.OnSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = "Navigate",
            tint = MockAnalysisColors.Outline,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Platform account item for linked services.
 */
@Composable
fun PlatformAccountItem(
    name: String,
    shortCode: String,
    isConnected: Boolean,
    backgroundColor: Color,
    textColor: Color,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MockAnalysisCorners.Large)
            .background(MockAnalysisColors.SurfaceContainerLow)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Platform icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = shortCode,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            
            Column {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isConnected) "CONNECTED" else "NOT LINKED",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isConnected) MockAnalysisColors.Secondary else MockAnalysisColors.Outline,
                    letterSpacing = 0.5.sp
                )
            }
        }
        
        if (isConnected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Connected",
                tint = MockAnalysisColors.Secondary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            TextButton(onClick = onAction) {
                Text(
                    text = "LINK",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MockAnalysisColors.Primary,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

/**
 * Achievement badge component.
 */
@Composable
fun AchievementBadge(
    name: String,
    icon: ImageVector,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(MockAnalysisColors.Primary, MockAnalysisColors.PrimaryContainer)
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (isUnlocked) {
                        Brush.linearGradient(gradientColors)
                    } else {
                        Brush.linearGradient(
                            listOf(
                                MockAnalysisColors.SurfaceContainerHighest,
                                MockAnalysisColors.SurfaceContainerHighest
                            )
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = if (isUnlocked) Color.White else MockAnalysisColors.OutlineVariant,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MockAnalysisColors.Outline
            )
            if (!isUnlocked) {
                Text(
                    text = "(Locked)",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = MockAnalysisColors.Outline.copy(alpha = 0.6f)
                )
            }
        }
    }
}
