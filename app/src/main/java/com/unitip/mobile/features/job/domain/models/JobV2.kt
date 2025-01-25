package com.unitip.mobile.features.job.domain.models

import com.unitip.mobile.features.job.commons.JobConstants

sealed class JobV2 {
    data class ListItem(
        val id: String,
        val type: JobConstants.Type,
        val title: String,
        val note: String,
        val service: String,
        val pickupLocation: String,
        val destination: String,
        val createdAt: String,
        val updatedAt: String,
        val totalApplications: Int,
        val customer: Customer
    ) {
        data class Customer(
            val name: String
        )
    }

    data class Detail(
        val id: String = "",
        val title: String = "",
        val destination: String = "",
        val note: String = "",
        val service: String = "",
        val pickupLocation: String = "",
        val createdAt: String = "",
        val updatedAt: String = "",
        val customerId: String = "",
        val applications: List<Application> = emptyList(),
    ) {
        data class Application(
            val id: String = "",
            val freelancerName: String = "",
            val price: Int = 0
        )
    }
}