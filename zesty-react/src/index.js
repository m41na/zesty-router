import React from "react";
import { hydrate } from "react-dom";
import App from "./App";
import { BrowserRouter as Router } from "react-router-dom";
import products from "./store";

const initialState = window.__PRELOADED_STATE__ || products;

hydrate(
  <Router>
    <App products={initialState} />
  </Router>,
  document.getElementById("root")
);
