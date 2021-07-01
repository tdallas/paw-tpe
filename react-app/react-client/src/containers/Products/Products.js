import React, { useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";

import Button from "../../components/Button/Button";
import Table from "../../components/Table/Table";
import { useTranslation } from "react-i18next";
import {
  disableProduct,
  enableProduct,
  getAllProducts,
} from "../../api/productApi";
import { productsColumns } from "../../utils/columnsUtil";

import Modal from 'react-bootstrap/Modal'


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

const Products = ({ history }) => {
  const classes = useStyles();
  const { t } = useTranslation();
  const [products, setProducts] = useState([]);
  const [totalCount, setTotalCount] = useState(0);

  const handleCloseMessage = () => setShowMessage(false);
  const [showMessage, setShowMessage] = useState(false);

  const getProducts = (page, limit) => {
    getAllProducts({ page, limit }).then((response) => {
      setProducts(
        response.data.map(({ id, description, price, file, enabled }) => {
          return Object.assign(
            {},
            { file, description, price, enabled },
            { toggle: () => toggleProductEnabled(enabled, id) }
          );
        })
      );
      setTotalCount(+response.headers["x-total-count"]);
    })
      .catch((response) => setShowMessage(true));
  };

  const toggleProductEnabled = (toggleBoolean, id) => {
    if (toggleBoolean) {
      disableProduct(id).then(() => { getProducts(); }).catch((response) => setShowMessage(true));
    } else {
      enableProduct(id).then(() => { getProducts(); }).catch((response) => setShowMessage(true));
    }
  };

  const addProduct = () => {
    history.push("/product/newProduct");

    console.log(history, "history");
  };

  const back = () => {
    history.push("/");
  };

  return (
    <div>
      <Container className={classes.container}>
        <Row className={classes.row}>
          <Col xs={12} md={10} className={classes.tableCol}>
            <Table
              columns={productsColumns}
              rows={products}
              totalItems={totalCount}
              pageFunction={getProducts}
            />
          </Col>
          <Col xs={12} md={2}>
            <Col xs={12} md={6} style={{ textAlign: "left" }}>
              <Button
                ButtonType="Save"
                size="large"
                onClick={addProduct}
                ButtonText={t("product.add")}
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
            <Button ButtonType="Save" size="large" onClick={handleCloseMessage}
              ButtonText={t("accept")} />
          </Modal.Footer>
        </Modal>
      </Container>
    </div>
  );
};

export default withRouter(Products);
