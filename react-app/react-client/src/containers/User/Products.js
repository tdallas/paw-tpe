import React, { useState } from "react";
import { Container, Row, Col, CardDeck } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";

import Card from "../../components/Card/Card";
import { getAllProducts } from "../../api/productApi";
import { buyProduct } from "../../api/userApi";
import InfoSimpleDialog from '../../components/Dialog/SimpleDialog';
import { useTranslation } from "react-i18next";



const useStyles = makeStyles((theme) => ({
  container: {
    background: "#FAF6FC",
    paddingTop: "40px",
    maxWidth: "100%",
    paddingLeft: "40px",
    paddingRight: "40px",
    display: "flex",
    justifyContent: "center",
  },
}));

const UserProducts = ({ match, history }) => {
  const classes = useStyles();

  const reservationId = match.params.id;

  const [products, setProducts] = useState([]);
  const [showDialog, updateShowDialog] = useState(false);
  const [loading, updateShowLoading] = useState(false);
  const [info, updateInfo] = useState(undefined);

  const { t } = useTranslation();


  const onSubmitBuy = (productId) => {
    updateShowLoading(true);
    buyProduct({ reservationId, productId },{})
      .then((response) => {
        updateShowLoading(false);
        // call show dialog in InfoSimpleDialog
        updateShowDialog(true);
        // send result to dialog window to show it
        updateInfo("compraste coquita");
      })
      .catch((error) => {
        updateShowLoading(false);
        updateShowDialog(true);
        updateInfo(undefined);
      });
  }

  const handleDialogClose = () => {
    updateShowDialog(false);
  }

  products.length === 0 &&
    getAllProducts()
      .then((response) => setProducts(response.data));

  return (
    <Container className={classes.container}>
      <InfoSimpleDialog open={loading} title={t('loading')} />
                            <InfoSimpleDialog open={showDialog} onClose={handleDialogClose} title={info ? t('reservation.checkin.successful') : ''}>
                                {info ? <div>
                                   <div>Compre Coquita</div>
                                </div> : <div>{t('reservation.checkin.error')}</div>}
                            </InfoSimpleDialog>
      <Row style={{ background: "#FAF6FC", width: '100%' }}>
        <CardDeck style={{ justifyContent: "center", background: "#FAF6FC", width: "100vw" }}>
          {products.map(({ id, description, price }) => (
            <Card
              id={id}
              name={description}
              price={"$" + price}
              reservationId={reservationId}
              onClick={() => onSubmitBuy(id)}
            />
          ))}
        </CardDeck>
      </Row>
    </Container>
  );
};

export default withRouter(UserProducts);
