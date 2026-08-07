package com.example.civicly.ui.match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun MatchQuizScreen(vm: MatchViewModel, onFinished: () -> Unit) {
    val index by vm.index.collectAsState()
    val answers by vm.answers.collectAsState()
    val question = vm.questions[index]
    val selected = answers[question.issueKey]
    val isLast = index == vm.questions.lastIndex
    val progress = (index + 1) / vm.questions.size.toFloat()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Question ${index + 1} of ${vm.questions.size}",
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(Modifier.height(24.dp))
        Text(question.prompt, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        question.options.forEach { opt ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selected == opt.value,
                        onClick = { vm.answer(question.issueKey, opt.value) },
                        role = Role.RadioButton,
                    )
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = selected == opt.value, onClick = null)
                Spacer(Modifier.width(12.dp))
                Text(opt.label, style = MaterialTheme.typography.bodyLarge)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            TextButton(onClick = { vm.back() }, enabled = index > 0) { Text("Back") }
            if (isLast) {
                Button(enabled = selected != null, onClick = onFinished) { Text("See my matches") }
            } else {
                Button(enabled = selected != null, onClick = { vm.next() }) { Text("Next") }
            }
        }
    }
}
