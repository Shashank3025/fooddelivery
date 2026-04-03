import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import "./RestaurantsPage.css";

const restaurantImages = [
  "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4",
  "https://images.unsplash.com/photo-1552566626-52f8b828add9",
  "https://images.unsplash.com/photo-1544025162-d76694265947",
  "https://images.unsplash.com/photo-1414235077428-338989a2e8c0",
  "https://images.unsplash.com/photo-1555396273-367ea4eb4db5",
  "https://images.unsplash.com/photo-1528605248644-14dd04022da1",
];

function RestaurantsPage() {
  const [restaurants, setRestaurants] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadRestaurants();
  }, []);

  const loadRestaurants = async () => {
    try {
      const res = await api.get("/restaurants");
      setRestaurants(res.data || []);
    } catch (err) {
      console.error("Failed to load restaurants:", err);
      alert("Failed to load restaurants");
    } finally {
      setLoading(false);
    }
  };

  const filteredRestaurants = useMemo(() => {
    return restaurants.filter((restaurant) => {
      const name = restaurant.name?.toLowerCase() || "";
      const address = restaurant.address?.toLowerCase() || "";
      const q = search.toLowerCase();

      return name.includes(q) || address.includes(q);
    });
  }, [restaurants, search]);

  const openMenu = (restaurantId) => {
    navigate(`/restaurants/${restaurantId}/menu`);
  };

  return (
    <div className="restaurants-page">
      <section className="hero-section">
        <div className="hero-overlay"></div>

        <div className="hero-content">
          <p className="hero-tag">🍔 FoodExpress</p>
          <h1>Discover the best food near you</h1>
          <p className="hero-subtitle">
            Fresh meals, fast delivery, and top restaurants all in one place.
          </p>

          <div className="search-box">
            <input
              type="text"
              placeholder="Search by restaurant name or address"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </div>
      </section>

      <section className="restaurants-section">
        <div className="section-header">
          <h2>Popular Restaurants</h2>
          <p>{filteredRestaurants.length} places found</p>
        </div>

        {loading ? (
          <div className="status-message">Loading restaurants...</div>
        ) : filteredRestaurants.length === 0 ? (
          <div className="status-message">No restaurants found.</div>
        ) : (
          <div className="restaurant-grid">
            {filteredRestaurants.map((restaurant, index) => (
              <div
                key={restaurant.id}
                className="restaurant-card"
                onClick={() => openMenu(restaurant.id)}
              >
                <img
                  src={`${restaurantImages[index % restaurantImages.length]}?auto=format&fit=crop&w=1200&q=80`}
                  alt={restaurant.name}
                />

                <div className="restaurant-card-body">
                  <div className="restaurant-top-row">
                    <h3>{restaurant.name}</h3>
                    <span className="rating">⭐ 4.{(index % 5) + 3}</span>
                  </div>

                  <p className="category">Featured Restaurant</p>

                  <p className="address">
                    {restaurant.address || "Address not available"}
                  </p>

                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      openMenu(restaurant.id);
                    }}
                  >
                    View Menu
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

export default RestaurantsPage;