package com.fooddelivery.agentservice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fooddelivery.agentservice.client.MenuClient;
import com.fooddelivery.agentservice.client.OrderClient;
import com.fooddelivery.agentservice.client.RestaurantClient;
import com.fooddelivery.agentservice.entity.AiAgentResponse;
import com.fooddelivery.agentservice.entity.ConversationContext;
import com.fooddelivery.agentservice.entity.CreateOrderRequestDto;
import com.fooddelivery.agentservice.entity.CreatedOrderResponseDto;
import com.fooddelivery.agentservice.entity.MenuItemDto;
import com.fooddelivery.agentservice.entity.OrderItemRequestDto;
import com.fooddelivery.agentservice.entity.RestaurantDto;

@Service
public class AiAgentService {

    private final RestTemplate restTemplate;
    private final RestaurantClient restaurantClient;
    private final MenuClient menuClient;
    private final OrderClient orderClient;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.url}")
    private String openAiApiUrl;

    @Value("${openai.model}")
    private String model;

    private final Map<Long, ConversationContext> userContexts = new ConcurrentHashMap<>();

    public AiAgentService(RestTemplate restTemplate,
                          RestaurantClient restaurantClient,
                          MenuClient menuClient,
                          OrderClient orderClient) {
        this.restTemplate = restTemplate;
        this.restaurantClient = restaurantClient;
        this.menuClient = menuClient;
        this.orderClient = orderClient;
    }

    public AiAgentResponse chat(Long userId, String message) {
        try {
            String normalizedMessage = message == null ? "" : message.trim().toLowerCase();
            ConversationContext existingContext = userContexts.get(userId);

            // Step 9.3.3: place suggested order
            if (existingContext != null && isPlaceOrderReply(normalizedMessage)) {
                if (existingContext.getSuggestedRestaurant() != null && existingContext.getSuggestedMenuItem() != null) {
                    CreatedOrderResponseDto createdOrder = createOrderFromSuggestion(
                            userId,
                            existingContext.getSuggestedRestaurant(),
                            existingContext.getSuggestedMenuItem(),
                            existingContext.getQuantity()
                    );

                    existingContext.setAwaitingConfirmation(false);
                    userContexts.put(userId, existingContext);

                    return new AiAgentResponse(
                            "ORDER_CONFIRMED",
                            existingContext.getItems(),
                            existingContext.getPreferences(),
                            existingContext.getQuantity(),
                            "Order placed successfully. Order ID: #" + createdOrder.getId() +
                                    " for " + existingContext.getSuggestedMenuItem().getName() +
                                    " from " + existingContext.getSuggestedRestaurant().getName() + ".",
                            false,
                            existingContext.getSuggestedRestaurant(),
                            existingContext.getSuggestedMenuItem()
                    );
                }

                return new AiAgentResponse(
                        "ORDER_CONFIRMED",
                        List.of(),
                        List.of(),
                        1,
                        "I don’t have a current suggestion to place. Please ask me for a suggestion first.",
                        false,
                        null,
                        null
                );
            }

            // Follow-up confirmation flow
            if (existingContext != null && existingContext.isAwaitingConfirmation()) {
                if (isPositiveReply(normalizedMessage)) {
                    existingContext.setAwaitingConfirmation(false);

                    SuggestionResult suggestionResult = findSuggestionFromContext(existingContext);

                    if (suggestionResult != null) {
                        existingContext.setSuggestedRestaurant(suggestionResult.restaurant);
                        existingContext.setSuggestedMenuItem(suggestionResult.menuItem);
                        userContexts.put(userId, existingContext);

                        return new AiAgentResponse(
                                "FOLLOW_UP_CONFIRMATION",
                                existingContext.getItems(),
                                existingContext.getPreferences(),
                                existingContext.getQuantity(),
                                "I found " + suggestionResult.menuItem.getName() +
                                        " at " + suggestionResult.restaurant.getName() +
                                        " for $" + suggestionResult.menuItem.getPrice() +
                                        ". If you want, say 'place it' to order.",
                                false,
                                suggestionResult.restaurant,
                                suggestionResult.menuItem
                        );
                    }

                    userContexts.put(userId, existingContext);

                    return new AiAgentResponse(
                            "FOLLOW_UP_CONFIRMATION",
                            existingContext.getItems(),
                            existingContext.getPreferences(),
                            existingContext.getQuantity(),
                            "I couldn’t find a matching restaurant or dish right now.",
                            false,
                            null,
                            null
                    );
                }

                if (isNegativeReply(normalizedMessage)) {
                    existingContext.setAwaitingConfirmation(false);
                    userContexts.put(userId, existingContext);

                    return new AiAgentResponse(
                            "FOLLOW_UP_CANCELLED",
                            existingContext.getItems(),
                            existingContext.getPreferences(),
                            existingContext.getQuantity(),
                            "Okay, I won’t continue with that. Tell me what you’d like instead.",
                            false,
                            null,
                            null
                    );
                }
            }

            String prompt = buildPrompt(message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);

            JSONArray input = new JSONArray();

            JSONObject developerMessage = new JSONObject();
            developerMessage.put("role", "developer");
            developerMessage.put("content",
                    "You are an AI food assistant for a food delivery app. " +
                    "Return only valid JSON with exactly these keys: " +
                    "intent, items, preferences, quantity, reply, awaitingConfirmation. " +
                    "intent must be one of: KNOWLEDGE_QUERY, NUTRITION_QUERY, HEALTH_FOOD_QUERY, SUGGEST_FOOD, ORDER_FOOD. " +
                    "items must be an array of strings. " +
                    "preferences must be an array of strings. " +
                    "quantity must be a positive integer. " +
                    "reply must be a short helpful response for the user. " +
                    "awaitingConfirmation must be true only when the user is asking for suggestions/options and a follow-up like yes/no makes sense. " +
                    "For food explanation questions like 'how dosa is made', use KNOWLEDGE_QUERY. " +
                    "For calories/protein/healthy questions, use NUTRITION_QUERY. " +
                    "For mild health-food guidance questions like 'I have fever what should I eat', use HEALTH_FOOD_QUERY. " +
                    "For recommendation questions like 'suggest dosa nearby', use SUGGEST_FOOD. " +
                    "For direct order requests like 'order burger', use ORDER_FOOD. " +
                    "Do not include markdown, code fences, or extra text.");

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);

            input.put(developerMessage);
            input.put(userMessage);

            requestBody.put("input", input);

            HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
            String rawResponse = restTemplate.postForObject(openAiApiUrl, entity, String.class);

            AiAgentResponse response = parseOpenAiResponse(rawResponse);

            if (shouldStoreContext(response.getIntent())) {
                ConversationContext newContext = new ConversationContext(
                        response.getIntent(),
                        response.getItems(),
                        response.getPreferences(),
                        response.getQuantity(),
                        response.isAwaitingConfirmation()
                );
                userContexts.put(userId, newContext);
            } else {
                userContexts.remove(userId);
            }

            return response;

        } catch (Exception e) {
            e.printStackTrace();

            return new AiAgentResponse(
                    "UNAVAILABLE",
                    List.of(),
                    List.of(),
                    1,
                    "AI Assistant is temporarily unavailable. Please try again.",
                    false,
                    null,
                    null
            );
        }
    }

    private CreatedOrderResponseDto createOrderFromSuggestion(Long userId,
                                                              RestaurantDto restaurant,
                                                              MenuItemDto menuItem,
                                                              int quantity) {
        CreateOrderRequestDto orderRequest = new CreateOrderRequestDto(
                userId,
                restaurant.getId(),
                List.of(new OrderItemRequestDto(menuItem.getId(), quantity > 0 ? quantity : 1))
        );

        return orderClient.createOrder(orderRequest);
    }

    private SuggestionResult findSuggestionFromContext(ConversationContext context) {
        if (context.getItems() == null || context.getItems().isEmpty()) {
            return null;
        }

        List<RestaurantDto> restaurants = restaurantClient.getAllRestaurants();
        String requestedItem = normalize(context.getItems().get(0));

        for (RestaurantDto restaurant : restaurants) {
            try {
                List<MenuItemDto> menuItems = menuClient.getMenuByRestaurantId(restaurant.getId());

                if (menuItems == null || menuItems.isEmpty()) {
                    continue;
                }

                for (MenuItemDto menuItem : menuItems) {
                    if (menuItem.getName() == null) {
                        continue;
                    }

                    String menuName = normalize(menuItem.getName());

                    if (menuName.contains(requestedItem) || requestedItem.contains(menuName)) {
                        return new SuggestionResult(restaurant, menuItem);
                    }
                }

            } catch (Exception e) {
                // skip if one restaurant menu fails
            }
        }

        return null;
    }

    private String buildPrompt(String message) {
        return "Analyze this user message for a food delivery assistant: " + message;
    }

    private AiAgentResponse parseOpenAiResponse(String rawResponse) {
        JSONObject responseJson = new JSONObject(rawResponse);
        String outputText = extractOutputText(responseJson);
        JSONObject parsed = new JSONObject(outputText);

        String intent = parsed.optString("intent", "UNKNOWN");
        List<String> items = jsonArrayToList(parsed.optJSONArray("items"));
        List<String> preferences = jsonArrayToList(parsed.optJSONArray("preferences"));
        int quantity = parsed.optInt("quantity", 1);
        String reply = parsed.optString("reply", "I understood your request.");
        boolean awaitingConfirmation = parsed.optBoolean("awaitingConfirmation", false);

        return new AiAgentResponse(
                intent,
                items,
                preferences,
                quantity,
                reply,
                awaitingConfirmation,
                null,
                null
        );
    }

    private String extractOutputText(JSONObject responseJson) {
        JSONArray output = responseJson.optJSONArray("output");
        if (output == null || output.length() == 0) {
            throw new RuntimeException("No output returned from OpenAI");
        }

        for (int i = 0; i < output.length(); i++) {
            JSONObject outputItem = output.getJSONObject(i);
            JSONArray content = outputItem.optJSONArray("content");
            if (content == null) {
                continue;
            }

            for (int j = 0; j < content.length(); j++) {
                JSONObject contentItem = content.getJSONObject(j);
                if ("output_text".equals(contentItem.optString("type"))) {
                    return contentItem.getString("text");
                }
            }
        }

        throw new RuntimeException("No output_text found in OpenAI response");
    }

    private List<String> jsonArrayToList(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        if (jsonArray == null) {
            return list;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }

        return list;
    }

    private boolean shouldStoreContext(String intent) {
        return "SUGGEST_FOOD".equalsIgnoreCase(intent)
                || "ORDER_FOOD".equalsIgnoreCase(intent);
    }

    private boolean isPositiveReply(String message) {
        return message.equals("yes")
                || message.equals("yeah")
                || message.equals("yep")
                || message.equals("ok")
                || message.equals("okay")
                || message.equals("sure")
                || message.equals("go ahead");
    }

    private boolean isNegativeReply(String message) {
        return message.equals("no")
                || message.equals("nope")
                || message.equals("cancel")
                || message.equals("not now");
    }

    private boolean isPlaceOrderReply(String message) {
        return message.equals("place it")
                || message.equals("place order")
                || message.equals("order this")
                || message.equals("yes place order")
                || message.equals("confirm order");
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static class SuggestionResult {
        private final RestaurantDto restaurant;
        private final MenuItemDto menuItem;

        private SuggestionResult(RestaurantDto restaurant, MenuItemDto menuItem) {
            this.restaurant = restaurant;
            this.menuItem = menuItem;
        }
    }
}