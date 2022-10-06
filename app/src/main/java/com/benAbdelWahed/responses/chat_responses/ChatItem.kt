package com.benAbdelWahed.responses.chat_responses

data class ChatItem(
        val message: Message,
        var key: String?,
        var sender: UserItem
) {
}