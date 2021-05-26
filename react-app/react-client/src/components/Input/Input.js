import React from 'react';
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';

const useStyles = makeStyles((theme) => ({
  root: {
    '& > *': {
      margin: theme.spacing(1),
    },
  },
}));

const ComposedTextField = (props) => {
  const { onChange, error, required, helperText, label, value, type} = props;
  const classes = useStyles();

  return (
    <form className={classes.root} noValidate autoComplete="off">
        <TextField id="component-simple" label={label} value={value} type={type}
                   onChange={onChange} fullWidth={true} error={error}
                   required={required} helperText={helperText}
        />
    </form>
  );
}

export default ComposedTextField;