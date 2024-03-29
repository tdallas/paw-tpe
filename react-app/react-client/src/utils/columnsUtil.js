import moment from "moment";


export const busyRoomsColumns = [
    {id: "id", label: "room.singular"},
    {
        id: "startDate",
        label: "room.room.from",
        format: (date) => {
            return moment(date).format("DD/MM/YYYY");
        },
    },
    {
        id: "endDate",
        label: "room.room.until",
        format: (date) => moment(date).format("DD/MM/YYYY"),
    },
    {id: "userEmail", label: "room.room.owner"},
];


export const helpListColumns = [
    {id: "id", label: "help.request.plural"},
    {id: "helpStep", label: "help.status.message"},
    {id: "helpText", label: "help.text.singular"},
    {id: "roomNumber", label: "room.singular"},
    {id: "actions", label: "help.resolve", isButton: true}
];


export const ordersColumns = [
    {id: "roomNumber", label: "room.singular"},
    {id: "description", label: "product.description"},
    {id: "action", label: "order.send", isButton: true}
];


export const productsColumns = [
    {id: "description", label: "product.description"},
    {id: "price", label: "user.price"},
    {id: "toggle", label: "product.enabled", isButton: true}
];


export const reservationUserColumns = [
    {id: "roomType", label: "reservation.room.type"},
    {
        id: "startDate",
        label: "reservation.date.start",
        format: (date) => {
            return moment(date).format("DD/MM/YYYY");
        },
    },
    {
        id: "endDate",
        label: "reservation.date.end",
        format: (date) => moment(date).format("DD/MM/YYYY"),
    },
    {id: "roomNumber", label: "reservation.room.number"},
    {id: "actions", label: "user.actions", isButton: true},
    {id: "expenses", label: "user.expenses", isButton: true},
    {id: "help", label: "user.problem", isButton: true},
];


export const reservationsColumns = [
    {id: "hash", label: "reservation.id"},
    {id: "userEmail", label: "reservation.email"},
    {
        id: "startDate", label: "reservation.date.start", format: (date) => {
            return moment(date).format("DD/MM/YYYY");
        },
    },
    {
        id: "endDate", label: "reservation.date.end", format: (date) => {
            return moment(date).format("DD/MM/YYYY");
        },
    },
    {id: "isActive", label: "reservation.isActive"}
]


export const rateColumns = [
  {id: "roomNumber", label: "RoomNumber"},
  {id: "userEmail", label: "reservation.email"},
  {
      id: "startDate", label: "reservation.date.start", format: (date) => {
          return moment(date).format("DD/MM/YYYY");
      },
  },
  {
      id: "endDate", label: "reservation.date.end", format: (date) => {
          return moment(date).format("DD/MM/YYYY");
      },
  },
  {id: "calification", label: "Rate"}
]
