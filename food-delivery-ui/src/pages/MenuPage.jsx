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
  const [placingOrder, setPlacingOrder] = useState(false);

  useEffect(() => {
    loadMenu();
  }, [id]);

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
      const existing = prev.find((cartItem) => cartItem.menuItemId === item.id);

      if (existing) {
        return prev.map((cartItem) =>
          cartItem.menuItemId === item.id
            ? { ...cartItem, quantity: cartItem.quantity + 1, name: item.name, price: item.price }
            : cartItem
        );
      }

      return [
        ...prev,
        {
          menuItemId: item.id,
          quantity: 1,
          name: item.name,
          price: item.price,
        },
      ];
    });
  };

  const decreaseQuantity = (itemId) => {
    setCart((prev) =>
      prev
        .map((item) =>
          item.menuItemId === itemId
            ? { ...item, quantity: item.quantity - 1 }
            : item
        )
        .filter((item) => item.quantity > 0)
    );
  };

  const getQuantity = (itemId) => {
    const item = cart.find((cartItem) => cartItem.menuItemId === itemId);
    return item ? item.quantity : 0;
  };

  const totalItems = useMemo(() => {
    return cart.reduce((sum, item) => sum + item.quantity, 0);
  }, [cart]);

  const totalPrice = useMemo(() => {
    return cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
  }, [cart]);

  const placeOrder = async () => {
    if (cart.length === 0) {
      alert("Please add at least one item");
      return;
    }

    const userId = Number(localStorage.getItem("userId")) || 1;

    try {
      setPlacingOrder(true);

      await api.post("/orders", {
        userId,
        restaurantId: Number(id),
        status: "PENDING",
        items: cart.map((item) => ({
          menuItemId: item.menuItemId,
          quantity: item.quantity,
        })),
      });

      alert("Order placed successfully");
      setCart([]);
      navigate("/orders");
    } catch (err) {
      console.error("Order failed:", err);
      alert("Order failed");
    } finally {
      setPlacingOrder(false);
    }
  };

  return (
    <div className="menu-page">
      <section className="menu-hero">
        <div className="menu-hero-overlay"></div>
        <div className="menu-hero-content">
          <p className="menu-tag">🍽️ Curated Menu</p>
          <h1>Choose your favorite dishes</h1>
          <p>Freshly prepared meals from your selected restaurant.</p>
        </div>
      </section>

      <section className="menu-content">
        <div className="menu-left">
          <div className="section-title-row">
            <h2>Menu Items</h2>
            <span>{menuItems.length} items</span>
          </div>

          {loading ? (
            <div className="menu-status">Loading menu...</div>
          ) : menuItems.length === 0 ? (
            <div className="menu-status">No menu items found.</div>
          ) : (
            <div className="menu-grid">
              {menuItems.map((item, index) => (
                <div key={item.id} className="menu-card">
                  <img
                    src={`${foodImages[index % foodImages.length]}?auto=format&fit=crop&w=1200&q=80`}
                    alt={item.name}
                  />

                  <div className="menu-card-body">
                    <h3>{item.name}</h3>
                    <p className="menu-desc">Delicious chef-prepared food made fresh for every order.</p>

                    <div className="menu-bottom-row">
                      <span className="price">${Number(item.price).toFixed(2)}</span>

                      <div className="qty-controls">
                        <button
                          className="qty-btn minus"
                          onClick={() => decreaseQuantity(item.id)}
                        >
                          -
                        </button>

                        <span className="qty-value">{getQuantity(item.id)}</span>

                        <button
                          className="qty-btn plus"
                          onClick={() => addToCart(item)}
                        >
                          +
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="menu-right">
          <div className="cart-card">
            <h2>Your Cart</h2>
            <p className="cart-subtitle">Review your order before checkout</p>

            {cart.length === 0 ? (
              <div className="empty-cart">Your cart is empty.</div>
            ) : (
              <div className="cart-items">
                {cart.map((item) => (
                  <div key={item.menuItemId} className="cart-item">
                    <div>
                      <h4>{item.name}</h4>
                      <p>
                        {item.quantity} × ${Number(item.price).toFixed(2)}
                      </p>
                    </div>
                    <span>${(item.quantity * item.price).toFixed(2)}</span>
                  </div>
                ))}
              </div>
            )}

            <div className="cart-summary">
              <div className="summary-row">
                <span>Total Items</span>
                <span>{totalItems}</span>
              </div>
              <div className="summary-row total">
                <span>Total Price</span>
                <span>${totalPrice.toFixed(2)}</span>
              </div>
            </div>

            <button
              className="place-order-btn"
              onClick={placeOrder}
              disabled={placingOrder}
            >
              {placingOrder ? "Placing Order..." : "Place Order"}
            </button>
          </div>
        </div>
      </section>
    </div>
  );
}

export default MenuPage;