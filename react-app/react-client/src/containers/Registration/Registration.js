import React, {useEffect, useState} from "react";
import { Container, Row, Col } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";
import { useTranslation } from "react-i18next";


import Button from "../../components/Button/Button";
import Input from "../../components/Input/Input";
import { registerOccupants } from "../../api/roomApi";

import Modal from 'react-bootstrap/Modal'
import {useQuery} from "../../utils/hooks/useQuery";


const useStyles = makeStyles((theme) => ({
  container: {
    background: "#FAF6FC",
    height: "100vh",
  },
  buttonColLeft: {
    textAlign: "right",
  },
  buttonColRight: {
    textAlign: "center",
  },
  buttonRow: {
    paddingTop: "20px",
    textAlign: "center",
    width: "100%",
  },
}));



const emptyOccupantCopy = () => ({ firstName: "", lastName: "" });


const addOccupantDiv = (
  index,
  { firstName, lastName },
  classes,
  onFirstNameChange,
  onLastNameChange
) => (
  <div key={index}>
    <Row className={classes.buttonRow}>
      <Col xs={6} md={3} />
      <Col>
        <Input
          label="First name"
          value={firstName}
          onChange={onFirstNameChange}
        />
      </Col>
      <Col>
        <Input
          label="Last Name"
          value={lastName}
          onChange={onLastNameChange}
        />
      </Col>
      <Col xs={6} md={3} />
    </Row>
  </div>
);

const isEmpty = (string) => string === undefined || string.length === 0;

// FIXME add more validation to each occupant
const hasEmptyOccupant = (occupants) =>
  occupants.length === 1 &&
  isEmpty(occupants[0].firstName) &&
  isEmpty(occupants[0].lastName);

const registration = ({ history }) => {
  const classes = useStyles();
  const { t } = useTranslation();
  const query = useQuery();
  const hash = query.get("hash");

  const [reservationId, onReservationId] = useState("");
  const [submit, onSubmit] = useState(false);
  const [occupants, addOccupant] = useState([emptyOccupantCopy()]);
  const [showMessage, setShowMessage] = useState(false);

  useEffect(() => {
    if (hash) {
      onReservationId(hash);
      console.log("hola")
    }
  }, []);

  const handleCloseMessage = () => setShowMessage(false);

  const onChangeReservationId = (newReservationId) => {
    onReservationId(newReservationId.target.value);
  };

  const registrationSubmit = () => {
    onSubmit(true);
    const filteredOccupants = occupants.filter(
      (occupant) => !isEmpty(occupant.firstName) && !isEmpty(occupant.lastName)
    );
    registerOccupants({ occupants: filteredOccupants }, reservationId)
      .then((response) => {
        history.push("/");
      })
      .catch((error) => setShowMessage(true));
  };

  const back = () => {
    history.push("/");
  };

  const onAddOccupant = () => addOccupant([...occupants, emptyOccupantCopy()]);

  const onOccupantChange = (field) => (index) => (event) => {
    occupants[index][field] = event.target.value;
    addOccupant([...occupants]);
  };

  const onFirstNameChange = onOccupantChange("firstName");
  const onLastNameChange = onOccupantChange("lastName");

  return (
    <div>
      <Container fluid="md" className={classes.container}>
        <Row style={{ width: "100%" }}>
          <Col xs={6} md={3}/>
          <Col>
            <Row className={classes.buttonRow}>
              <Col style={{ marginBottom: "5px" }}>
                <Input
                  value={hash}
                  label={t("reservation.number")}
                  onChange={onChangeReservationId}
                />
              </Col>
              <Col className={classes.buttonColLeft}>
                <Button
                  ButtonType="Save"
                  onClick={registrationSubmit}
                  ButtonText={t("register")}
                  disabled={
                    submit || hasEmptyOccupant(occupants) || !reservationId
                  }
                />
              </Col>
              <Col className={classes.buttonColRight}>
                <Button
                  ButtonType="Back"
                  onClick={back}
                  ButtonText={t("cancel")}
                />
              </Col>
            </Row>
            <Row className={classes.buttonRow}>
              <Col className={classes.buttonColRight}>
                <Button
                  ButtonType="Save"
                  onClick={onAddOccupant}
                  ButtonText={t("registrations.addOccupant")}
                />
              </Col>
            </Row>
          </Col>
          <Col xs={6} md={3} />
        </Row>
        {occupants.map((occupant, index) =>
          addOccupantDiv(
            index,
            occupant,
            classes,
            onFirstNameChange(index),
            onLastNameChange(index)
          )
        )}
        <Modal centered show={showMessage} onHide={handleCloseMessage}>
          <Modal.Body>
            <Row>
              <Col xs={1} sm={1}/>
              <Col xs={10} sm={10}>
                <h4>{t("something_happened")}</h4>
              </Col>
              <Col xs={1} sm={1}/>
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

export default withRouter(registration);
