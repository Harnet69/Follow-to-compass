package com.harnet.followtocompass.model

enum class Permissions(
    val permName: String,
    val permCode: Int,
    val rationaleTitle: String,
    val rationaleMessage: String
) {
    LOCATION(
        "location",
        111,
        "Access to a location",
        "If you want to use the app, you should give an access to your location"
    )
}