package com.ljdit.digitalpublishing.data.repository

import com.ljdit.digitalpublishing.data.api.RetrofitClient

class DistributorRepository {

    suspend fun getDistributors() =
        RetrofitClient.photoApi.getDistributors()

}