import React, { useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";
import { LinearProgress } from "@material-ui/core";

import Button from "../../components/Button/Button";
import Table from "../../components/Table/Table";
import { useTranslation } from "react-i18next";
import { ordersColumns } from "../../utils/columnsUtil";
import { getAllUndeliveredOrders, sendOrderToRoom } from "../../api/roomApi";
import InfoSimpleDialog from "../../components/Dialog/SimpleDialog";

import Modal from "react-bootstrap/Modal";

const useStyles = makeStyles((theme) => ({
  container: {
    background: "#FAF6FC",
    height: "100vh",
    maxWidth: "100%",
    padding: 0,
  },
  row: {
    paddingTop: "40px",
    paddingLeft: "10%",
    paddingRight: "10%",
    width: "100%",
  },
  tableCol: {
    paddingRight: "7.5%",
    paddingLeft: "7.5%",
  },
}));

const Orders = ({ history }) => {
  const classes = useStyles();
  const { t } = useTranslation();
  const [orders, setOrders] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const [showDialog, updateShowDialog] = useState(false);
  const [info, updateInfo] = useState(undefined);
  const [loading, setLoading] = useState(false);

  const handleCloseMessage = () => setShowMessage(false);
  const [showMessage, setShowMessage] = useState(false);

  const getUndeliveredOrders = (page, limit) => {
    setLoading(true);
    getAllUndeliveredOrders({ page, limit })
      .then((response) => {
        // This map-reduce will take the response and make an object like:
        // { roomNumber : { roomId, [ name , amount ] } }
        // This is so orders are grouped by room number
        const dataByRoom = response.data.reduce((acc, charge) => {
          let key = charge["roomNumber"];
          if (!acc[key]) {
            acc[key] = [];
            acc[key].push({ roomId: charge["roomId"], chargesInfo: [] });
          }
          // acc[key][0]['chargesInfo'].push({chargeId: charge['chargeId'], description: charge['description']});
          acc[key][0]["chargesInfo"].push({
            description: charge["description"],
          });
          return acc;
        }, {});
        const dataByRoomArray = Object.keys(dataByRoom).map((key) => [
          Number(key),
          dataByRoom[key],
        ]);
        dataByRoomArray.forEach((room) => {
          room[1][0].chargesInfo = room[1][0].chargesInfo.reduce(
            (acc, product) => {
              let key = product.description;
              if (!acc[key]) {
                acc[key] = [];
                acc[key].push({ amount: 0 });
              }
              acc[key][0].amount++;
              return acc;
            },
            {}
          );
        });

        // set total count
        setTotalCount(dataByRoomArray.length);

        setOrders(
          dataByRoomArray.map((roomOrders) => {
            let productsListObj = roomOrders[1][0].chargesInfo;
            return Object.assign(
              {},
              {
                id: roomOrders[1][0].roomId,
                roomNumber: roomOrders[0],
                description: Object.keys(productsListObj).map((key) => [
                  key,
                  +productsListObj[key][0].amount,
                ]),
              },
              {
                action: () => {
                  setOrdersDelivered(`${+roomOrders[1][0].roomId}`);
                },
              }
            );
          })
        );
        setLoading(false);
      })
      .catch(
        (response) =>
          setShowMessage(true) && setLoading(false)
      );
  };

  const setOrdersDelivered = (id) => {
    setLoading(true);
    sendOrderToRoom(id)
      .then((response) => {
        // call show dialog in InfoSimpleDialog
        updateShowDialog(true);
        // send result to dialog window to show it
        updateInfo(response.data ? response.data : t("order.deliver"));
        setLoading(false);
      })
      .catch((error) => {
        updateShowDialog(true);
        updateInfo(undefined);
        setLoading(false);
      });
  };

  const onSubmitOrders = () => {
    getUndeliveredOrders();
    updateShowDialog(false);
  };

  const back = () => {
    history.push("/");
  };

  return (
    <div>
      <Container className={classes.container}>
        <Row className={classes.row}>
          <Col xs={12} md={10} className={classes.tableCol}>
            {loading && <LinearProgress />}
            <Table
              columns={ordersColumns}
              rows={orders}
              totalItems={totalCount}
              pageFunction={getUndeliveredOrders}
            />
          </Col>
          <Col xs={12} md={2}>
            <Col xs={12} md={6} style={{ textAlign: "left" }}>
              <Button
                ButtonType="Save"
                size="large"
                onClick={onSubmitOrders}
                ButtonText={t("refresh")}
              />
            </Col>
            <Col xs={12} md={6} style={{ textAlign: "left" }}>
              <Button
                ButtonType="Back"
                size="large"
                onClick={back}
                ButtonText={t("home")}
              />
            </Col>
          </Col>
        </Row>
        <InfoSimpleDialog open={showDialog} onClose={onSubmitOrders}>
          {info ? <div>{t("order.deliver")}</div> : <div>{t("error.500")}</div>}
        </InfoSimpleDialog>

        <Modal centered show={showMessage} onHide={handleCloseMessage}>
          <Modal.Body>
            <Row>
              <Col xs={1} sm={1}></Col>
              <Col xs={10} sm={10}>
                <h4>{t("something_happened")}</h4>
              </Col>
              <Col xs={1} sm={1}></Col>
            </Row>
          </Modal.Body>
          <Modal.Footer>
            <Button
              ButtonType="Save"
              size="large"
              onClick={handleCloseMessage}
              ButtonText={t("accept")}
            />
          </Modal.Footer>
        </Modal>
      </Container>
    </div>
  );
};

export default withRouter(Orders);
