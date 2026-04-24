import { useState } from "react";
import api from "../api/api";
import "./AddMenuItemPage.css";

function AddMenuItemPage() {
  const [form, setForm] = useState({
    name: "",
    price: "",
    restaurantId: "",
  });

  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.name.trim() || !form.price || !form.restaurantId) {
      alert("Please enter item name, price, and restaurant ID");
      return;
    }

    setLoading(true);

    try {
      await api.post(`/restaurants/${form.restaurantId}/menu`, {
        name: form.name,
        price: parseFloat(form.price),
      });

      alert("Menu item added successfully!");

      setForm({
        name: "",
        price: "",
        restaurantId: "",
      });
    } catch (err) {
      console.error("Add menu item error:", err);
      alert("Failed to add menu item");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="menu-page">
      <div className="menu-bg-orb orb-1"></div>
      <div className="menu-bg-orb orb-2"></div>
      <div className="menu-bg-orb orb-3"></div>

      <div className="menu-shell">
        <div className="menu-left-panel">
          <div className="brand-badge">Manager Panel</div>

          <h1>
            Create a <span>show-stopping</span> menu item
          </h1>

          <p>
            Add new food items for a restaurant with a clean and production-style
            manager dashboard experience.
          </p>

          <div className="feature-grid">
            <div className="feature-card">
              <div className="feature-icon">🍔</div>
              <div>
                <h3>Menu Ready</h3>
                <p>Build attractive food listings for customers instantly.</p>
              </div>
            </div>

            <div className="feature-card">
              <div className="feature-icon">⚡</div>
              <div>
                <h3>Fast Input</h3>
                <p>Simple manager workflow for adding items quickly.</p>
              </div>
            </div>

            <div className="feature-card">
              <div className="feature-icon">💎</div>
              <div>
                <h3>Premium Look</h3>
                <p>Bright gradients, glass cards, and modern form styling.</p>
              </div>
            </div>

            <div className="feature-card">
              <div className="feature-icon">📦</div>
              <div>
                <h3>Restaurant Control</h3>
                <p>Map menu items to the correct restaurant with full clarity.</p>
              </div>
            </div>
          </div>
        </div>

        <div className="menu-form-card">
          <div className="menu-form-header">
            <div>
              <p className="mini-label">Food Delivery Admin</p>
              <h2>Add Menu Item</h2>
            </div>
            <div className="live-dot-wrap">
              <span className="live-dot"></span>
              <span>Live</span>
            </div>
          </div>

          <form className="menu-form" onSubmit={handleSubmit}>
            <div className="input-group">
              <label>Item Name</label>
              <input
                type="text"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="Ex: Truffle Cheese Burger"
                required
              />
            </div>

            <div className="input-row">
              <div className="input-group">
                <label>Price</label>
                <input
                  type="number"
                  step="0.01"
                  name="price"
                  value={form.price}
                  onChange={handleChange}
                  placeholder="Ex: 12.99"
                  required
                />
              </div>

              <div className="input-group">
                <label>Restaurant ID</label>
                <input
                  type="number"
                  name="restaurantId"
                  value={form.restaurantId}
                  onChange={handleChange}
                  placeholder="Ex: 1"
                  required
                />
              </div>
            </div>

            <div className="preview-box">
              <div className="preview-chip">Preview</div>
              <h3>{form.name || "Your dish name appears here"}</h3>

              <div className="preview-meta">
                <span>💰 ${form.price || "0.00"}</span>
                <span>🏪 Restaurant ID: {form.restaurantId || "Not selected"}</span>
              </div>
            </div>

            <button type="submit" className="submit-btn" disabled={loading}>
              {loading ? "Adding Item..." : "Add Menu Item"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default AddMenuItemPage;