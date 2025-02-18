package com.unitip.mobile.features.job.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.icons.lucide.Bike
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.MapPinned
import com.composables.icons.lucide.RefreshCw
import com.unitip.mobile.features.job.commons.JobRoutes
import com.unitip.mobile.features.job.presentation.states.DetailJobState
import com.unitip.mobile.features.job.presentation.viewmodels.DetailJobViewModel
import com.unitip.mobile.shared.commons.compositional.LocalNavController
import com.unitip.mobile.shared.commons.extensions.isDriver
import com.unitip.mobile.shared.presentation.components.CustomIconButton

@Composable
fun DetailJobScreen(
    jobId: String,
    viewModel: DetailJobViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val context = LocalContext.current

    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.approveApplicationDetail) {
        with(uiState.approveApplicationDetail) {
            if (this is DetailJobState.ApproveApplicationDetail.Failure)
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            MaterialTheme.colorScheme.surfaceContainerLowest,
                        )
                    )
                )
                .padding(it)
        ) {
            // app bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomIconButton(
                    onClick = { navController.popBackStack() },
                    icon = Lucide.ChevronLeft
                )
                CustomIconButton(
                    onClick = { viewModel.fetchData() },
                    icon = Lucide.RefreshCw
                )
            }

            // loading state
            if (uiState.detail is DetailJobState.Detail.Loading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(strokeCap = StrokeCap.Round)
                }
            }

            // success state
            if (uiState.detail is DetailJobState.Detail.Success) {
                Log.d("DetailJobScreen", "DetailJobScreen: called")
                AnimatedVisibility(visible = listState.canScrollBackward) {
                    HorizontalDivider()
                }

                LazyColumn(modifier = Modifier.weight(1f), state = listState) {
                    /**
                     * component untuk menampilkan catatan pekerjaan
                     */
                    item {
                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                            Text(
                                text = uiState.jobModel.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = uiState.jobModel.note,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    /**
                     * component untuk menampilkan details job
                     * seperti jenis layanan, titik jemput, destinasi
                     */
                    itemsIndexed(
                        listOf(
                            mapOf(
                                "icon" to Lucide.Bike,
                                "title" to "Jenis Pekerjaan",
                                "value" to "Service tidak tersedia",
//                                "value" to JobConstant.services
//                                    .find { item -> item.value == uiState.jobModel.service }
//                                    .let { service ->
//                                        when (service != null) {
//                                            true -> service.title
//                                            else -> "Service tidak tersedia"
//                                        }
//                                    }
                            ),
                            mapOf(
                                "icon" to Lucide.MapPinned,
                                "title" to "Titik Jemput",
                                "value" to uiState.jobModel.pickupLocation
                            ),
                            mapOf(
                                "icon" to Lucide.MapPin,
                                "title" to "Destinasi",
                                "value" to uiState.jobModel.destinationLocation
                            ),
                        )
                    ) { index, item ->
                        Box(
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = if (index == 0) 16.dp else 4.dp
                                )
                                .clip(
                                    RoundedCornerShape(
                                        topStart = if (index == 0) 16.dp else 4.dp,
                                        topEnd = if (index == 0) 16.dp else 4.dp,
                                        bottomStart = if (index == 2) 16.dp else 4.dp,
                                        bottomEnd = if (index == 2) 16.dp else 4.dp
                                    )
                                )
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = .08f
                                    )
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.onSurface)
                                ) {
                                    Icon(
                                        item["icon"]!! as ImageVector,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(20.dp),
                                        tint = MaterialTheme.colorScheme.surface
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item["title"]!! as String,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = item["value"]!! as String,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    /**
                     * menampilkan daftar applications yang melamar job berikut dengan syarat
                     * yang membuat job adalah user saat ini dan user saat ini login sebagai
                     * role customer
                     */
                    item {
                        Text(text = "Daftar pelamar")
                        Text(text = "Silahkan pilih pelamar yang sesuai dengan kebutuhan anda")
                    }

                    with(uiState.getAllApplicationsDetail) {
                        when (this) {
                            is DetailJobState.GetAllApplicationsDetail.Loading -> item {
                                CircularProgressIndicator()
                            }

                            is DetailJobState.GetAllApplicationsDetail.Success ->
                                itemsIndexed(applications) { index, application ->
                                    ListItem(
                                        overlineContent = {
                                            Text(text = application.price.toString())
                                        },
                                        headlineContent = {
                                            Text(text = application.driver.name)
                                        },
                                        supportingContent = {
                                            Text(text = application.bidNote)
                                        },
                                        trailingContent = {
                                            with(uiState.approveApplicationDetail) {
                                                if (this is DetailJobState.ApproveApplicationDetail.Loading && applicationId == application.id) {
                                                    CircularProgressIndicator()
                                                } else {
                                                    OutlinedIconButton(
                                                        onClick = {
                                                            viewModel.approveApplication(
                                                                applicationId = application.id
                                                            )
                                                        }
                                                    ) {
                                                        Icon(
                                                            Lucide.Check,
                                                            contentDescription = null
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }

                            else -> Unit
                        }
                    }

                    /**
                     * spacer untuk memberikan jarak bawah pada lazy column
                     */
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                /**
                 * button untuk melamar pekerjaan, hanya dapat dilihat dan dilakukan
                 * oleh role driver
                 */
                if (uiState.session.isDriver()) {
                    Button(
                        onClick = {
                            navController.navigate(
                                JobRoutes.Apply(
                                    jobId = jobId
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp,
                                top = 16.dp
                            )
                    ) {
                        Text(text = "Lamar pekerjaan")
                    }
                }
            }
        }
    }
}