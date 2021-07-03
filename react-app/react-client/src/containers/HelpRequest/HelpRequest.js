import React, { useState } from "react";
import { Container, Row, Col } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";
import { LinearProgress } from "@material-ui/core";
import Button from "../../components/Button/Button";
import Table from "../../components/Table/Table";
import { useTranslation } from "react-i18next";
import { getAllHelpRequests } from "../../api/helpApi";
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
  const [tableInfo, setTableInfo] = useState({ helpList: [], totalCount: 0 });
  const { helpList, totalCount } = tableInfo;

  const handleCloseMessage = () => setShowMessage(false);
  const [showMessage, setShowMessage] = useState(false);
  const [loading, setLoading] = useState(false);

  const getAllHelpRequestsUnsolved = (page, limit) => {
    setLoading(true);
    getAllHelpRequests({ page, limit })
      .then((response) => {
        setTableInfo({
          helpList: response.data,
          totalCount: +response.headers["x-total-count"],
        });
        setLoading(false);
      })
      .catch(() => setShowMessage(true) && setLoading(false));
  };

  const onSubmitHelpRequest = () => {
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
              rows={helpList}
              totalItems={totalCount}
              pageFunction={getAllHelpRequestsUnsolved}
            />
          </Col>
          <Col xs={12} md={2}>
            <Col xs={12} md={6} style={{ textAlign: "left" }}>
              <Button
                ButtonType="Save"
                size="large"
                onClick={onSubmitHelpRequest}
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

export default withRouter(HelpRequest);
