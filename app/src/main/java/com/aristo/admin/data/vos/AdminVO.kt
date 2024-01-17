package com.aristo.admin.data.vos

import java.io.Serializable

data class AdminVO (
    var companyName: String = "Empress",
    var address: String = "",
    var phone: String = "",
    var viber: String? = null,
    var fbPage: String? = null,
    var fbPageLink: String? = null,
) : Serializable