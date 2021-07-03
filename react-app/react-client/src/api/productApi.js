import {baseURL, post, get, put} from "./baseApi";

const baseProductsURL = "/products";
const baseProductImagesURL = "/productImgs"

const addProductFileUrl = baseProductImagesURL;
const getProductFileUrl = (id) => baseProductImagesURL + "/" + id;
const addProductUrl = baseProductsURL;
const enableDisableProductUrl = (id) => baseProductsURL + "/" + id;
const findAllProductsUrl = baseProductsURL;

export const uploadProductFile = async (body) => post(addProductFileUrl, body);
export const addProduct = async (body) => post(addProductUrl, body);
export const enableProduct = async (id) => put(enableDisableProductUrl(id), null, { enabled: true });
export const disableProduct = async (id) => put(enableDisableProductUrl(id), null, { enabled: false });
export const getProductFile = (id) => baseURL + getProductFileUrl(id);
export const getAllProducts = async (params) => get(findAllProductsUrl, params);
