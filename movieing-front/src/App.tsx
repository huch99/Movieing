import { BrowserRouter, Route, Routes } from "react-router-dom"
import Home from "./pages/Home/Home"
import './App.css';
import AdminPage from "./pages/Admin/AdminPage";
import Layout from "./components/layout/Layout";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 공통 Layout */}
        <Route element={<Layout />}>
          {/* User */}
          <Route path="/" element={<Home />} />

          {/* Admin */}
          <Route path="/admin" element={<AdminPage />} />
        </Route>

        {/* Optional: 404 */}
        <Route path="*" element={<Home />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
