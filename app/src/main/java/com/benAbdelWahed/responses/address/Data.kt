package com.benAbdelWahed.responses.address

import com.benAbdelWahed.responses.Meta

data class Data(
        val `data`: List<Address>,
        val meta: Meta
)