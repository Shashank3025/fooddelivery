import React, { useEffect, useState } from "react";
import api from "../api/api";
import "./OrdersPage.css";

export default function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [menuMap, setMenuMap] = useState({});
  const [restaurantMap, setRestaurantMap] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const userId = localStorage.getItem("userId");

  const fetchOrders = async () => {
    try {
      setLoading(true);
      setError(null);

      const res = await api.get(`/api/orders/user/${userId}`);
      const ordersData = res.data || [];
      setOrders(ordersData);

      if (ordersData.length === 0) {
        setRestaurantMap({});
        setMenuMap({});
        return;
      }

      const restaurantIds = [...new Set(ordersData.map((o) => o.restaurantId))];

      const restaurantResponses = await Promise.all(
        restaurantIds.map((id) =>
          api.get(`/api/restaurants/${id}`).catch((err) => {
            console.warn(`Restaurant ${id} not found:`, err.message);
            return null;
          })
        )
      );

      const menuResponses = await Promise.all(
        restaurantIds.map((id) =>
          api.get(`/api/restaurants/${id}/menu?size=100`).catch((err) => {
            console.warn(`Menu for restaurant ${id} not found:`, err.message);
            return null;
          })
        )
      );

      const restaurantData = {};
      const menuData = {};

      restaurantResponses.forEach((response) => {
        if (!response) return;
        const restaurant = response.data;
        restaurantData[restaurant.id] = restaurant.name;
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

  const getItemName = (item) => {
    if (item.itemName) return item.itemName;
    const menuItem = menuMap[item.menuItemId];
    if (menuItem?.name) return menuItem.name;
    return `Item #${item.menuItemId}`;
  };

  const getItemPrice = (item) => {
    if (item.itemPrice !== undefined && item.itemPrice !== null) {
      return Number(item.itemPrice);
    }
    const menuItem = menuMap[item.menuItemId];
    if (menuItem?.price !== undefined && menuItem?.price !== null) {
      return Number(menuItem.price);
    }
    return 0;
  };

  const calculateTotal = (items) => {
    return items
      .reduce((total, item) => {
        const price = getItemPrice(item);
        const qty = item.quantity || 0;
        return total + price * qty;
      }, 0)
      .toFixed(2);
  };

  const getStatusClass = (status) => {
    const map = {
      CREATED: "status-created",
      CONFIRMED: "status-confirmed",
      PREPARING: "status-preparing",
      OUT_FOR_DELIVERY: "status-out_for_delivery",
      DELIVERED: "status-delivered",
      CANCELLED: "status-cancelled",
    };
    return map[status] || "status-unknown";
  };

  const getStatusLabel = (status) => {
    const map = {
      CREATED: "● Created",
      CONFIRMED: "● Confirmed",
      PREPARING: "⏳ Preparing",
      OUT_FOR_DELIVERY: "🛵 On the way",
      DELIVERED: "✓ Delivered",
      CANCELLED: "✕ Cancelled",
    };
    return map[status] || status;
  };

  if (loading) {
    return (
      <div className="orders-page">
        <p className="orders-message">Loading your orders...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="orders-page">
        <p className="orders-error">{error}</p>
        <button className="refresh-btn" onClick={fetchOrders}>
          Try Again
        </button>
      </div>
    );
  }

  return (
    <div className="orders-page">
      <div className="orders-header">
        <h1>My Orders</h1>
        {orders.length > 0 && (
          <p>
            {orders.length} order{orders.length !== 1 ? "s" : ""} found
          </p>
        )}
      </div>

      <button className="refresh-btn" onClick={fetchOrders}>
        ↻ Refresh Orders
      </button>

      {orders.length === 0 && (
        <p className="orders-message">No orders found. Place your first order!</p>
      )}

      <div className="orders-list">
        {orders.map((order) => (
          <div className="order-card" key={order.id}>
            <div className="order-card-inner">
              <div className="order-top">
                <div>
                  <h2>Order #{order.id}</h2>
                  <p className="order-subtext">
                    {order.restaurantName ||
                      restaurantMap[order.restaurantId] ||
                      "Restaurant not available"}
                  </p>
                </div>

                <div className="order-right">
                  <span className={`status-badge ${getStatusClass(order.status)}`}>
                    {getStatusLabel(order.status)}
                  </span>
                  <p className="order-placed-time">
                    {new Date(order.createdAt).toLocaleString()}
                  </p>
                </div>
              </div>

              <div className="order-divider" />

              <div className="items-section">
                <h3>Items</h3>

                <div className="items-grid">
                  {order.items && order.items.length > 0 ? (
                    order.items.map((item, index) => (
                      <div className="item-card" key={index}>
                        <div className="item-row">
                          <span className="item-name">
                            {getItemName(item)}
                          </span>

                          <span className="item-price">
                            ${getItemPrice(item).toFixed(2)}
                          </span>

                          <span className="item-quantity">x {item.quantity}</span>
                        </div>
                      </div>
                    ))
                  ) : (
                    <p className="orders-message">No items found for this order.</p>
                  )}
                </div>
              </div>

              <p className="total-price">
                Total: ${calculateTotal(order.items || [])}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}