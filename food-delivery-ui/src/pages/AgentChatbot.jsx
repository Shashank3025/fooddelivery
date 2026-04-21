import React, { useEffect, useRef, useState } from "react";
import ReactDOM from "react-dom";
import axios from "axios";
import "./AgentChatbot.css";

const AGENTS = {
  rules: {
    id: "rules",
    label: "Order Agent",
    icon: "🛵",
    placeholder: 'e.g. "Order Burger"',
    welcome:
      "Hey there! I'm your Order Agent.\nTell me what you'd like and I'll place the order right away.\n\nTry: \"Order Burger\" or \"Order Chicken Biryani\"",
  },
  ai: {
    id: "ai",
    label: "AI Assistant",
    icon: "✨",
    placeholder: 'e.g. "What cuisines are available?"',
    welcome:
      "Hi! I'm your AI Food Assistant.\nI can help you explore menus, suggest dishes, or help you decide what to order.\n\nWhat are you in the mood for today?",
  },
};

const OrderCard = ({ data }) => (
  <div className="acb-order-card">
    <div className="acb-order-success">Order Placed Successfully</div>

    <div className="acb-order-rows">
      {[
        ["Restaurant", data.restaurant],
        ["Item", data.item],
        ["Price", data.price],
        ["Order ID", data.orderId],
      ].map(([label, value]) => (
        <React.Fragment key={label}>
          <div className="acb-order-row">
            <span className="acb-order-label">{label}</span>
            <span className="acb-order-value">{value}</span>
          </div>
          <div className="acb-order-divider" />
        </React.Fragment>
      ))}

      <div className="acb-order-row">
        <span className="acb-order-label">Status</span>
        <span className="acb-order-badge">{data.status}</span>
      </div>
    </div>
  </div>
);

