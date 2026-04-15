import React, { useEffect, useState } from "react";
import api from "../api/api";
import "./OrdersPage.css";

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const userId = localStorage.getItem("userId");

  const fetchOrders = async () => {
    if (!userId) {
      setError("User not logged in");
      setOrders([]);
      return;
    }

    try {
      setLoading(true);
      setError("");

      const response = await api.get(`/api/orders/user/${userId}`);
      console.log("Orders response:", response.data);

      const data = response.data;

      if (Array.isArray(data)) {
        setOrders(data);
      } else if (Array.isArray(data?.content)) {
        setOrders(data.content);
      } else if (data && typeof data === "object") {
        setOrders([data]);
      } else {
        setOrders([]);
      }
    } catch (err) {
      console.error("Error fetching orders:", err);
      setError("Failed to load orders");
      setOrders([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  return (
    <div className="orders-page">
      <div className="orders-header">
        <h1>My Orders</h1>
        <p>Track your placed food orders</p>
      </div>

      {loading && <p className="orders-message">Loading orders...</p>}
      {error && <p className="orders-error">{error}</p>}

      {!loading && !error && orders.length === 0 && (
        <p className="orders-message">No orders found</p>
      )}

      <div className="orders-list">
        {Array.isArray(orders) &&
          orders.map((order) => (
            <div className="order-card" key={order.id}>
              <div className="order-top">
                <div>
                  <h2>Order #{order.id}</h2>
                  <p className="order-subtext">
                    Restaurant ID: {order.restaurantId}
                  </p>
                </div>

                <div className="order-right">
                  <span
                    className={`status-badge status-${order.status?.toLowerCase()}`}
                  >
                    {order.status || "UNKNOWN"}
                  </span>
                  <p className="order-placed-time">
                    Order Placed:{" "}
                    {order.createdAt
                      ? new Date(order.createdAt).toLocaleString()
                      : "N/A"}
                  </p>
                </div>
              </div>

              <div className="order-info">
                <p>
                  <strong>User ID:</strong> {order.userId}
                </p>
              </div>

              <div className="items-section">
                <h3>Items</h3>

                {Array.isArray(order.items) && order.items.length > 0 ? (
                  <div className="items-grid">
                    {order.items.map((item, index) => (
                      <div className="item-card" key={item.id ?? index}>
                        <p>
                          <strong>Menu Item ID:</strong> {item.menuItemId}
                        </p>
                        <p>
                          <strong>Quantity:</strong> {item.quantity}
                        </p>
                      </div>
                    ))}
                  </div>
                ) : (
                  <p className="no-items">No items in this order</p>
                )}
              </div>
            </div>
          ))}
      </div>
    </div>
  );
}