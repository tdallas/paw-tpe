import React from 'react';
import { Container, Row, Col, Card } from 'react-bootstrap'
import { makeStyles } from '@material-ui/core/styles';

import Navbar from '../../components/Navbar/Navbar'
import Button from '../../components/Button/Button'
import Input from '../../components/Input/Input'


const useStyles = makeStyles((theme) => ({
    container: {
        background: '#FAF6FC',
        height: '100vh'
    },
    buttonColLeft: {
        textAlign: 'right',
    },
    buttonColRight: {
        textAlign: 'center',
    },
    buttonRow: {
        marginTop: '20px',
        textAlign: 'center'
    },
    card: {
        marginTop: '40px',
    }
}));


const checkIn = (props) => {
    const classes = useStyles();

    return (
        <div>
            <Container fluid="md" className={classes.container}>
                <Row>
                    <Col>
                        <Navbar></Navbar>
                    </Col>
                </Row>
                <Row>
                    <Col xs={6} md={3}></Col>
                    <Col>
                    <Card className={classes.card}>
                        <Row className={classes.buttonRow}>
                            <Col style={{marginBottom: '5px'}}>
                                <Input label="Reservation Id"></Input>
                            </Col>
                            <Col className={classes.buttonColLeft}>
                                <Button ButtonType="Save" ButtonText="Accept"></Button>
                            </Col>
                            <Col className={classes.buttonColRight}>
                                <Button ButtonType="Back" ButtonText="Cancel"></Button>
                            </Col>
                        </Row>
                    </Card>
                    </Col>
                    <Col xs={6} md={3}></Col>
                </Row>
            </Container>
        </div>
    );
}

export default checkIn;