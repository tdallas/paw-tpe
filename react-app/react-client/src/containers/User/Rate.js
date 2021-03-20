import React from "react";
import { withRouter } from "react-router";
import { Col, Container, Row } from "react-bootstrap";
import Card from "../../components/Card/Card";
import CssBaseline from "@material-ui/core/CssBaseline";
import Avatar from "@material-ui/core/Avatar";
import LockOutlinedIcon from "@material-ui/icons/LockOutlined";
import Typography from "@material-ui/core/Typography";
import {makeStyles} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";

const useStyles = makeStyles((theme) => ({
  root: {
    Width: 350,
  },
  paper: {
    marginTop: theme.spacing(8),
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },
  avatar: {
    margin: theme.spacing(1),
    backgroundColor: theme.palette.secondary.main,
  },
  form: {
    width: "100%", // Fix IE 11 issue.
    marginTop: theme.spacing(1),
    paddingLeft: "20px",
    paddingRight: "20px",
  },
  submit: {
    width: "150px",
    margin: theme.spacing(3, 0, 2),
  },
  container: {
    background: "#FAF6FC",
    height: "100vh",
  },
  row: {
    paddingTop: "100px",
  },
  col: {
    textAlign: "center",
  },
}));

const UserRated = () => {
  const classes = useStyles();
  const { t } = useTranslation();

  return (
      <div className={classes.container}>
        <Container fluid="md" component="main" maxWidth="sm">
          <Row className={classes.row}>
            <Col>
              <Card className={classes.root}>
                <CssBaseline/>
                <div className={classes.paper}>
                  <Avatar className={classes.avatar}>
                    <LockOutlinedIcon/>
                  </Avatar>
                  <Typography component="h1" variant="h5">
                    e-Lobby
                  </Typography>
                  <h2>{t("ratings.thanks")}</h2>
                </div>
              </Card>
            </Col>
          </Row>
        </Container>
      </div>
  );
};

export default withRouter(UserRated);
