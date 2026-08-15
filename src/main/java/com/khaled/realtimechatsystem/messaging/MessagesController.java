package com.khaled.realtimechatsystem.messaging;

import com.khaled.realtimechatsystem.messaging.response.ConversationHistoryPage;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Messages API")
public class MessagesController {
    private final MessageService messageService;

    @GetMapping("/{conversationId}/messages")
    public ConversationHistoryPage getHistory(
            @PathVariable UUID conversationId,
            @RequestParam(required = false) UUID before,
            @Max(100) @RequestParam(defaultValue = "50") int limit) {

        return before == null
                ? messageService.getConversationHistory(conversationId, limit)
                : messageService.getConversationHistoryBefore(conversationId, before, limit);
    }
}
