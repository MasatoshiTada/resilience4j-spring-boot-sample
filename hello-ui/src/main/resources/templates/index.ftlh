<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>hello-ui</title>
</head>
<body>
    <h1>${message}</h1>

    <form action="/hello" method="get">
        <input type="submit" value="相手が落ちてるかも">
    </form>
    <form action="/slow" method="get">
        <input type="submit" value="相手が遅いかも">
    </form>
</body>
</html>