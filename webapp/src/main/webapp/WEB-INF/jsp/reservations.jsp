<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>

<head>
    <title>e-lobby</title>

    <link  rel="stylesheet" href="${pageContext.request.contextPath}/resources/CSS/my_style.css" >

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.3.1.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/slideBar.js"></script>

    <script src='https://kit.fontawesome.com/a076d05399.js'></script>
    <link rel="stylesheet" href="https://cdn.datatables.net/1.10.20/css/jquery.dataTables.min.css">
    <script src='https://cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js'></script>

</head>
<body>
<div class="container cont"
     style="height: 100vh !important; width: 100vw !important;margin-left: 0 !important; margin-right: 0 !important">
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
                            <li><a href="/rooms/checkin">Check-In</a></li>
                            <li><a href="/rooms/checkout">Check-Out</a></li>
                            <li><a href="/rooms/reservations">Reservas</a></li>
                            <li><a href="/">Productos</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        </div>
    </div>
<c:url value="/rooms/reservationPost" var="postPath"/>
<form:form modelAttribute="reservationForm" action="${postPath}" method="post">
    <div class="row myheader vertical-align">
        <div class="col-xs-6" style="text-align: left">
            <div>Reservas</div>
    </div>
    <br>
    <br>

    <div class="row" >
        <div class="col-xs-6">
            <div class="form-question">
                <div class="form-question__title">
                    <form:label class="items" path="startDate">Desde: </form:label>

                </div>

                <div class="input-container">
                    <form:input id="from_date" path="startDate" type="date" name="effective-date" minlength="1"
                maxlength="64" placeholder=" " autocomplete="nope" required="required"></form:input>
                    <span class="bar"></span>
                </div>
            </div>
        </div>
    <div class="col-xs-6">
        <div class="form-question">
            <div class="form-question__title">
                <form:label class="items" path="endDate">Hasta: </form:label>
            </div>
        <div class="input-container">
                <form:input id="to_date" path="endDate" type="date" name="effective-date" minlength="1"
                maxlength="64" placeholder=" " autocomplete="nope" required="required"></form:input>
                <span class="bar"></span>
        </div>
        </div>
    </div>
    </div>
    <div class="row">
         <div class="col-xs-6">
            <form:label class="items" path="id_reservation">ID de reserva: </form:label>
                 <div class="input-group">
                        <span class="input-group-addon"></span>
                      <form:input id="IDres" path="id_reservation" type="text" class="form-control" name="IDres"
                placeholder="ID de reserva"></form:input>
                 </div>
          </div>
        <div class="col-xs-6">
            <input type="submit" class="btn btn-success btn-lg" value="Buscar"/>

    </div>
    </div>
    <br>


    <div class="row">
        <div class="col-xs-12 form-group" style="z-index:9999;grid-auto-columns: auto">
            <table id="myTable" class="display" style="width:100%;  border: 1px solid black !important;">
                <thead>
                <tr>
                    <th>Numero</th>
                    <th>Titular</th>
                    <th>Desde</th>
                    <th>Hasta</th>
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
            </table>
        </div>
    </div>

</div>
</div>
</body>

<script>
    $(document).ready(function () {
        $('#myTable').DataTable({
            filter: false,
        });
    });
</script>

</html>
