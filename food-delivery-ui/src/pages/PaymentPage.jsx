import React, { useEffect, useState } from "react";
import api from "../api/api";
import { useNavigate } from "react-router-dom";
import "./PaymentPage.css";

const PaymentPage = () => {
  const navigate = useNavigate();

  const [processing, setProcessing] = useState(false);
  const [paymentDone, setPaymentDone] = useState(false);

  const [paymentCart] = useState(() => {
    return JSON.parse(localStorage.getItem("paymentCart"));
  });

  const [paymentMethod, setPaymentMethod] = useState("CARD");

  useEffect(() => {
    if (!paymentCart || !paymentCart.items || paymentCart.items.length === 0) {
      alert("No cart found for payment");
      navigate("/cart");
    }
  }, [paymentCart, navigate]);

  if (!paymentCart) {
    return <div className="payment-page">Loading payment...</div>;
  }

  const totalAmount = paymentCart.items.reduce((sum, item) => {
    const price = Number(item.price || item.itemPrice || 0);
    const quantity = Number(item.quantity || 1);
    return sum + price * quantity;
  }, 0);

  const handlePayment = () => {
    setProcessing(true);
    setPaymentDone(false);

    setTimeout(async () => {
      try {
        const orderPayload = {
          userId: Number(paymentCart.userId),
          restaurantId: paymentCart.restaurantId,
          restaurantName: paymentCart.restaurantName,
          items: paymentCart.items.map((item) => ({
            menuItemId: item.menuItemId,
            itemName: item.itemName || item.name,
            itemPrice: Number(item.price || item.itemPrice),
            quantity: Number(item.quantity),
          })),
        };

        const orderRes = await api.post("/orders", orderPayload);
        const createdOrder = orderRes.data;

       const idempotencyKey = `PAY-${createdOrder.id}`;

await api.post("/payments", {
  orderId: createdOrder.id,
  amount: totalAmount,
  paymentMethod: paymentMethod,
  idempotencyKey,
});

        localStorage.removeItem("paymentCart");
        setPaymentDone(true);

        setTimeout(() => {
          setProcessing(false);
          navigate("/orders");
        }, 1500);
      } catch (err) {
        console.error("Payment failed:", err);
        setProcessing(false);
        setPaymentDone(false);
        alert("Payment failed. Please try again.");
      }
    }, 5000);
  };

  return (
    <div className="payment-page">
      {processing && (
        <div className="payment-overlay">
          <div className="payment-loader-card">
            <div className={paymentDone ? "money-icon done" : "money-icon"}>
              {paymentDone ? "✅" : "💵"}
            </div>

            <h2>{paymentDone ? "Payment Done!" : "Processing Payment..."}</h2>

            <p>
              {paymentDone
                ? "Your order has been placed successfully."
                : "Please wait while we confirm your payment."}
            </p>

            {!paymentDone && (
              <div className="simple-loader">
                <span></span>
              </div>
            )}
          </div>
        </div>
      )}

      <div className="payment-card">
        <h1>Complete Payment</h1>
        <p className="payment-subtitle">
          Review your order before placing payment
        </p>

        <div className="payment-summary">
          <h2>{paymentCart.restaurantName}</h2>

          {paymentCart.items.map((item) => {
            const price = Number(item.price || item.itemPrice || 0);
            const quantity = Number(item.quantity || 1);

            return (
              <div className="payment-item" key={item.menuItemId}>
                <span>
                  {item.itemName || item.name} × {quantity}
                </span>
                <span>${(price * quantity).toFixed(2)}</span>
              </div>
            );
          })}

          <div className="payment-total">
            <span>Total Amount</span>
            <strong>${totalAmount.toFixed(2)}</strong>
          </div>
        </div>

        <div className="payment-methods">
          <h2>Payment Method</h2>

          <label>
            <input
              type="radio"
              value="CARD"
              checked={paymentMethod === "CARD"}
              onChange={(e) => setPaymentMethod(e.target.value)}
            />
            Card Payment
          </label>

          <label>
            <input
              type="radio"
              value="UPI"
              checked={paymentMethod === "UPI"}
              onChange={(e) => setPaymentMethod(e.target.value)}
            />
            UPI
          </label>

          <label>
            <input
              type="radio"
              value="CASH"
              checked={paymentMethod === "CASH"}
              onChange={(e) => setPaymentMethod(e.target.value)}
            />
            Cash on Delivery
          </label>
        </div>

        <button
          className="pay-now-btn"
          onClick={handlePayment}
          disabled={processing}
        >
          {processing
            ? "Processing..."
            : `Pay $${totalAmount.toFixed(2)} & Place Order`}
        </button>

        <button
          className="back-cart-btn"
          onClick={() => navigate("/cart")}
          disabled={processing}
        >
          Back to Cart
        </button>
      </div>
    </div>
  );
};

export default PaymentPage;