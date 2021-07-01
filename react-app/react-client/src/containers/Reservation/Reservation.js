import React, {useEffect, useState} from "react";
import { Container, Row, Col } from "react-bootstrap";
import { makeStyles } from "@material-ui/core/styles";
import { withRouter } from "react-router";
import InfoSimpleDialog from '../../components/Dialog/SimpleDialog';

import { getFreeRooms } from "../../api/roomApi";
import { doReservation } from "../../api/roomApi";
import { useTranslation } from "react-i18next";
import Button from "../../components/Button/Button";
import DatePicker from "../../components/DatePickers/DatePicker";
import Dropdown from "../../components/Dropdown/Dropdown";
import Input from "../../components/Input/Input";
import {useQuery} from "../../utils/hooks/useQuery";

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
  buttonCol: {
    textAlign: "center",
  },
}));

const Reservation = ({ history }) => {
  const classes = useStyles();
  const { t } = useTranslation();
  const query = useQuery();

  const [showRooms, show] = useState(false);
  const [room, setRoom] = useState("");
  const [options, setOptions] = useState([]);
  const [dateFrom, setDateFrom] = useState(query.get("startDate") || "");
  const [dateTo, setDateTo] = useState(query.get("startDate") || "");
  const [email, setEmail] = useState("");
  const [errorInput, setErrorInput] = useState(false);
  const [errorDropdown, setErrorDropdown] = useState(false);

  const [showDialog, updateShowDialog] = useState(false);
  const [loading, updateShowLoading] = useState(false);
  const [info, updateInfo] = useState(undefined);
  const [responseError, setResponseError] = useState(false);
  const [respStartDate, setRespStartDate] = useState();
  const [respEndDate, setRespEndDate] = useState();

  const handleDialogClose = () => {
    updateShowDialog(false);
    if (info) {
      history.push("/");
    }
  }

  const createQueryString = () => `startDate=${dateFrom}&endDate=${dateTo}&email=${email}&room=${room}`;

  useEffect(() => {
    if (dateFrom || dateTo) {
      history.push({
        pathname: `/reservation`,
        search: `?${createQueryString()}`
      });
    }
  }, [dateFrom, dateTo, email, room]);

  const emailOnChange = (newEmail) => {
    setEmail(newEmail.target.value);
  };

  const dateFromOnChange = (newDateFrom) => {
    setDateFrom(newDateFrom.target.value);
  };

  const dateToOnChange = (newDateTo) => {
    setDateTo(newDateTo.target.value);
  };

  const showRoomsHandler = (startDate, endDate) => () => {
    getFreeRooms({ startDate, endDate })
      .then((result) => {
        setOptions(result.data);
        if (!showRooms) show(true);
      })
      .catch((error) => {
        updateShowLoading(false);
        updateShowDialog(true);
        setResponseError(true);
        updateInfo(t("something_happened"));
      });
    
  };

  const formIsValid = () => {
    let isOk = true;
    if (email.length === 0 || !email.includes('@')) {
      setErrorInput(true);
      isOk = false;
    }
    if (room.length === 0) {
      isOk = false;
      setErrorDropdown(true);
    }

    return isOk;
  }

  const onSubmitReservation = ({
    startDate,
    endDate,
    userEmail,
    roomId,
  }) => () => {
    if (!formIsValid())
      return;
    else {
      updateShowLoading(true);
      setResponseError(false);
      doReservation({ startDate, endDate, userEmail, roomId })
        .then((response) => {
          updateShowLoading(false);
          updateShowDialog(true);
          updateInfo(response.data);

          let dStart = new Date(response.data.startDate),
          dformatStart = [dStart.getMonth()+1,
            dStart.getDate(),
            dStart.getFullYear()].join('/');

          setRespStartDate(dformatStart);

          let dEnd = new Date(response.data.endDate),
          dformatEnd = [dEnd.getMonth()+1,
            dEnd.getDate(),
            dEnd.getFullYear()].join('/');

          setRespEndDate(dformatEnd);
        })
        .catch((error) => {
          updateShowLoading(false);
          updateShowDialog(true);
          setResponseError(true);
          updateInfo(t("something_happened"));
        });
    }
  };


  const onRoomChange = (newRoom) => {
    setRoom(newRoom.target.value);
  };

  const reservationCancel = () => {
    history.push("/");
  };

  return (
    <div>
      <Container className={classes.container}>
        <Row className={classes.row}>
          <Col xs={12} md={6}>
            <DatePicker
              Id="from"
              label={t("room.room.from")}
              value={dateFrom}
              onChange={dateFromOnChange}
            />
          </Col>
          <Col xs={12} md={6}>
            <DatePicker
              Id="to"
              label={t("room.room.until")}
              value={dateTo}
              onChange={dateToOnChange}
            />
          </Col>
        </Row>
        <Row className={classes.row}>
          <Col xs={12} md={12} className={classes.buttonCol}>
            <Button
              ButtonType="Inherit"
              Id="search-availability"
              ButtonText={t("room.room.filter")}
              onClick={showRoomsHandler(dateFrom, dateTo)}
            />
          </Col>
        </Row>
        <Row className={classes.row}>
          <Col xs={12} md={2}>
            {showRooms && (
              <Dropdown onChange={onRoomChange} error={errorDropdown} helperText={errorDropdown && t("required")} required={true} options={options} />
            )}
          </Col>
          <Col xs={12} md={6}>
            <Input label={t("room.room.owner")} error={errorInput} helperText={errorInput && t("reservation.emailRequirements")} required={true} onChange={emailOnChange} />
          </Col>
          <Col xs={6} md={2}>
            <Button
              ButtonType="Save"
              onClick={onSubmitReservation({
                startDate: dateFrom,
                endDate: dateTo,
                userEmail: email,
                roomId: room,
              })}
              ButtonText={t("accept")}
            />
          </Col>
          <Col xs={6} md={2}>
            <Button
              ButtonType="Back"
              onClick={reservationCancel}
              ButtonText={t("back")}
            />
          </Col>
        </Row>
        <InfoSimpleDialog open={loading} title={t('loading')} />
        <InfoSimpleDialog open={showDialog} onClose={handleDialogClose} title={!responseError ? t('success') : ''}>
          {!responseError && info ? <div>
            <div>{t("reservation.number")}: {info.hash}</div>
            <div>{t("ratings.roomNumber")}: {info.room.number}</div>
            <div>{t("reservation.date.start")}: {respStartDate}</div>
            <div>{t("reservation.date.end")}: {respEndDate}</div>
            <div>{t("reservation.email")}: {info.userEmail}</div>
          </div> : <div>{info ? info : t('reservation.checkin.error')}</div>}
        </InfoSimpleDialog>
      </Container>
    </div>
  );
};

export default withRouter(Reservation);
