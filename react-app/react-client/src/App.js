import React, { Component } from "react";

import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import { PrivateRoute } from "./components/PrivateRoute/PrivateRoute";

import "./App.css";

import Login from "./containers/Login/Login";
import Principal from "./containers/Principal/Principal";
import Reservation from "./containers/Reservation/Reservation";
import CheckIn from "./containers/Check-In/Check-In";
import Registration from "./containers/Registration/Registration";
import CheckOut from "./containers/Check-Out/Check-Out";
import Reservations from "./containers/Reservations/Reservations";
import Products from "./containers/Products/Products";
import NewProduct from "./containers/Products/NewProduct";
import Orders from "./containers/Orders/Orders";
import HelpRequest from "./containers/HelpRequest/HelpRequest";
import Navbar from "./components/Navbar/Navbar";
import UserNavbar from "./components/Navbar/UserNavbar";

import UserPrincipal from "./containers/User/Principal";
import UserHelp from "./containers/User/Help";
import UserProducts from "./containers/User/Products";
import UserExpenses from "./containers/User/Expenses";
import { CLIENT } from "./components/PrivateRoute/routesByRole";
import { NotFound } from "./containers/NotFound/NotFound";

const isClient = () => localStorage.getItem("role") === CLIENT;
const isLoggedIn = () => !!localStorage.getItem("token");

class App extends Component {
  render() {
    return (
      <Router>
        {isLoggedIn() ? isClient() ? <UserNavbar /> : <Navbar /> : <div />}
        <Switch>
          <PrivateRoute
            exact
            path="/"
            component={isClient() ? UserPrincipal : Principal}
          />
          <PrivateRoute path="/login" component={Login} />
          <PrivateRoute path="/checkin" component={CheckIn} />
          <PrivateRoute path="/reservation" component={Reservation} />
          <PrivateRoute path="/registration" component={Registration} />
          <PrivateRoute path="/checkout" component={CheckOut} />
          <PrivateRoute path="/reservations" component={Reservations} />
          <Route exact path="/products/:id" component={UserProducts} />
          <Route exact path="/products" component={Products} />
          <PrivateRoute
            exact
            path="/products/newProduct"
            component={NewProduct}
          />
          <PrivateRoute path="/orders" component={Orders} />
          <Route
            path="/help/:id"
            component={isClient() ? UserHelp : HelpRequest}
          />
          <Route path="/expenses/:id" component={UserExpenses} />
          <Route>
            <NotFound />
          </Route>
        </Switch>
      </Router>
    );
  }
}

export default App;
