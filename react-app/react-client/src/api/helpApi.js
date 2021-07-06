import {get, post} from "./baseApi";

const baseRoomURL = "/help";

const getAllHelpRequestsUrl = baseRoomURL + "";
const aHelpRequestUrl = id => baseRoomURL + `/${id}`;

export const getAllHelpRequests = async (params) => get(getAllHelpRequestsUrl, params);
export const getAHelpRequest = async (id) => get(aHelpRequestUrl(id));
export const markHelpRequestResolved = async (id) => post(aHelpRequestUrl(id));
