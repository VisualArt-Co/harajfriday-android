package com.benAbdelWahed.responses.haraj_responses

import com.google.gson.annotations.SerializedName

data class Meta(

	@field:SerializedName("per_page")
	val perPage: Int? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("last_page")
	val lastPage: Int? = null,

	@field:SerializedName("hasMorePages")
	val hasMorePages: Boolean? = null,

	@field:SerializedName("current_page")
	val currentPage: Int? = null
)
