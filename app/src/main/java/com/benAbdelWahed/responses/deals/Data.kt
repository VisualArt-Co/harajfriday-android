package com.benAbdelWahed.responses.deals

import com.benAbdelWahed.responses.Meta

data class Data(
        val `data`: List<Deal>,
        val meta: Meta
)