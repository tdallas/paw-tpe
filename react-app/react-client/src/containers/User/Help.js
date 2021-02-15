import React, { useState } from "react";
import { Container, Row, Col, Form } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";

import { requestHelp } from "../../api/userApi";

import Button from "../../components/Button/Button";
import UserNavbar from '../../components/Navbar/UserNavbar'

const useStyles = makeStyles((theme) => ({
    container: {
        background: "#FAF6FC",
        height: "100vh",
    },
}));


const UserHelp = (props, { history }) => {
    const classes = useStyles();

    const [help, setHelp] = useState("");

    const onHelpChange = (newHelp) => {
        setHelp(newHelp.target.value);
    }

    const onSubmitHelp = (helpDescription) => () => {
            requestHelp(props.match.params.id,{helpDescription})
                .then((response) => {
                    console.log(response.status);
          })
          .catch((error) => {
            console.log("there was an error trying to generate a help request", error);
          });
    }

    const back = () => {
        history.push("/userprincipal");
    }

    return (
        <div>
            <Container fluid="md" className={classes.container}>
                <UserNavbar />
                <Row className="justify-content-sm-center" style={{ paddingTop: '40px' }}>
                    <Col xs={1} md={2}></Col>
                    <Col xs={9} md={7}>
                        <Form.Group controlId="exampleForm.ControlTextarea1">
                            <Form.Label>Write what you need</Form.Label>
                            <Form.Control as="textarea" rows={7} onChange={onHelpChange} />
                        </Form.Group>
                    </Col>
                    <Col xs={1} md={1}>
                        <Col xs={12} md={6} style={{ textAlign: 'left' }}>
                            <Button ButtonType="Save" size="large" onClick={onSubmitHelp({
                                helpDescription: help
                            })} ButtonText="Enviar"></Button>
                        </Col>
                        <Col xs={12} md={6} style={{ textAlign: 'left' }}>
                            <Button ButtonType="Back" size="large" onClick={back} ButtonText="Volver"></Button>
                        </Col>
                    </Col>
                    <Col xs={1} md={2}></Col>
                </Row>
                <Row>

                </Row>
            </Container>
        </div>
    );
};

export default withRouter(UserHelp);
