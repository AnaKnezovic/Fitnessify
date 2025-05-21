// src/components/RoleRoute.js
import React from "react";
import { Navigate } from "react-router-dom";

const RoleRoute = ({ allowedRoles, children }) => {
  const role = localStorage.getItem("role");
  if (!allowedRoles.includes(role)) {
    return <Navigate to="/not-found" replace />;
  }
  return children;
};

export default RoleRoute;
