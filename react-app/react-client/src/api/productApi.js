import {baseURL, post, get, put} from "./baseApi";

const baseProductsURL = "/products";

const addProductFileUrl = baseProductsURL + "/upload-file";
const getProductFileUrl = (id) => baseProductsURL + "/" + id + "/img";
const addProductUrl = baseProductsURL;
const enableProductUrl = (id) => baseProductsURL + "/" + id + "/enable";
const disableProductUrl = (id) => baseProductsURL + "/" + id + "/disable";
const findAllProductsUrl = baseProductsURL;

export const uploadProductFile = async (body) => post(addProductFileUrl, body);
export const addProduct = async (body) => post(addProductUrl, body);
export const enableProduct = async (id) => put(enableProductUrl(id), null, { enabled: true });
export const disableProduct = async (id) => put(disableProductUrl(id), null, { enabled: false });
export const getProductFile = (id) => baseURL + getProductFileUrl(id);
export const getAllProducts = async (params) => get(findAllProductsUrl, params);
