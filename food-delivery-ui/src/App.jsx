import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import RestaurantsPage from "./pages/RestaurantsPage";
import MenuPage from "./pages/MenuPage";
import OrdersPage from "./pages/OrdersPage";
import CartPage from "./pages/CartPage";
import ProcessingPage from "./pages/ProcessingPage";
import PaymentPage from "./pages/PaymentPage";
import ManagerDashboard from "./pages/ManagerDashboard";
import AddRestaurantPage from "./pages/AddRestaurantPage";
import AddMenuItemPage from "./pages/AddMenuItemPage";
import SplashScreen from "./pages/SplashScreen";
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<SplashScreen />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/restaurants" element={<RestaurantsPage />} />
        <Route path="/cart" element={<CartPage />} />
        <Route path="/processing" element={<ProcessingPage />} />
        <Route path="/payment" element={<PaymentPage />} />
        <Route path="/restaurants/:id/menu" element={<MenuPage />} />
        <Route path="/orders" element={<OrdersPage />} />
        <Route path="/manager" element={<ManagerDashboard />} />
        <Route path="/manager/add-restaurant" element={<AddRestaurantPage />} />
        <Route path="/manager/add-menu-item" element={<AddMenuItemPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;