package org.umn.jesslyn0617week08_b.data.remote

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders
import org.umn.jesslyn0617week08_b.data.models.Product
import org.umn.jesslyn0617week08_b.data.remote.APIConfig.API_KEY

class ProductRemoteDataSource {
    suspend fun fetchProducts(): List<Product> {
        return try {
            val response = HttpClientProvider.client.get(
                "${APIConfig.BASE_URL}/rest/v1/tbl_products"
                //trigger error 404: "${APIConfig.BASE_URL}/rest/v1/table_that_does_not_exist" 
            ) {
                header("apikey", API_KEY)
                header(HttpHeaders.Accept,"application/json")
                // Sengaja menggunakan nama kolom yang tidak ada, trigger error 400
                // parameter("select", "invalid_column_xyz")
                parameter(
                    "select",
                    "product_id, category_id, product_name, price, stock, image, created_at"
                )
                parameter("order", "product_name.asc")
            }
            response.body<List<Product>>()
        } catch (e: Exception) {
            throw e
        }
    }
}