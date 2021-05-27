import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';

const useStyles = makeStyles((theme) => ({
  textField: {
    marginLeft: theme.spacing(1),
    marginRight: theme.spacing(1),
    display: 'flex',
    flexWrap: 'wrap',
  },
}));

const DatePickers = (props) => {
  const classes = useStyles();
  const {Id, label, onChange, value} = props;

  return (
      <TextField
        id={Id}
        label={label}
        type="date"
        className={classes.textField}
        InputLabelProps={{
          shrink: true,
        }}
        onChange={onChange}
        value={value}
      />
  );
}

export default DatePickers;