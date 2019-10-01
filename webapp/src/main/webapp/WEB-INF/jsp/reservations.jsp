<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
    <script src="/resources/js/jquery.min.js"></script>
    <script src="/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="/resources/js/slideBar.js"></script>
    <link href="http://cdn.datatables.net/1.10.19/css/jquery.dataTables.min.css" rel="stylesheet">
    <script type="text/javascript" src="/resources/js/jquery.dataTables.min.js"></script>
    <link href="/resources/CSS/my_style.css" rel="stylesheet">
    <script src='https://kit.fontawesome.com/a076d05399.js'></script>

</head>
<body>
<div class="container cont" style="height: 100vh !important; width: 100vw !important;margin-left: 0 !important; margin-right: 0 !important" >
    <div class="row">
        <div class="col">
            <nav class="navbar navbar-inverse sidebar" style="z-index: initial !important;" role="navigation">
                <div class="container-fluid">
                    <!-- Brand and toggle get grouped for better mobile display -->
                    <div class="navbar-header">
                        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-sidebar-navbar-collapse-1">
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
                            <li ><a href="#">Chrck-In</a></li>
                            <li ><a href="#">Check-Out</a></li>
                            <%--                <li class="dropdown">--%>
                            <%--                    <a href="#" class="dropdown-toggle" data-toggle="dropdown">Reservas<span class="caret"></span></a>--%>
                            <%--                    <ul class="dropdown-menu forAnimate" role="menu">--%>
                            <%--                        <li><a href="#">Action</a></li>--%>
                            <%--                        <li><a href="#">Another action</a></li>--%>
                            <%--                        <li><a href="#">Something else here</a></li>--%>
                            <%--                        <li class="divider"></li>--%>
                            <%--                        <li><a href="#">Separated link</a></li>--%>
                            <%--                        <li class="divider"></li>--%>
                            <%--                        <li><a href="#">One more separated link</a></li>--%>
                            <%--                    </ul>--%>
                            <%--                </li>--%>
                            <li><a href="#">Reservas</a></li>
                            <li><a href="/">Productos</a></li>
                        </ul>
                    </div>
                </div>
            </nav>
        </div>
    </div>
    <div class="row myheader">
        <div class="col-xs-12 " style="text-align: left" >Reservacion</div>
    </div>
    <br>
    <br>
    <div class="row" style="height: 45px">
        <div class="col-xs-12">
            <table>
                <thead>
                <tr>
                    <th>Company</th>
                    <th>Contact</th>
                    <th>Country</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>Alfreds Futterkiste</td>
                    <td>Maria Anders</td>
                    <td>Germany</td>
                </tr>
                <tr>
                    <td>Centro comercial Moctezuma</td>
                    <td>Francisco Chang</td>
                    <td>Mexico</td>
                </tr>
                <tr>
                    <td>Ernst Handel</td>
                    <td>Roland Mendel</td>
                    <td>Austria</td>
                </tr>
                <tr>
                    <td>Island Trading</td>
                    <td>Helen Bennett</td>
                    <td>UK</td>
                </tr>
                <tr>
                    <td>Laughing Bacchus Winecellars</td>
                    <td>Yoshi Tannamuri</td>
                    <td>Canada</td>
                </tr>
                <tr>
                    <td>Magazzini Alimentari Riuniti</td>
                    <td>Giovanni Rovelli</td>
                    <td>Italy</td>
                </tr>
                </tbody>
            </table>

        </div>

    </div>
</div>
</body>
</html>