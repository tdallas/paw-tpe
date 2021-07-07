import React, { useState } from "react";
import { Container, Row, Col } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";
import { LinearProgress } from "@material-ui/core";
import Button from "../../components/Button/Button";
import Table from "../../components/Table/Table";
import { useTranslation } from "react-i18next";
import { getAllHelpRequests, markHelpRequestResolved } from "../../api/helpApi";
import { helpListColumns } from "../../utils/columnsUtil";

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

const HelpRequest = ({ history }) => {
  const classes = useStyles();
  const { t } = useTranslation();
  const [tableInfo, setTableInfo] = useState([]);

  const { totalCount } = tableInfo;

  const [showMessage, setShowMessage] = useState(false);
  const [message, setMessage] = useState(t("something_happened"));
  const [loading, setLoading] = useState(false);

  const handleCloseMessage = () => {
    setShowMessage(false);
  };

  const onActionHandler = (id) => {
    setLoading(true);
    markHelpRequestResolved(id)
      .then(() => {
        setLoading(false);
        setMessage(t("help.status.update"));
        getAllHelpRequestsUnsolved(1, 20);
      })
      .catch(() => setShowMessage(true) && setLoading(false));
  }

  const getAllHelpRequestsUnsolved = (page, limit) => {
    setMessage(t("something_happened"))
    setLoading(true);
    getAllHelpRequests({ page, limit })
      .then((response) => {
        setTableInfo(
          response.data.map(elem => {
            return Object.assign(
              {},
              elem,
              {
                actions: () => onActionHandler(elem.id)
              }
            )
          })
        );
        setLoading(false);
      })
      .catch(() => setShowMessage(true) && setLoading(false));
  };

  const refreshHelpRequestsList = () => {
    getAllHelpRequestsUnsolved();
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
              columns={helpListColumns}
              rows={tableInfo}
              totalItems={totalCount}
              pageFunction={getAllHelpRequestsUnsolved}
            />
          </Col>
          <Col xs={12} md={2}>
            <Col xs={12} md={6} style={{ textAlign: "left" }}>
              <Button
                ButtonType="Save"
                size="large"
                onClick={refreshHelpRequestsList}
                ButtonText={t("refresh")}
              />
            </Col>
            <Col xs={12} md={6} style={{ textAlign: "left" }}>
              <Button
                ButtonType="Back"
                size="large"
                onClick={back}
                ButtonText={t("back")}
              />
            </Col>
          </Col>
        </Row>

        <Modal centered show={showMessage} onHide={handleCloseMessage}>
          <Modal.Body>
            <Row>
              <Col xs={1} sm={1}/>
              <Col xs={10} sm={10}>
                <h4>{message}</h4>
              </Col>
              <Col xs={1} sm={1}/>
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

export default withRouter(HelpRequest);
