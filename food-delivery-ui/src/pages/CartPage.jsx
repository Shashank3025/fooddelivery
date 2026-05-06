import { useEffect, useState } from "react";
import api from "../api/api";
import { useNavigate } from "react-router-dom";
import "./CartPage.css";

function CartPage() {
  const [cart, setCart] = useState(null);
  const navigate = useNavigate();
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    if (!userId) {
      navigate("/login");
      return;
    }

    const loadCart = async () => {
      try {
        const res = await api.get(`/cart/viewcart/${userId}`);
        setCart(res.data);
      } catch (err) {
        console.error("Failed to load cart:", err);
        alert("Failed to load cart");
      }
    };

    loadCart();
  }, [userId, navigate]);

  const refreshCart = async () => {
    try {
      const res = await api.get(`/cart/viewcart/${userId}`);
      setCart(res.data);
    } catch (err) {
      console.error("Failed to refresh cart:", err);
      alert("Failed to refresh cart");
    }
  };

  const deleteCartItem = async (menuItemId, restaurantId) => {
    try {
      await api.delete(`/cart/items/${userId}/${restaurantId}/${menuItemId}`);
      await refreshCart();
    } catch (err) {
      console.error("Failed to delete item:", err);
      alert("Failed to delete item");
    }
  };

  const goToPaymentForRestaurant = (items) => {
    const firstItem = items[0];

    const restaurantCart = {
      userId: Number(userId),
      restaurantId: firstItem.restaurantId,
      restaurantName: firstItem.restaurantName,
      items: items.map((item) => ({
        menuItemId: item.menuItemId,
        itemName: item.name,
        name: item.name,
        itemPrice: Number(item.price),
        price: Number(item.price),
        quantity: item.quantity,
        restaurantId: item.restaurantId,
        restaurantName: item.restaurantName,
      })),
      totalAmount: items.reduce(
        (sum, item) => sum + Number(item.price) * Number(item.quantity),
        0
      ),
    };

    localStorage.setItem("paymentCart", JSON.stringify(restaurantCart));
    navigate("/processing");
  };

  if (!cart) {
    return <div className="cart-container">Loading cart...</div>;
  }

  const groupedItems = cart.items?.reduce((groups, item) => {
    const restaurantName = item.restaurantName || "Restaurant Not Available";

    if (!groups[restaurantName]) {
      groups[restaurantName] = [];
    }

    groups[restaurantName].push(item);
    return groups;
  }, {});

  return (
    <div className="cart-container">
      <h1>Your Cart</h1>

      {cart.items && cart.items.length === 0 ? (
        <p>Your cart is empty</p>
      ) : (
        <div className="cart-list">
          {Object.entries(groupedItems || {}).map(([restaurantName, items]) => {
            const restaurantTotal = items.reduce(
              (sum, item) => sum + item.price * item.quantity,
              0
            );

            return (
              <div key={restaurantName} className="restaurant-cart-group">
                <h2 className="restaurant-cart-title">{restaurantName}</h2>

                {items.map((item) => (
                  <div
                    key={`${item.restaurantId}-${item.menuItemId}`}
                    className="cart-item"
                  >
                    <div>
                      <h3>{item.name}</h3>
                      <p>Price: ${item.price}</p>
                      <p>Quantity: {item.quantity}</p>

                      <button
                        className="delete-item-btn"
                        onClick={() =>
                          deleteCartItem(item.menuItemId, item.restaurantId)
                        }
                      >
                        Delete
                      </button>
                    </div>

                    <div className="cart-total">
                      ${(item.price * item.quantity).toFixed(2)}
                    </div>
                  </div>
                ))}

                <h3 className="restaurant-subtotal">
                  Restaurant Total: ${restaurantTotal.toFixed(2)}
                </h3>

                <button
                  className="place-order-btn"
                  onClick={() => goToPaymentForRestaurant(items)}
                >
                  Proceed to Payment
                </button>
              </div>
            );
          })}
        </div>
      )}

      <div className="cart-summary">
        <h2>Total Items: {cart.totalQuantity}</h2>
        <h2>Total Amount: ${Number(cart.totalAmount).toFixed(2)}</h2>
      </div>

      <button className="back-btn" onClick={() => navigate("/restaurants")}>
        ← Back to Restaurants
      </button>
    </div>
  );
}

export default CartPage;