import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../api/api";
import "./ManagerDashboard.css";

function ManagerDashboard() {
  const [restaurants, setRestaurants] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadRestaurants();
  }, []);

  const loadRestaurants = async () => {
    try {
      const res = await api.get("/api/restaurants");
      setRestaurants(res.data || []);
    } catch (err) {
      console.error("Failed to load restaurants", err);
      alert("Failed to load restaurants");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="manager-page">
      <section className="manager-hero">
        <div className="manager-overlay"></div>
        <div className="manager-hero-content">
          <p className="manager-tag">👨‍🍳 Manager Panel</p>
          <h1>Manage your restaurant business</h1>
          <p>Add restaurants, add menu items, and monitor the platform.</p>
        </div>
      </section>

      <section className="manager-content">
        <div className="manager-actions">
          <Link to="/manager/add-restaurant" className="manager-action-card">
            <h3>Add Restaurant</h3>
            <p>Create a new restaurant and publish it to the platform.</p>
          </Link>

          <Link to="/manager/add-menu-item" className="manager-action-card">
            <h3>Add Menu Item</h3>
            <p>Add dishes and prices to an existing restaurant.</p>
          </Link>

          <Link to="/orders" className="manager-action-card">
            <h3>View Orders</h3>
            <p>Track all current customer orders and statuses.</p>
          </Link>
        </div>

        <div className="manager-list-section">
          <div className="manager-list-header">
            <h2>All Restaurants</h2>
            <span>{restaurants.length} total</span>
          </div>

          {loading ? (
            <div className="manager-status">Loading restaurants...</div>
          ) : restaurants.length === 0 ? (
            <div className="manager-status">No restaurants found.</div>
          ) : (
            <div className="manager-restaurant-grid">
              {restaurants.map((restaurant) => (
                <div key={restaurant.id} className="manager-restaurant-card">
                  <h3>{restaurant.name}</h3>
                  <p>{restaurant.address}</p>
                  <div className="restaurant-id">Restaurant ID: {restaurant.id}</div>
                </div>
              ))}
            </div>
          )}
        </div>
      </section>
    </div>
  );
}

export default ManagerDashboard;