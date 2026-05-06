import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import api from "../api/api";
import "./MenuPage.css";

const foodImages = [
  "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38",
  "https://images.unsplash.com/photo-1550547660-d9450f859349",
  "https://images.unsplash.com/photo-1600891964092-4316c288032e",
  "https://images.unsplash.com/photo-1546069901-ba9599a7e63c",
  "https://images.unsplash.com/photo-1512058564366-18510be2db19",
  "https://images.unsplash.com/photo-1567620905732-2d1ec7ab7445",
];

function MenuPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [menuItems, setMenuItems] = useState([]);
  const [cart, setCart] = useState([]);
  const [loading, setLoading] = useState(true);
  const [addingToCart, setAddingToCart] = useState(false);
  const [restaurantName, setRestaurantName] = useState("");

  useEffect(() => {
    loadMenu();
    loadRestaurant();
  }, [id]);

  const loadRestaurant = async () => {
    try {
      const res = await api.get(`/restaurants/${id}`);
      setRestaurantName(res.data?.name || "");
    } catch (err) {
      console.error("Failed to load restaurant:", err);
    }
  };

  const loadMenu = async () => {
    try {
      const res = await api.get(`/restaurants/${id}/menu?page=0&size=20`);
      setMenuItems(res.data.content || []);
    } catch (err) {
      console.error("Failed to load menu:", err);
      alert("Failed to load menu");
    } finally {
      setLoading(false);
    }
  };

  const addToCart = (item) => {
    setCart((prev) => {
      const existing = prev.find((c) => c.menuItemId === item.id);

      if (existing) {
        return prev.map((c) =>
          c.menuItemId === item.id
            ? { ...c, quantity: c.quantity + 1 }
            : c
        );
      }

      return [
        ...prev,
        {
          menuItemId: item.id,
          quantity: 1,
          itemName: item.name,
          itemPrice: Number(item.price),
          name: item.name,
          price: Number(item.price),
        },
      ];
    });
  };

  const decreaseQuantity = (id) => {
    setCart((prev) =>
      prev
        .map((item) =>
          item.menuItemId === id
            ? { ...item, quantity: item.quantity - 1 }
            : item
        )
        .filter((item) => item.quantity > 0)
    );
  };

  const getQuantity = (id) => {
    const item = cart.find((c) => c.menuItemId === id);
    return item ? item.quantity : 0;
  };

  const totalItems = useMemo(
    () => cart.reduce((sum, item) => sum + item.quantity, 0),
    [cart]
  );

  const totalPrice = useMemo(
    () => cart.reduce((sum, item) => sum + item.price * item.quantity, 0),
    [cart]
  );

  const saveCartToRedis = async () => {
    if (cart.length === 0) return alert("Add items first");

    try {
      setAddingToCart(true);

      const userId = Number(localStorage.getItem("userId")) || 1;

      for (const item of cart) {
        await api.post("/cart/items", {
          userId: Number(userId),
          restaurantName: restaurantName,
          restaurantId: Number(id),
          menuItemId: item.menuItemId,
          name: item.name,
          price: Number(item.price),
          quantity: item.quantity,
        });
      }

      alert("Items added to cart!");
      navigate("/cart");
    } catch (err) {
      console.error(err);
      alert("Failed to add items to cart");
    } finally {
      setAddingToCart(false);
    }
  };

  return (
    <div className="menu-page">
      <div className="top-section">
        <div className="hero">
          <div className="hero-overlay"></div>

          <div className="hero-content">
            <p className="tag">🍽️ Curated Menu</p>
            <h1>{restaurantName || "Choose your favorite dishes"}</h1>
            <p>Freshly prepared meals from your selected restaurant.</p>
          </div>
        </div>

        <div className="cart-card">
          <button className="view-cart-btn" onClick={() => navigate("/cart")}>
            🛒 View Cart
          </button>

          <h2>Your Cart</h2>

          <div className="cart-summary">
            <div>
              <span>Total Items</span>
              <span>{totalItems}</span>
            </div>

            <div className="cart-items-container">
              {cart.length === 0 ? (
                <p className="empty">Your cart is empty</p>
              ) : (
                cart.map((item) => (
                  <div key={item.menuItemId} className="cart-item">
                    <span>{item.name}</span>
                    <span>{item.quantity}</span>
                  </div>
                ))
              )}
            </div>

            <div>
              <span>Total Price</span>
              <span>${totalPrice.toFixed(2)}</span>
            </div>
          </div>

          <button onClick={saveCartToRedis} disabled={addingToCart}>
            {addingToCart ? "Adding..." : "Add to Cart"}
          </button>
        </div>
      </div>

      <div className="menu-section">
        <h2>Menu Items</h2>

        {loading ? (
          <p>Loading...</p>
        ) : (
          <div className="menu-grid">
            {menuItems.map((item, index) => (
              <div key={item.id} className="menu-card">
                <img
                  src={`${
                    foodImages[index % foodImages.length]
                  }?auto=format&fit=crop&w=1200&q=80`}
                  alt={item.name}
                />

                <div className="card-body">
                  <h3>{item.name}</h3>
                  <p>Fresh & delicious food</p>

                  <div className="bottom">
                    <span className="price">
                      ${Number(item.price).toFixed(2)}
                    </span>

                    <div className="qty">
                      <button onClick={() => decreaseQuantity(item.id)}>-</button>
                      <span>{getQuantity(item.id)}</span>
                      <button onClick={() => addToCart(item)}>+</button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

export default MenuPage;