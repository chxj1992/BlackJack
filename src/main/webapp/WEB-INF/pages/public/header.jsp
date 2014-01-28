<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
    <head>
        <meta http-equiv="Content-Type" contect="text/html" charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Black Jack</title>
        <link rel="stylesheet" href="/bower/bootstrap/dist/css/bootstrap.min.css"/>
        <link rel="stylesheet" href="/css/main.css" />
        <script src="/bower/seajs/sea.js"></script>
        <script>
            seajs.config({
                base: "/js/",
                alias: {
                    "jquery": "sea_modules/jquery.js",
                    "bootstrap": "sea_modules/bootstrap.js"
                }
            });
        </script>
