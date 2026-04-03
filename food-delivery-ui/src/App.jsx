import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import RestaurantsPage from "./pages/RestaurantsPage";
import MenuPage from "./pages/MenuPage";
import OrdersPage from "./pages/OrdersPage";
import ManagerDashboard from "./pages/ManagerDashboard";
import AddRestaurantPage from "./pages/AddRestaurantPage";
import AddMenuItemPage from "./pages/AddMenuItemPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/restaurants" element={<RestaurantsPage />} />
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