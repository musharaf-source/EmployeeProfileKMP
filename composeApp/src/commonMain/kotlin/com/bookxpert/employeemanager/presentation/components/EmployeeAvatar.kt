package com.bookxpert.employeemanager.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.bookxpert.employeemanager.presentation.theme.PrimaryBlue
import com.bookxpert.employeemanager.presentation.theme.PrimaryBlueDark

@Composable
fun EmployeeAvatar(
    fullName: String,
    imagePath: String?,
    size: Dp = 80.dp,
    showCameraOverlay: Boolean = false,
    onAvatarClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val initials = if (fullName.isBlank()) "EM" else {
        val parts = fullName.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
        if (parts.size == 1) parts[0].take(2).uppercase()
        else "${parts[0].first()}${parts[1].first()}".uppercase()
    }

    Box(
        modifier = modifier
            .size(size)
            .then(if (onAvatarClick != null) Modifier.clickable { onAvatarClick() } else Modifier),
        contentAlignment = Alignment.Center
    ) {
        if (!imagePath.isNullOrBlank()) {
            AsyncImage(
                model = imagePath,
                contentDescription = "Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(PrimaryBlue, PrimaryBlueDark)
                        )
                    )
                    .border(2.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.36f).sp
                )
            }
        }

        if (showCameraOverlay) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size((size.value * 0.38f).dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change photo",
                    tint = Color.White,
                    modifier = Modifier.size((size.value * 0.22f).dp)
                )
            }
        }
    }
}
