<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"  %>


<!DOCTYPE html>
<html lang="en">
<head>
    <title>Live - ${serverTime}</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>

    <link href="<c:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet"  media="screen" />
    <link href="<c:url value="/resources/css/cerulean.bootstrap.min.css" />" rel="stylesheet"  media="screen" />
    <link href="<c:url value="/resources/css/stickyfooter.css" />" rel="stylesheet"  media="screen" />

    <script type="text/javascript" src="<c:url value="/resources/js/jquery-1.10.2.min.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/highstock.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/exporting.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/chartfunctions.js" />"></script>

    <script type="text/javascript">
        var contextPath = '<c:out value="${pageContext.request.contextPath}"/>';
        console.log(contextPath);
        var CHART;
        var SEQ = new Object();
        var TS = new Object();
        $(document).ready(function() {
            SEQ["bitstamp"] = 0;
            SEQ["mtgox"] = 0;
            createCandleStickWithVolumeChart(contextPath+'/'+'trades', 'bitstamp', SEQ["bitstamp"], 'bitstamp-container');
            /* createCandleStickWithVolumeChart(contextPath+'/'+'trades', 'mtgox', SEQ["mtgox"], 'mtgox-container'); */
        });
    </script>
</head>
<body>
<div id="wrap">
<!-- NAVBAR -->
<!-- Docs master nav -->
<div class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <a href="<c:url value='/charts'/>" class="navbar-brand"><small></small></a>
        <button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#navbar-main">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <div class="nav-collapse collapse" id="navbar-main">
            <ul class="nav navbar-nav pull-left">
            </ul>
        </div>
    </div>
</div><br/><br/><br/><br/>

<!-- CONTAINER -->
<div class="container">
<div class="row">
    <div class="col-md-12">
        <div class="row">
            <div class="col-md-4">
                <div class="panel panel-danger" style="min-width: 505px; max-width: 800px;">
                    <div class="panel-heading" >
                        <h3 class="panel-title" >Bitstamp</h3>
                    </div>
                    <div id="bitstamp-container" style="height: 500px; min-width: 500px"></div>
                    <div id="mtgox-container" style="height: 500px; min-width: 500px"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- //style="min-width: 500px; max-width: 799px;  height: 800px; margin: 0 auto" -->
</div><!--/CONTAINER -->
<div id="push"></div>
</div><!--/WRAP -->


<!-- Footer
=================================-->
<div id="footer">
    <div class="container">
        <div class="row">
            <p class="text-center"><small>2013 <a href="http://nolandiatrading.com">Nolandia Trading</a></small></p>
        </div>

    </div>
</div>

<!-- /Footer
=================================-->

</body>
</html>