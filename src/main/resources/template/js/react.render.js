function reactRender(CommentsList, comments){
    return `
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Comments List</title>
    </head>
    <body>
        <div id="comments-list">
            ${ReactDOM.renderToString(React.createElement(CommentsList, {comments: comments})) }
        </div>
        <!-- Load our React component. -->
        <script src="comments-list.js"></script>
    </body>
    </html>`
}