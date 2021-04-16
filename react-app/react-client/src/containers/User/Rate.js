import React, {useState} from "react";
import {useParams, withRouter} from "react-router";
import StarsRating from 'stars-rating';
import { Col, Container, Row } from "react-bootstrap";
import CssBaseline from "@material-ui/core/CssBaseline";
import Avatar from "@material-ui/core/Avatar";
import LockOutlinedIcon from "@material-ui/icons/LockOutlined";
import Typography from "@material-ui/core/Typography";
import {makeStyles} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import InfoSimpleDialog from "../../components/Dialog/SimpleDialog";
import {rateStay} from "../../api/userApi";
import {useHistory, useLocation} from "react-router-dom";

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
  const [modalMessage, setModalMessage] = useState();
  const {reservationId} = useParams();
  const history = useHistory();
  const lastLocation = useLocation();

  const ratingChanged = (rating) => {
    rateStay(reservationId, {rating}).then(() => {
      setModalMessage(t("ratings.thanks"));
    }).catch((error) => {
      if (error.response) {
        if (error.response.status === 403) {
          redirectToLogin();
        }
      }
      setModalMessage(t("ratings.repeated"));
    });
  }

  const redirectToLogin = () => {
    console.log("lastLocation", lastLocation);
    console.log("lastLocation string", lastLocation.toString());
    console.log("lastLocation pathname", lastLocation.pathname);
    history.push(`/login?redirectTo=${lastLocation.pathname}`);
  }

  return (
      <div className={classes.container}>
        <Container fluid="md" component="main" maxWidth="sm">
          <Row className={classes.row}>
            <Col>
              <CssBaseline/>
              <div className={classes.paper}>
                <Avatar className={classes.avatar}>
                  <LockOutlinedIcon/>
                </Avatar>
                <Typography component="h1" variant="h5">
                  e-Lobby
                </Typography>
              </div>
              <div className={classes.paper}>{t("ratings.rate")}</div>
              <div className={classes.paper}>
                <StarsRating
                    count={5}
                    onChange={ratingChanged}
                    half={false}
                    size={24}
                    color2={'#ffd700'} />
              </div>
            </Col>
          </Row>
          <InfoSimpleDialog open={modalMessage}>
            <p>{modalMessage}</p>
          </InfoSimpleDialog>
        </Container>
      </div>
  );
};

export default withRouter(UserRated);
