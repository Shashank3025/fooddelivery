import React, { useEffect, useState } from "react";
import api from "../api/api";
import { useNavigate } from "react-router-dom";
import "./OrdersPage.css";

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [menuMap, setMenuMap] = useState({});
  const [restaurantMap, setRestaurantMap] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const navigate = useNavigate();
  const userId = localStorage.getItem("userId");

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError(null);

      const res = await api.get(`/orders/user/${userId}`);
      const ordersData = res.data || [];
      setOrders(ordersData);

      const restaurantIds = [...new Set(ordersData.map((o) => o.restaurantId))];

      const restaurantResponses = await Promise.all(
        restaurantIds.map((id) =>
          api.get(`/api/restaurants/${id}`).catch(() => null)
        )
      );

      const menuResponses = await Promise.all(
        restaurantIds.map((id) =>
          api.get(`/api/restaurants/${id}/menu?size=100`).catch(() => null)
        )
      );

      const restaurantData = {};
      const menuData = {};

      restaurantResponses.forEach((response) => {
        if (!response) return;
        restaurantData[response.data.id] = response.data.name;
      });

      menuResponses.forEach((response) => {
        if (!response) return;
        const items = response.data?.content || [];
        items.forEach((item) => {
          menuData[item.id] = item;
        });
      });

      setRestaurantMap(restaurantData);
      setMenuMap(menuData);
    } catch (err) {
      console.error(err);
      setError("Failed to load orders. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const getItemName = (item) =>
    item.itemName || menuMap[item.menuItemId]?.name || `Item #${item.menuItemId}`;

  const getItemPrice = (item) =>
    Number(item.itemPrice ?? menuMap[item.menuItemId]?.price ?? 0);

  const calculateTotal = (items = []) =>
    items
      .reduce((total, item) => total + getItemPrice(item) * (item.quantity || 0), 0)
      .toFixed(2);

  const getStatusClass = (status) => {
    const map = {
      CREATED: "status-created",
      CONFIRMED: "status-confirmed",
      PREPARING: "status-preparing",
      OUT_FOR_DELIVERY: "status-out",
      DELIVERED: "status-delivered",
      CANCELLED: "status-cancelled",
    };
    return map[status] || "status-created";
  };

  if (loading) {
    return <div className="orders-page"><p className="orders-message">Loading your delicious orders...</p></div>;
  }

  return (
    <div className="orders-page">
      <div className="orders-hero">
        <h1>🍽️ My Orders</h1>
        <p>Your food journey looks tasty today</p>

        <div className="orders-actions">
          <button className="back-restaurants-btn" onClick={() => navigate("/restaurants")}>
            ← Back to Restaurants
          </button>

          <button className="refresh-btn" onClick={fetchOrders}>
            ↻ Refresh Orders
          </button>
        </div>
      </div>

      {error && <p className="orders-error">{error}</p>}

      {orders.length === 0 ? (
        <div className="empty-orders">
          <h2>No orders yet</h2>
          <p>Go back and order something amazing.</p>
          <button onClick={() => navigate("/restaurants")}>Browse Restaurants</button>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map((order) => (
            <div className="order-card" key={order.id}>
              <div className="order-top">
                <div>
                  <h2>Order #{order.id}</h2>
                  <p className="restaurant-name">
                    {order.restaurantName ||
                      restaurantMap[order.restaurantId] ||
                      "Restaurant not available"}
                  </p>
                </div>

                <div className="order-right">
                  <span className={`status-badge ${getStatusClass(order.status)}`}>
                    {order.status || "CREATED"}
                  </span>
                  <p>{order.createdAt ? new Date(order.createdAt).toLocaleString() : "Just now"}</p>
                </div>
              </div>

              <div className="items-section">
                <h3>Order Items</h3>

                {order.items?.map((item, index) => (
                  <div className="item-card" key={index}>
                    <span className="item-name">{getItemName(item)}</span>
                    <span className="item-price">${getItemPrice(item).toFixed(2)}</span>
                    <span className="item-qty">x {item.quantity}</span>
                  </div>
                ))}
              </div>

              <div className="order-footer">
                <span>Thank you for ordering ❤️</span>
                <strong>Total: ${calculateTotal(order.items)}</strong>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}