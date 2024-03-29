import axios from "axios";

const options = (token) => {
  if (token) {
    return {
      headers: {
        "Content-Type": "application/json;charset=UTF-8",
        Authorization: `Bearer ${token}`
      }
    }
  } else {
    return {
      headers: {
        "Content-Type": "application/json;charset=UTF-8"
      }
    }
  }
}

// FIXME add to configs
export const baseURL = "http://localhost:8080/api";

// export const baseURL = "/paw-2019b-2/api";


export const post = async (url, body) =>
  axios.post(baseURL + url, body, options(localStorage.getItem("token")));

export const put = async (url, params, body) =>
  axios.put(baseURL + url, body,
    Object.assign({}, {params}, options(localStorage.getItem("token")))
  );

export const get = async (url, params) =>
  axios.get(baseURL + url,
    Object.assign({}, { params }, options(localStorage.getItem("token")))
  );
