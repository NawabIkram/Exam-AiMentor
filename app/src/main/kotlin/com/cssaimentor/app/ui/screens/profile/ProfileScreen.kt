package com.cssaimentor.app.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cssaimentor.app.ui.components.GlassCard
import com.cssaimentor.app.ui.components.MentorOutlinedButton
import com.cssaimentor.app.ui.components.MetricPill
import com.cssaimentor.app.ui.components.SectionHeader
import com.cssaimentor.app.ui.theme.MentorAmber
import com.cssaimentor.app.ui.theme.MentorCyan
import com.cssaimentor.app.ui.theme.MentorGreen
import com.cssaimentor.app.ui.theme.MentorPurple
import com.cssaimentor.app.ui.theme.MentorTextMuted
import com.cssaimentor.app.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        SectionHeader("Profile", "Your preparation snapshot")
        GlassCard(accent = MentorCyan) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Person, contentDescription = null, tint = MentorCyan, modifier = Modifier.size(62.dp))
                Column {
                    Text(user?.name ?: "CSS Aspirant", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(user?.email ?: "demo@cssaimentor.app", color = MentorTextMuted)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricPill("Streak", "${user?.streak ?: 7} days", Modifier.weight(1f), MentorCyan)
            MetricPill("Quizzes", "${user?.completedQuizzes ?: 3}", Modifier.weight(1f), MentorGreen)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MetricPill("Progress", "${(((user?.progress ?: 0.42f) * 100).toInt())}%", Modifier.weight(1f), MentorPurple)
            MetricPill("Study", "${user?.totalStudyMinutes ?: 480} min", Modifier.weight(1f), MentorAmber)
        }
        GlassCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Settings, contentDescription = null, tint = MentorTextMuted)
                    Column {
                        Text("Settings")
                        Text("Notifications, study goals, and theme controls", color = MentorTextMuted)
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        MentorOutlinedButton(
            text = "Logout",
            onClick = { viewModel.logout(onLoggedOut) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Rounded.Logout, contentDescription = null) }
        )
        Spacer(Modifier.height(18.dp))
    }
}
