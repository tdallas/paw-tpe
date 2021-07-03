import React, { useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";
import { useTranslation } from "react-i18next";
import { LinearProgress } from "@material-ui/core";

import { getAllReservations } from "../../api/userApi";
import { reservationUserColumns } from "../../utils/columnsUtil";
import Table from "../../components/Table/Table";
import Button from "../../components/Button/Button";

import Modal from 'react-bootstrap/Modal'


const useStyles = makeStyles((theme) => ({
  container: {
    background: "#FAF6FC",
    height: "100vh",
  },
}));

const UserPrincipal = ({ history }) => {
  const { t } = useTranslation();

  const classes = useStyles();
  const [reservations, setReservations] = useState([]);

  const handleCloseMessage = () => setShowMessage(false);
  const [showMessage, setShowMessage] = useState(false);
  const [loading, setLoading] = useState(false);

  const getMyReservations = () => {
    setLoading(true);
    getAllReservations().then((response) => {
      setReservations(
        response.data.activeReservations.map(
          ({ roomType, startDate, endDate, roomNumber, reservationId }) => {
            return Object.assign(
              {},
              { roomType, startDate, endDate, roomNumber },
              {
                actions: () => history.push(`/products/${reservationId}`),
                expenses: () => history.push(`/expenses/${reservationId}`),
                help: () => history.push(`/help/${reservationId}`),
              }
            );
          }
        )
      );
      setLoading(false);
    })
      .catch((response) => setShowMessage(true) && setLoading(false));
  }

  return (
    <div>
      <Container fluid="md" className={classes.container}>
        <Row
          className="justify-content-sm-center"
          style={{ paddingTop: "40px", width: "100%" }}
        >
          <Col xs={1} md={1} />
          <Col xs={10} md={10}>
          {loading && <LinearProgress />}
            <Table
              columns={reservationUserColumns}
              rows={reservations}
              totalItems={0}
              pageFunction={getMyReservations}
            />
          </Col>
          <Col xs={1} md={1} />
        </Row>
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
            <Button ButtonType="Save" size="large" onClick={handleCloseMessage}
              ButtonText={t("accept")} />
          </Modal.Footer>
        </Modal>
      </Container>
    </div>
  );
};

export default withRouter(UserPrincipal);
