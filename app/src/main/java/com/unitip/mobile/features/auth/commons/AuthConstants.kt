package com.unitip.mobile.features.auth.commons

import com.composables.icons.lucide.Bike
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.User
import com.unitip.mobile.features.auth.domain.models.Role

object AuthConstants {
    val roles = listOf(
        Role(
            icon = Lucide.Bike,
            title = "Driver",
            subtitle = "Ambil dan tawarkan pekerjaan kepada pengguna lainnya",
            role = "driver"
        ),
        Role(
            icon = Lucide.User,
            title = "Customer",
            subtitle = "Buat pesanan dan ikut penawaran dari pengguna lainnya",
            role = "customer"
        ),
    )
}