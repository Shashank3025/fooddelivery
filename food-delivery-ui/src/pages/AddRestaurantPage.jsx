import { useState } from "react";
import api from "../api/api";
import "./AddRestaurantPage.css";

function AddRestaurantPage() {
  const [form, setForm] = useState({
    name: "",
    address: "",
    theme: "Modern",
    vibe: "Casual Dining",
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

    if (!form.name.trim() || !form.address.trim()) {
      alert("Enter restaurant name and address");
      return;
    }

    try {
      setLoading(true);

      await api.post("/api/restaurants", {
        name: form.name,
        address: form.address,
      });

      alert("Restaurant created successfully!");

      setForm({
        name: "",
        address: "",
        theme: "Modern",
        vibe: "Casual Dining",
      });
    } catch (err) {
      console.error(err);
      alert("Failed to create restaurant");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="add-rest-page">
      {/* Background glow elements */}
      <div className="bg-glow g1"></div>
      <div className="bg-glow g2"></div>
      <div className="bg-glow g3"></div>

      <div className="rest-container">
        
        {/* LEFT SIDE */}
        <div className="rest-left">
          <div className="badge">🏪 Restaurant Builder</div>

          <h1>
            Launch your <span>next viral restaurant</span>
          </h1>

          <p>
            Create restaurant profiles with personality, presence, and premium
            experience. Make your brand unforgettable.
          </p>

          <div className="insights">
            <div className="insight-card">
              <h3>⚡ Instant Listing</h3>
              <p>Go live in seconds with structured data.</p>
            </div>

            <div className="insight-card">
              <h3>🎯 Brand Identity</h3>
              <p>Define vibe, theme, and presence.</p>
            </div>

            <div className="insight-card">
              <h3>🚀 Scale Ready</h3>
              <p>Built for multi-restaurant expansion.</p>
            </div>
          </div>
        </div>

        {/* RIGHT SIDE FORM */}
        <div className="rest-form-card">
          <h2>Create Restaurant</h2>

          <form onSubmit={handleSubmit}>
            <input
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="Restaurant Name"
            />

            <input
              name="address"
              value={form.address}
              onChange={handleChange}
              placeholder="Address"
            />

            <div className="row">
              <select name="theme" value={form.theme} onChange={handleChange}>
                <option>Modern</option>
                <option>Luxury</option>
                <option>Traditional</option>
                <option>Street Food</option>
              </select>

              <select name="vibe" value={form.vibe} onChange={handleChange}>
                <option>Casual Dining</option>
                <option>Fine Dining</option>
                <option>Quick Bites</option>
                <option>Family Friendly</option>
              </select>
            </div>

            {/* LIVE PREVIEW */}
            <div className="preview">
              <h3>{form.name || "Restaurant Name"}</h3>
              <p>{form.address || "Location will appear here"}</p>

              <div className="preview-tags">
                <span>{form.theme}</span>
                <span>{form.vibe}</span>
              </div>
            </div>

            <button disabled={loading}>
              {loading ? "Creating..." : "Create Restaurant"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default AddRestaurantPage;