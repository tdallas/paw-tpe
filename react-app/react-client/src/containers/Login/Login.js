import React, {useEffect, useState} from "react";
import Avatar from "@material-ui/core/Avatar";
import Button from "@material-ui/core/Button";
import CssBaseline from "@material-ui/core/CssBaseline";
import TextField from "@material-ui/core/TextField";
import LockOutlinedIcon from "@material-ui/icons/LockOutlined";
import Typography from "@material-ui/core/Typography";
import { makeStyles } from "@material-ui/core/styles";
import Container from "@material-ui/core/Container";
import Card from "@material-ui/core/Card";
import { Row, Col } from "react-bootstrap";
import { useTranslation } from "react-i18next";
import { withRouter } from "react-router";

import { login } from "../../api/loginApi";
import InfoSimpleDialog from "../../components/Dialog/SimpleDialog";
import {useQuery} from "../../utils/hooks/useQuery";

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

const Login = ({ history, setIsLoggedIn, setIsClient, isLoggedIn }) => {
  const classes = useStyles();
  const { t } = useTranslation();
  const query = useQuery();
  const redirected = query.get("redirectTo");
  const [hasLogged, onSubmit] = useState(false);
  const [user, onChangeUser] = useState("");
  const [password, onChangePassword] = useState("");
  const [showDialog, updateShowDialog] = useState(false);

  const changeUser = (newUser) => onChangeUser(newUser.target.value);
  const changePassword = (newPassword) =>
    onChangePassword(newPassword.target.value);

  useEffect(() => {
    if (isLoggedIn) {
      history.push("/");
    }
  }, []);

  const onLoginPerformed = (onSubmit, { user, password }, { setIsLoggedIn, setIsClient }) => () => {
    login(user, password)
      .then(() => {
        setIsLoggedIn(true);
        setIsClient(localStorage.getItem("role") === "CLIENT");
        onSubmit(redirected);
      })
      .catch(() => {
        updateShowDialog(true);
      });
  };

  const handleDialogClose = () => {
    updateShowDialog(false);
  }

  const submitLogin = (redirect) => {
    onSubmit(true);
    if (redirect) {
      history.push(redirected);
    } else {
      history.push("/");
    }
  };

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
                <form className={classes.form} noValidate>
                  <TextField
                    variant="outlined"
                    margin="normal"
                    required
                    fullWidth
                    id="email"
                    label={t("user.singular")}
                    name="email"
                    autoComplete="email"
                    autoFocus
                    value={user}
                    onChange={changeUser}
                  />
                  <TextField
                    variant="outlined"
                    margin="normal"
                    required
                    fullWidth
                    name="password"
                    label={t("password")}
                    type="password"
                    id="password"
                    autoComplete="current-password"
                    value={password}
                    onChange={changePassword}
                  />
                  <Row>
                    <Col className={classes.col}>
                      <Button
                        type="button"
                        variant="contained"
                        color="primary"
                        className={hasLogged ? classes.submit : classes.submit} // FIXME
                        disabled={hasLogged}
                        onClick={onLoginPerformed(
                          submitLogin,
                          { user, password, },
                          { setIsClient, setIsLoggedIn }
                        )}
                      >
                        {t("login")}
                      </Button>
                    </Col>
                  </Row>
                </form>
              </div>
            </Card>
          </Col>
        </Row>
        <InfoSimpleDialog open={showDialog} onClose={handleDialogClose}>
          {t("loginFailed")}
        </InfoSimpleDialog>
      </Container>
    </div>
  );
};

export default withRouter(Login);
