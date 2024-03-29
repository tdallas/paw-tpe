import React, { useState } from "react";
import { Container, Row, Col, Form } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";
import { useTranslation } from "react-i18next";

import Button from "../../components/Button/Button";
import Input from "../../components/Input/Input";

import { uploadProductFile, addProduct } from "../../api/productApi";

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
  buttonCol: {
    textAlign: "center",
  },
}));

const NewProduct = ({ history }) => {
  const classes = useStyles();

  const [description, setDescription] = useState("");
  // const [photo, setPhoto] = useState("");
  const [photoId, setPhotoId] = useState(undefined);
  const [price, setPrice] = useState("");
  const [errorDescription, setErrorDescription] = useState(false);
  const [errorPrice, setErrorPrice] = useState(false);
  const [errorMessagePrice, setErrorMessagePrice] = useState("");
  const { t } = useTranslation();

  const handleCloseMessage = () => setShowMessage(false);
  const [showMessage, setShowMessage] = useState(false);

  const descriptionOnChange = (description) => {
    setDescription(description.target.value);
  };

  const photoOnChange = (newPhoto) => {
    const file = newPhoto.target.files[0];
    const data = new FormData();
    data.append("file", file);

    uploadProductFile(data)
      .then((response) => {
        setPhotoId(response.data.productImageId);
      })
      .catch((response) => setShowMessage(true));
    // setPhoto(newPhoto.target.value);
  };

  const priceOnChange = (newPrice) => {
    setPrice(newPrice.target.value);
  };

  const formIsValidate = () => {
    let isOk = true;
    if (description.length === 0) {
      setErrorDescription(true);
      isOk = false;
    }
    if (price.length === 0) {
      setErrorPrice(true);
      setErrorMessagePrice(t("required"));
      isOk = false;
    } else if (price <= 0) {
      setErrorPrice(true);
      setErrorMessagePrice(t("product.errorMessagePrice"));
      isOk = false;
    }

    return isOk;
  };

  const back = () => {
    history.push("/products");
  };

  const onSubmitProduct = () => {
    if (!formIsValidate()) return;
    else {
      addProduct({ productImageId: photoId, description, price })
        .then(() => history.push("/products"))
        .catch((response) => setShowMessage(true));
    }
  };

  return (
    <div>
      <Container className={classes.container}>
        <Row className={classes.row}>
          <Col xs={12} md={4} style={{ justifyContent: "center" }}>
            <Input
              label={t("product.description")}
              type="text"
              error={errorDescription}
              helperText={errorDescription && "El campo es requerido"}
              required={true}
              onChange={descriptionOnChange}
            />
          </Col>
          <Col xs={12} md={4} style={{ justifyContent: "center" }}>
            <Form.Group>
              <Form.File
                id="exampleFormControlFile1"
                label={t("product.img")}
                onChange={photoOnChange}
              />
            </Form.Group>
          </Col>
          <Col xs={12} md={4} style={{ justifyContent: "center" }}>
            <Input
              label={t("product.price")}
              type="number"
              onChange={priceOnChange}
              error={errorPrice}
              helperText={errorPrice && errorMessagePrice}
              required={true}
            />
          </Col>
        </Row>
        <Row className={classes.row}>
          <Col xs={12} md={6} style={{ textAlign: "right" }}>
            <Button
              ButtonType="Save"
              onClick={onSubmitProduct}
              ButtonText={t("product.create")}
            ></Button>
          </Col>
          <Col xs={12} md={6}>
            <Button
              ButtonType="Back"
              onClick={back}
              ButtonText={t("cancel")}
            ></Button>
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

export default withRouter(NewProduct);
