<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<html>

<head>

    <title>e-lobby</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.3.1.js"></script>
    <script src="/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/resources/js/slideBar.js"></script>
    <link href="/resources/CSS/my_style.css" rel="stylesheet">
    <script src='https://kit.fontawesome.com/a076d05399.js'></script>
    <link rel="stylesheet" href="https://cdn.datatables.net/1.10.20/css/jquery.dataTables.min.css">
    <script src='https://cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js'></script>

</head>
<body>
<div class="container cont" style="height: 100vh !important; width: 100vw !important;margin-left: 0 !important; margin-right: 0 !important">
    <div class="row">
        <div class="col">
            <nav class="navbar navbar-inverse sidebar" style="z-index: initial !important;" role="navigation">
                <div class="container-fluid">
                    <!-- Brand and toggle get grouped for better mobile display -->
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle" data-toggle="collapse"
                                data-target="#bs-sidebar-navbar-collapse-1">
                            <span class="sr-only">Toggle navigation</span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                            <span class="icon-bar"></span>
                        </button>
                        <a class="navbar-brand" href="/">e-lobby</a>
                    </div>
                    <!-- Collect the nav links, forms, and other content for toggling -->
                    <div class="collapse navbar-collapse" id="bs-sidebar-navbar-collapse-1">
                        <ul class="nav navbar-nav">
                            <li><a href="#">Check-In</a></li>
                            <li><a href="#">Check-Out</a></li>
                            <li><a href="/rooms/reservations">Reservas</a></li>
                            <li><a href="/">Productos</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        </div>
    </div>

    <div class="row myheader vertical-align">
        <div class="col-xs-12" style="text-align: left">
            <div>Check-Out exitoso!</div>
        </div>
    </div>
    <br>
    <br>
    <div class="row" style="font-size:x-large ">
        <div class="col" style="margin-left: 25px">
            La accion se realizo exitosamente.
        </div>
    </div>

    <div class="row">
        <div class="col-xs-6 form-group" style="z-index:9999;grid-auto-columns: auto">
            <table id="myTable" class="display" style="width:100%;  border: 1px solid black !important;">
                <thead>
                <tr>
                    <th>Servicios</th>
                    <th>Valor</th>
                </tr>
                </thead>
                <tbody>
<%--                <c:forEach var="room" items="${RoomList}">--%>
<%--                    <tr>--%>

<%--                        <c:if test="${room.freeNow == true}">--%>

<%--                            <td style="text-align: left">${room.id}</td>--%>
<%--                            <td style="text-align: left">${room.number}</td>--%>
<%--                            <td>${room.roomType}</td>--%>
<%--                            <td style="text-align: left">-</td>--%>
<%--                            <td style="text-align: left">-</td>--%>

<%--                        </c:if>--%>


<%--                    </tr>--%>
<%--                </c:forEach>--%>
                </tbody>
                <tfoot>
                <tr>
                    <th>Total</th>
                    <th>Mucho</th>
                </tr>
                </tfoot>
            </table>
        </div>
    </div>

        <div class="col-xs-6" style="margin-left: 25px">
            <button type="button" class="btn btn-success btn-lg"><a href="/" style="color: white">Volver</a></button>
        </div>


</div>
</body>
</html>

<script>
    $(document).ready(function () {
        $('#myTable').DataTable({
            filter: false,
        });
    });
</script>