package org.umn.jesslyn0617week08_b.data.repository

import org.umn.jesslyn0617week08_b.data.models.Product
import org.umn.jesslyn0617week08_b.data.remote.ProductRemoteDataSource

class ProductRepository(
    private val remoteDataSource: ProductRemoteDataSource =
        ProductRemoteDataSource()
) {
    suspend fun getProducts(): List<Product> {
        return remoteDataSource.fetchProducts()
    }
}