const AgentChatbot = () => {
  const [isOpen, setIsOpen] = useState(true);
  const [activeAgent, setActiveAgent] = useState("rules");
  const [input, setInput] = useState("");
  const [chatHistory, setChatHistory] = useState({
    rules: [
      {
        sender: "bot",
        payload: { type: "text", text: AGENTS.rules.welcome },
      },
    ],
    ai: [
      {
        sender: "bot",
        payload: { type: "text", text: AGENTS.ai.welcome },
      },
    ],
  });
  const [loading, setLoading] = useState(false);

  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);
  const isMounted = useRef(false);

  const agent = AGENTS[activeAgent];
  const messages = chatHistory[activeAgent];

  useEffect(() => {
    if (!isMounted.current) {
      isMounted.current = true;
      return;
    }

    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [chatHistory, loading]);

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => inputRef.current?.focus(), 150);
    }
  }, [isOpen, activeAgent]);

  const switchAgent = (id) => {
    setActiveAgent(id);
    setInput("");
  };

  const addMessage = (agentId, sender, payload) => {
    setChatHistory((prev) => ({
      ...prev,
      [agentId]: [...prev[agentId], { sender, payload }],
    }));
  };

  const sendMessage = async () => {
    if (!input.trim() || loading) return;

    const userMessage = input.trim();
    const userId = Number(localStorage.getItem("userId") || 19);
    const currentAgent = activeAgent;

    setChatHistory((prev) => ({
      ...prev,
      [currentAgent]: [
        ...prev[currentAgent],
        { sender: "user", payload: { type: "text", text: userMessage } },
      ],
    }));

    setInput("");
    setLoading(true);

    try {
      if (currentAgent === "rules") {
        const { data } = await axios.post(
          "http://localhost:8080/api/agent/place-order",
          {
            userId,
            message: userMessage,
          }
        );

        if (data.status === "SUCCESS" && data.createdOrder) {
          addMessage(currentAgent, "bot", {
            type: "order",
            order: {
              restaurant: data.matchedRestaurant?.name || "N/A",
              item: data.matchedMenuItem?.name || "N/A",
              price: `$${data.matchedMenuItem?.price ?? "N/A"}`,
              orderId: `#${data.createdOrder?.id}`,
              status: data.createdOrder?.status,
            },
          });
        } else if (data.status === "SUCCESS" && data.matchedMenuItem) {
          addMessage(currentAgent, "bot", {
            type: "order",
            order: {
              restaurant: data.matchedRestaurant?.name || "N/A",
              item: data.matchedMenuItem?.name || "N/A",
              price: `$${data.matchedMenuItem?.price ?? "N/A"}`,
              orderId: "—",
              status: "MATCHED",
            },
          });
        } else {
          addMessage(currentAgent, "bot", {
            type: "text",
            text:
              data.message ||
              "Sorry, I couldn’t find a matching item. Please try something else.",
          });
        }
      } else {
        const { data } = await axios.post(
          "http://localhost:8080/api/ai-agent/chat",
          {
            userId,
            message: userMessage,
          }
        );

        addMessage(currentAgent, "bot", {
          type: "text",
          text: data.reply || data.message || "I couldn't understand that.",
        });
      }
    } catch (error) {
      console.error("Agent error:", error);

      addMessage(currentAgent, "bot", {
        type: "text",
        text: "Failed to connect to the agent service. Please try again.",
      });
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      sendMessage();
    }
  };

  const renderMessage = (msg, i) => {
    const isUser = msg.sender === "user";
    const payload = msg.payload || { type: "text", text: "" };

    return (
      <div key={i} className={`acb-msg acb-msg--${msg.sender}`}>
        {!isUser && (
          <div className={`acb-avatar acb-avatar--${activeAgent}`}>
            {agent.icon}
          </div>
        )}

        {payload.type === "order" ? (
          <OrderCard data={payload.order} />
        ) : (
          <div className="acb-bubble">
            {payload.text.split("\n").map((line, j) => (
              <div key={j}>{line || <br />}</div>
            ))}
          </div>
        )}
      </div>
    );
  };

  const widget = (
    <div className="acb-root">
      <button
        className={`acb-toggle acb-toggle--${activeAgent}`}
        onClick={() => setIsOpen((p) => !p)}
      >
        {isOpen ? "✕ Close" : "💬 Agents"}
      </button>

      {isOpen && (
        <div className="acb-panel">
          <div className={`acb-header acb-header--${activeAgent}`}>
            <div className="acb-header-top">
              <div className="acb-header-info">
                <div
                  className={`acb-header-avatar acb-header-avatar--${activeAgent}`}
                >
                  {agent.icon}
                </div>

                <div>
                  <div className="acb-header-title">{agent.label}</div>
                  <div className="acb-header-online">● Online</div>
                </div>
              </div>

              <button
                className="acb-close-icon"
                onClick={() => setIsOpen(false)}
              >
                ✕
              </button>
            </div>

            <div className="acb-switcher">
              {Object.values(AGENTS).map((a) => (
                <button
                  key={a.id}
                  className={`acb-switch-btn ${
                    activeAgent === a.id
                      ? `active active--${activeAgent}`
                      : ""
                  }`}
                  onClick={() => switchAgent(a.id)}
                >
                  {a.icon} {a.label}
                </button>
              ))}
            </div>
          </div>

          <div className="acb-messages">
            {messages.map(renderMessage)}

            {loading && (
              <div className="acb-msg acb-msg--bot">
                <div className={`acb-avatar acb-avatar--${activeAgent}`}>
                  {agent.icon}
                </div>

                <div className="acb-bubble acb-typing">
                  <span />
                  <span />
                  <span />
                </div>
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          <div className="acb-input-area">
            <input
              ref={inputRef}
              type="text"
              placeholder={agent.placeholder}
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              disabled={loading}
            />

            <button
              className={`acb-send acb-send--${activeAgent}`}
              onClick={sendMessage}
              disabled={loading || !input.trim()}
            >
              ➤
            </button>
          </div>
        </div>
      )}
    </div>
  );

  return ReactDOM.createPortal(widget, document.body);
};

export default AgentChatbot;