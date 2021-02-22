import React from "react";
import { Route, Redirect } from "react-router-dom";
import { managerPaths, clientPaths, CLIENT, MANAGER } from "./routesByRole";
import Forbidden from "../../containers/Forbidden/Forbidden";

export const PrivateRoute = ({
  component: Component,
  setIsLoggedIn,
  setIsClient,
  ...routeProps
}) => (
  <Route
    render={(props) => {
      const currentUser = localStorage.getItem("token");
      const role = localStorage.getItem("role");

      const { path } = routeProps;

      console.log("path AL QUE QUIERO IR", path);

      if (path === "/login") {
        if (role && currentUser) {
          return <Redirect to={{ pathname: "/" }} />;
        }
        return (
          <Component
            {...Object.assign({}, ...props, { setIsClient, setIsLoggedIn })}
          />
        );
      }

      if (!currentUser || !role) {
        // not logged in so redirect to login page with the return url
        return <Redirect to={{ pathname: "/login" }} />;
      }

      if (role === CLIENT) {
        console.log(clientPaths);
        if (clientPaths.indexOf(path) == -1) {
          return <Forbidden />;
        }
      } else if (role === MANAGER) {
        console.log(managerPaths);
        if (managerPaths.indexOf(path) == -1) {
          return <Forbidden />;
        }
      }

      // authorised so return component
      return <Component {...props} />;
    }}
    {...routeProps}
  />
);
