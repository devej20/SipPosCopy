package net.sipconsult.jubensippos.data.network

import kotlinx.coroutines.Deferred
import net.sipconsult.jubensippos.data.models.SaleTransactionPostBody
import net.sipconsult.jubensippos.data.models.SignInBody
import net.sipconsult.jubensippos.data.network.response.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


interface SipShopApiService {
    @GET("categories")
    fun getProductCategoriesAsync(): Deferred<ProductCategories>

    @Headers("Content-Type: application/json")
    @GET("api/products")
    suspend fun getProductsAsync(): Products

    @Headers("Content-Type: application/json")
    @GET("api/locations")
    suspend fun getLocationsAsync(): Locations

    @Headers("Content-Type: application/json")
    @GET("api/paymentMethods")
    suspend fun getPaymentMethodsAsync(): PaymentMethods

    @Headers("Content-Type: application/json")
    @GET("api/Vouchers/{code}")
    suspend fun getVoucherAsync(@Path("code") code: String): VoucherResponse

    @POST("api/Account/authenticate")
    suspend fun authenticateAsync(@Body signInBody: SignInBody): LoginResponse

    @Headers("Content-Type: application/json")
    @GET("api/clients")
    suspend fun getClientsAsync(): Clients

    @Headers("Content-Type: application/json")
    @GET("api/DiscountTypes")
    suspend fun getDiscountTypesAsync(): DiscountTypes

    @Headers("Content-Type: application/json")
    @POST("api/SalesTransactions")
    suspend fun postTransactionAsync(@Body body: SaleTransactionPostBody): TransactionResponse

    @Headers("Content-Type: application/json")
    @GET("api/stockTransactions/operator/{operatorId}")
    suspend fun getTransactionAsync(@Path("operatorId") operatorId: String): SalesTransactions

    @GET("api/SalesAgent")
    suspend fun getSalesAgentsAsync(): SalesAgents

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): SipShopApiService {
            val requestInterceptor = Interceptor { chain ->
                val url = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("key", "API_KEY")
                    .build()
                val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(connectivityInterceptor)

                .build()

/*
            val gson = GsonBuilder()
                .setLenient()
                .create()
*/

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://41.66.218.205/sipshop/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SipShopApiService::class.java)
        }
    }
}