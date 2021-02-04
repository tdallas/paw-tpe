import React, { useState } from "react";
import { Container, Row, Col } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";


import Navbar from "../../components/Navbar/Navbar";
import Button from "../../components/Button/Button";
import Table from '../../components/Table/Table'


const useStyles = makeStyles((theme) => ({
    container: {
        background: "#FAF6FC",
        height: "100vh",
        maxWidth: "100%",
        padding: 0,
    },
    row: {
        marginTop: "40px",
        paddingLeft: "10%",
        paddingRight: "10%",
    },
    tableCol: {
        paddingRight: "7.5%",
        paddingLeft: "7.5%",
    },
}));

const HelpRequest = ({ history }) => {
    const classes = useStyles();

    const onSubmitHelpRequest = () => {
        console.log(history);
        history.push("/helpRequest")
    }

    const back = () => {
        history.push("/");
    }

    return (
        <div>
            <Container className={classes.container}>
                <Row>
                    <Col>
                        <Navbar></Navbar>
                    </Col>
                </Row>
                <Row className={classes.row}>
                    <Col xs={12} md={10} className={classes.tableCol}>
                        <Table></Table>
                    </Col>
                    <Col xs={12} md={2}>
                        <Col xs={12} md={6} style={{ textAlign: 'left' }}>
                            <Button ButtonType="Save" size="large" onClick={onSubmitHelpRequest} ButtonText="Refresh"></Button>
                        </Col>
                        <Col xs={12} md={6} style={{ textAlign: 'left' }}>
                            <Button ButtonType="Back" size="large" onClick={back} ButtonText="Volver"></Button>
                        </Col>
                    </Col>
                </Row>
            </Container>
        </div>
    );
};

export default withRouter(HelpRequest);