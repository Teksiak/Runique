@file:OptIn(ExperimentalFoundationApi::class)

package com.teksiak.core.presentation.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicSecureTextField
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.TextObfuscationMode
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teksiak.core.presentation.designsystem.CheckIcon
import com.teksiak.core.presentation.designsystem.EmailIcon
import com.teksiak.core.presentation.designsystem.EyeClosedIcon
import com.teksiak.core.presentation.designsystem.EyeOpenedIcon
import com.teksiak.core.presentation.designsystem.LockIcon
import com.teksiak.core.presentation.designsystem.R
import com.teksiak.core.presentation.designsystem.RuniqueTheme

@Composable
fun RuniquePasswordTextField(
    state: TextFieldState,
    isPasswordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String,
    title: String? = null,
) {
    var isFocused by remember { mutableStateOf(false) }

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if(isFocused) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(200, 0, LinearEasing),
        label = ""
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if(isFocused) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        },
        animationSpec = tween(200, 0, LinearEasing),
        label = ""
    )

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            if(title != null) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

        }
        Spacer(modifier = Modifier.height(4.dp))
        BasicSecureTextField(
            state = state,
            textObfuscationMode = if(isPasswordVisible) {
                TextObfuscationMode.Visible
            } else {
                TextObfuscationMode.RevealLastTyped
            },
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onBackground
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(animatedBackgroundColor)
                .border(
                    width = 1.dp,
                    color = animatedBorderColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(start = 12.dp, end = 6.dp)
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            decorator = { innerBox ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = LockIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .padding(end = 8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        if(state.text.isEmpty() && !isFocused) {
                            Text(
                                text = hint,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        innerBox()
                    }
                    IconButton(
                        onClick = onTogglePasswordVisibility,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = if(isPasswordVisible) EyeOpenedIcon else EyeClosedIcon,
                            contentDescription = if (isPasswordVisible) {
                                stringResource(id = R.string.hide_passoword)
                            } else {
                                stringResource(id = R.string.show_password)
                            },
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        )

    }

}

@Preview
@Composable
private fun RuniquePasswordTextFieldPreview() {
    RuniqueTheme {
        RuniquePasswordTextField(
            state = rememberTextFieldState(),
            isPasswordVisible = false,
            onTogglePasswordVisibility = {},
            hint = "password123",
            title = "Password",
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}