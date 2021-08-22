package ru.skillbranch.sbdelivery.data.network

import retrofit2.Response
import retrofit2.http.*
import ru.skillbranch.sbdelivery.data.network.req.ReviewReq
import ru.skillbranch.sbdelivery.data.network.res.*

interface RestService {

    @POST("auth/refresh")
    suspend fun refreshToken(@Body refreshToken: RefreshToken): Token

    @GET("dishes")
    @Headers("If-Modified-Since: Mon, 1 Jun 2020 08:00:00 GMT")
    suspend fun getDishes(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<List<DishRes>>

    @GET("categories")
    @Headers("If-Modified-Since: Mon, 1 Jun 2020 08:00:00 GMT")
    suspend fun getCategories(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): List<CategoryRes>

    @GET("reviews/{dishId}")
    @Headers("If-Modified-Since: Mon, 1 Jun 2020 08:00:00 GMT")
    suspend fun getReviews(
        @Path("dishId") dishId: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<List<ReviewRes>>

    @GET("reviews/{dishId}")
    @Headers("If-Modified-Since: Mon, 1 Jun 2020 08:00:00 GMT")
    suspend fun getFullReviews(
        @Path("dishId") dishId: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<List<FullReviewRes>>

    @POST("reviews/{dishId}")
    @Headers(
        "If-Modified-Since: Mon, 1 Jun 2020 08:00:00 GMT"
//        , "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYxMDFkMDM2Mjk5YzZhMDAzZTlkZGI5ZiIsImlhdCI6MTYyNzU5MDA2OSwiZXhwIjoxNjI3NTkxMjY5fQ.u8GAAaZkah1G5I4y4U2oULcnfnurAW0f08X0-iiN1pw"
    )
    suspend fun sendReview(
        @Path("dishId") dishId: String,
        @Body review: ReviewReq,
    ): ReviewRes


}