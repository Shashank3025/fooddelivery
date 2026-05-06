import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./ProcessingPage.css";

export default function ProcessingPage() {
  const navigate = useNavigate();
  const [step, setStep] = useState(0);

  const messages = [
    "Checking your cart...",
    "Connecting with restaurant...",
    "Preparing secure payment...",
    "Almost ready..."
  ];

  useEffect(() => {
    const stepTimer = setInterval(() => {
      setStep((prev) => (prev < messages.length - 1 ? prev + 1 : prev));
    }, 1200);

    const pageTimer = setTimeout(() => {
      navigate("/payment");
    }, 5000);

    return () => {
      clearInterval(stepTimer);
      clearTimeout(pageTimer);
    };
  }, [navigate, messages.length]);

  return (
    <div className="processing-page">
      <div className="processing-card">
        <div className="food-orbit">
          <span>🍕</span>
          <span>🍔</span>
          <span>🥤</span>
          <span>🍟</span>
        </div>

        <div className="delivery-track">
          <div className="delivery-bike">🛵</div>
        </div>

        <h1>Preparing your checkout</h1>
        <p>{messages[step]}</p>

        <div className="progress-bar">
          <div className="progress-fill"></div>
        </div>

        <div className="processing-steps">
          {messages.map((msg, index) => (
            <div
              key={msg}
              className={`processing-step ${index <= step ? "active" : ""}`}
            >
              <span>{index < step ? "✓" : index === step ? "●" : "○"}</span>
              {msg}
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}