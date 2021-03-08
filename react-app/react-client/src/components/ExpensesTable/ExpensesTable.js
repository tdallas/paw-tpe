import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import {useTranslation} from "react-i18next";

const useStyles = makeStyles({
  table: {
    minWidth: 700,
  },
});

function priceRow(qty, unit) {
  return qty * unit;
}

function subtotal(items) {
  return items
    .map(({ productAmount, productPrice }) => productAmount * productPrice)
    .reduce((sum, i) => sum + i, 0);
}

const ExpensesTable = (props) => {
  const classes = useStyles();
  const {t} = useTranslation();

  return (
    <TableContainer component={Paper}>
      <Table className={classes.table} aria-label="spanning table">
        <TableHead>
          <TableRow>
            <TableCell align="center" colSpan={3}>
              {t("table.details")}
            </TableCell>
            <TableCell align="right">{t("table.price")}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>{t("table.description")}</TableCell>
            <TableCell align="right">{t("table.quantity")}</TableCell>
            <TableCell align="right">{t("table.unit")}</TableCell>
            <TableCell align="right">{t("table.sum")}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {props.rows.map(
            ({ productAmount, productDescription, productPrice }) => {
              return (
                <TableRow key={productDescription}>
                  <TableCell>{productDescription}</TableCell>
                  <TableCell align="right">x{productAmount}</TableCell>
                  <TableCell align="right">${productPrice}</TableCell>
                  <TableCell align="right">
                    ${priceRow(productAmount, productPrice)}
                  </TableCell>
                </TableRow>
              );
            }
          )}
          <TableRow>
            <TableCell />
            <TableCell style={{ textAlign: "right" }} colSpan={2}>
              Total
            </TableCell>
            <TableCell align="right">${subtotal(props.rows)}</TableCell>
          </TableRow>
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default ExpensesTable;
