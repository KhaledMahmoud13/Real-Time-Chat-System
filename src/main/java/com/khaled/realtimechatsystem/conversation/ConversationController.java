package com.khaled.realtimechatsystem.conversation;

import com.khaled.realtimechatsystem.conversation.response.ConversationResponse;
import com.khaled.realtimechatsystem.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversations", description = "Conversations API")
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping("/direct")
    public ResponseEntity<ConversationResponse> getDirectConversation(
            @RequestParam("with") String otherUsername,
            Authentication authentication
    ) {
        String username = ((User) authentication.getPrincipal()).getUsername();
        UUID conversationId = conversationService.getOrCreateDirectConversation(username, otherUsername);
        return ResponseEntity.ok(new ConversationResponse(conversationId));
    }
}
