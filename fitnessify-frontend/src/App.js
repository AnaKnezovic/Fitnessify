import React, { useEffect } from "react";
import { Routes, Route, Navigate, useLocation, useParams } from "react-router-dom";
import RegistrationForm from "./components/RegistrationForm";
import LoginPage from "./components/LoginPage";
import HomePage from "./components/HomePage";
import MyProfile from "./components/MyProfile";
import PrivateRoute from "./components/PrivateRoute";
import RoleRoute from "./components/RoleRoute";
import PageNotFound from "./components/PageNotFound";
import Namirnice from "./components/Namirnice";
import { isTokenExpired, isLoggedIn } from "./utils";
import Navbar from "./components/Navbar";
import PrehrambeniDnevnik from "./components/PrehrambeniDnevnik";
import HelpPage from "./components/HelpPage";
import Aktivnosti from "./components/Aktivnosti";
import OdaberiTrenera from "./components/OdaberiTrenera";
import MojiPlanovi from "./components/MojiPlanovi";
import DodijeliPlan from "./components/DodijeliPlan";
import MojiKlijenti from "./components/MojiKlijenti";
import DetaljiKlijenta from "./components/DetaljiKlijenta";
import MojeAktivnosti from "./components/MojeAktivnosti";

function App() {
  useEffect(() => {
    const interval = setInterval(() => {
      const token = localStorage.getItem("token");
      if (token && isTokenExpired(token)) {
        localStorage.removeItem("token");
        window.location.href = "/login";
      }
    }, 60 * 1000);

    return () => clearInterval(interval);
  }, []);

  const location = useLocation();
  const hideNavbar =
    location.pathname === "/login" ||
    location.pathname === "/register" ||
    location.pathname === "/not-found";

  const DetaljiKlijentaWrapper = () => {
    const { id } = useParams();
    return <DetaljiKlijenta klijentId={id} />;
  };

  return (
    <>
      {!hideNavbar && <Navbar />}
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegistrationForm />} />

        <Route
          path="/"
          element={
            <PrivateRoute>
              <HomePage />
            </PrivateRoute>
          }
        />
        <Route
          path="/home"
          element={
            <PrivateRoute>
              <HomePage />
            </PrivateRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <MyProfile />
            </PrivateRoute>
          }
        />
        <Route
          path="/namirnice"
          element={
            <PrivateRoute>
              <Namirnice />
            </PrivateRoute>
          }
        />
        <Route
          path="/faq"
          element={
            <PrivateRoute>
              <HelpPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/treneri"
          element={
            <PrivateRoute>
              <RoleRoute allowedRoles={["KLIJENT"]}>
                <OdaberiTrenera />
              </RoleRoute>
            </PrivateRoute>
          }
        />
        <Route
          path="/moji-planovi"
          element={
            <PrivateRoute>
              <RoleRoute allowedRoles={["KLIJENT"]}>
                <MojiPlanovi />
              </RoleRoute>
            </PrivateRoute>
          }
        />
        <Route
          path="/dodijeli-plan"
          element={
            <PrivateRoute>
              <RoleRoute allowedRoles={["TRENER"]}>
                <DodijeliPlan />
              </RoleRoute>
            </PrivateRoute>
          }
        />
        <Route
          path="/moji-klijenti"
          element={
            <PrivateRoute>
              <RoleRoute allowedRoles={["TRENER"]}>
                <MojiKlijenti />
              </RoleRoute>
            </PrivateRoute>
          }
        />
        <Route
          path="/moje-aktivnosti"
          element={
            <PrivateRoute>
              <MojeAktivnosti />
            </PrivateRoute>
          }
        />
        <Route
          path="/aktivnosti"
          element={
            <PrivateRoute>
              <Aktivnosti />
            </PrivateRoute>
          }
        />
        <Route
          path="/not-found"
          element={<PageNotFound />}
        />
        <Route
          path="/prehrambeni-dnevnik"
          element={
            <PrivateRoute>
              <PrehrambeniDnevnik />
            </PrivateRoute>
          }
        />
        <Route
          path="/klijenti/:id"
          element={
            <PrivateRoute>
              <RoleRoute allowedRoles={["TRENER"]}>
                <DetaljiKlijentaWrapper />
              </RoleRoute>
            </PrivateRoute>
          }
        />

        <Route
          path="*"
          element={
            isLoggedIn() ? (
              <Navigate to="/not-found" replace />
            ) : (
              <Navigate to="/login" replace />
            )
          }
        />
      </Routes>
    </>
  );
}

export default App;
