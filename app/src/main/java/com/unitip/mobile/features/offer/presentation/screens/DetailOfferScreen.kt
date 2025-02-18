package com.unitip.mobile.features.offer.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.CircleUser
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.MapPinned
import com.composables.icons.lucide.Menu
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.RefreshCw
import com.composables.icons.lucide.Wallet
import com.unitip.mobile.features.chat.commons.ChatRoutes
import com.unitip.mobile.features.offer.commons.OfferRoutes
import com.unitip.mobile.features.offer.domain.models.Applicant
import com.unitip.mobile.features.offer.presentation.components.ErrorState
import com.unitip.mobile.features.offer.presentation.states.DetailOfferState
import com.unitip.mobile.features.offer.presentation.viewmodels.DetailOfferViewModel
import com.unitip.mobile.shared.commons.compositional.LocalNavController
import com.unitip.mobile.shared.commons.extensions.GetPopResult
import com.unitip.mobile.shared.commons.extensions.isCustomer
import com.unitip.mobile.shared.commons.extensions.isDriver
import com.unitip.mobile.shared.presentation.components.CustomIconButton

@Composable
fun DetailOfferScreen(
    offerId: String,
    viewModel: DetailOfferViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current
    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsState()
    val offer by viewModel.offer.collectAsState()
    var showToastForbidden by remember { mutableStateOf(false) }

    if (showToastForbidden) {
        Toast.makeText(
            LocalContext.current,
            "Anda tidak memiliki akses untuk melihat detail pelamar ini",
            Toast.LENGTH_SHORT
        ).show()
        showToastForbidden = false
    }
    // ketika user berhasil mengajukan penawaran, maka fetch data kembali
    navController.GetPopResult<Boolean>("applyOfferSuccess") { applyOffer ->
        if (applyOffer == true) {
            viewModel.fetchData()
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
            // App Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CustomIconButton(
                    onClick = { navController.popBackStack() },
                    icon = Lucide.ChevronLeft
                )
                Spacer(modifier = Modifier.weight(1f))
                CustomIconButton(
                    onClick = { viewModel.fetchData() },
                    icon = Lucide.RefreshCw
                )
                CustomIconButton(
                    onClick = { /* Handle menu */ },
                    icon = Lucide.Menu
                )
            }

            when (val detailState = uiState.detail) {
                is DetailOfferState.Detail.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(strokeCap = StrokeCap.Round)
                    }
                }

                is DetailOfferState.Detail.Failure -> {
                    ErrorState(message = detailState.message)
                }

                is DetailOfferState.Detail.Success -> {
                    LazyColumn(modifier = Modifier.weight(1f), state = listState) {
                        item {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = offer.title,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = offer.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }

                        // Offer Details
                        itemsIndexed(
                            listOf(
                                mapOf(
                                    "icon" to Lucide.Wallet,
                                    "title" to "Harga",
                                    "value" to "Rp${offer.price}"
                                ),
                                mapOf(
                                    "icon" to Lucide.Clock,
                                    "title" to "Tersedia Hingga",
                                    "value" to offer.availableUntil
                                ),
                                mapOf(
                                    "icon" to Lucide.MapPin,
                                    "title" to when (offer.type) {
                                        "jasa-titip" -> "Outlet Jastip"
                                        else -> "Area Penjemputan"
                                    },
                                    "value" to offer.pickupArea
                                ),
                                mapOf(
                                    "icon" to Lucide.MapPinned,
                                    "title" to "Area Pengantaran",
                                    "value" to offer.destinationArea
                                ),
                            )
                        ) { _, item ->
                            DetailItemOffer(item)
                        }

                        item {
                            if (uiState.session.isCustomer()) {
                                offer.freelancer?.id?.let { driverId ->
                                    ItemChat(
                                        driverId = driverId,
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }

                        // List of Applicants
                        item {
                            if (uiState.session.isDriver() && offer.freelancer.id == (uiState.session?.id
                                    ?: "") ||
                                uiState.session.isCustomer() && offer.applicants.any {
                                    it.customerId == (uiState.session?.id ?: "")
                                }
                            ) {

                                Text(
                                    text = "Daftar Pelamar (${offer.applicantsCount})",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )

                                // applicant  list
                                offer.applicants.forEach { applicant ->
                                    ApplicantItem(
                                        applicant = applicant,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        onItemClick = {
                                            if (uiState.session.isDriver() && offer.freelancer.id == (uiState.session?.id
                                                    ?: "")
                                            ) {
                                                // Driver yang membuat offer bisa akses semua detail
                                                navController.navigate(
                                                    OfferRoutes.DetailApplicant(
                                                        offerId = offerId,
                                                        applicantId = applicant.id
                                                    )
                                                )
                                            } else if (uiState.session.isCustomer()) {
                                                // Customer hanya bisa akses detail miliknya
                                                if (applicant.customerId == (uiState.session?.id
                                                        ?: "")
                                                ) {
                                                    navController.navigate(
                                                        OfferRoutes.DetailApplicant(
                                                            offerId = offerId,
                                                            applicantId = applicant.id
                                                        )
                                                    )
                                                } else {
                                                    // Tampilkan toast jika mencoba akses detail applicant lain
                                                    showToastForbidden = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (uiState.session.isCustomer()) {
                        when {
                            offer.hasApplied -> {
                                Text(
                                    text = "Anda sudah mengajukan penawaran",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            offer.applicantsCount >= offer.maxParticipants -> {
                                Text(
                                    text = "Kuota penawaran sudah penuh",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }

                            else -> {
                                Button(
                                    onClick = {
                                        if (offer.type == "jasa-titip") {
                                            navController.navigate(
                                                OfferRoutes.ApplyOffer(
                                                    offerId = offerId,
                                                    offerType = offer.type,
                                                    offerPickupLocation = offer.pickupArea
                                                )
                                            )
                                        } else {
                                            navController.navigate(
                                                OfferRoutes.ApplyOffer(
                                                    offerId = offerId,
                                                    offerType = offer.type
                                                )
                                            )
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(text = "Ajukan Penawaran")
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun ItemChat(
    driverId: String,
    viewModel: DetailOfferViewModel
) {

    val navController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToChat) {
        uiState.navigateToChat?.let { (roomId, otherUserId, otherUserName) ->
            navController.navigate(
                ChatRoutes.Conversation(
                    roomId = roomId,
                    otherUserId = otherUserId,
                    otherUserName = otherUserName
                )
            ) {
                launchSingleTop = true
            }
            // Reset state setelah navigasi
            viewModel.resetNavigateToChat()
        }
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
            .clickable { viewModel.createChat(driverId) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Lucide.CircleUser,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(20.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Chat",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "Chat dengan Driver",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Icon(
                imageVector = Lucide.MessageCircle,
                contentDescription = "Chat",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DetailItemOffer(item: Map<String, Any>) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    item["icon"] as ImageVector,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item["title"] as String,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = item["value"] as String,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ApplicantItem(
    applicant: Applicant,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onItemClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = applicant.customerName,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = when (applicant.status) {
                        "pending" -> "Pending"
                        "accepted" -> "Diterima"
                        "on_the_way" -> "On The Way"
                        "rejected" -> "Ditolak"
                        "done" -> "Selesai"
                        else -> applicant.status
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = when (applicant.status) {
                        "pending" -> MaterialTheme.colorScheme.primary
                        "accepted" -> MaterialTheme.colorScheme.tertiary
                        "rejected" -> MaterialTheme.colorScheme.error
                        "done" -> MaterialTheme.colorScheme.primary
                        "on_the_way" -> MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            Text(
                text = "Lokasi Jemput: ${applicant.pickupLocation}",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Lokasi Antar: ${applicant.destinationLocation}",
                style = MaterialTheme.typography.bodySmall
            )
            if (applicant.note.isNotEmpty()) {
                Text(
                    text = "Catatan: ${applicant.note}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "Harga Final: Rp${applicant.finalPrice}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}