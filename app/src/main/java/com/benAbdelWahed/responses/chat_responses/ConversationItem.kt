package com.benAbdelWahed.responses.chat_responses

data class ConversationItem(
        var messages: HashMap<String, Message>
) {
}