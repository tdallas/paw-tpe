import React, {useEffect, useState} from "react";
import { Container, Row, Col } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";
import { useTranslation } from "react-i18next";

import {doCheckout} from '../../api/roomApi';


import Button from "../../components/Button/Button";
import ExpensesTable from "../../components/ExpensesTable/ExpensesTable";
import InfoSimpleDialog from "../../components/Dialog/SimpleDialog";

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
        width: "100%"
    },
    tableCol: {
        paddingRight: "7.5%",
        paddingLeft: "7.5%",
    },
}));

const CheckOutExpenses = ({ history, match }) => {
    const classes = useStyles();
    const { t } = useTranslation();
    const [expenses, setExpenses] = useState([]);
    const [showDialog, updateShowDialog] = useState(false);
    const [loading, updateShowLoading] = useState(false);
    const [info, updateInfo] = useState(undefined);

    const checkingOut = () => {
        updateShowLoading(true);
        doCheckout({},match.params.reservationId)
            .then((response) => {
                updateShowLoading(false);
                setExpenses(
                    response.data.map(
                        ({ productDescription, productAmount, productPrice }) => ({
                            productDescription,
                            productAmount,
                            productPrice,
                        })
                    )
                );
                updateInfo(response.data.size);
            })
            .catch(() => {
                updateInfo(undefined);
                updateShowLoading(false);
                updateShowDialog(true);
            });
    }

    useEffect(() => {checkingOut()}, []);

    const handleErrorDialogClose = () => {
        updateShowDialog(false);
        history.push("/checkout");
    }

    const back = () => {
        history.push("/");
    };

    return (
        <div>
            <Container className={classes.container}>
                <Row className={classes.row}>
                    <Col xs={12} md={10} className={classes.tableCol}>
                        <ExpensesTable rows={expenses}/>
                    </Col>
                    <Col xs={12} md={2}>
                        <Col xs={12} md={12} style={{ textAlign: "left" }}>
                            <Button
                                ButtonType="Back"
                                size="large"
                                onClick={back}
                                ButtonText={t("user.home")}
                            />
                        </Col>
                    </Col>
                </Row>
                <InfoSimpleDialog open={loading}>
                    <div>
                        <div>{t('loading')}</div>
                    </div>
                </InfoSimpleDialog>
                <InfoSimpleDialog open={showDialog} onClose={handleErrorDialogClose}>
                    <div>
                        <div>{info ? '' : t('reservation.checkout.error')}</div>
                    </div>
                </InfoSimpleDialog>
            </Container>
        </div>
    );
};

export default withRouter(CheckOutExpenses);
