// PrivateRoute.js
import React from "react";
import { Navigate } from "react-router-dom";
import { isLoggedIn } from "../utils";

const PrivateRoute = ({ children }) => {
  return isLoggedIn() ? children : <Navigate to="/login" replace />;
};

export default PrivateRoute;
