`<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Comments List</title>
</head>
<body>
    <main>
        <div id="comments-list">
            <%= React.renderToString(React.createElement(CommentsList, {comments: comments})) %>
        </div>
    </main>
    <!-- Load our React component. -->
    <script src="comments-list.js"></script>

</body>
</html>`