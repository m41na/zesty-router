`<div id='comments'>
    <ul>
      <% comments.comments.forEach(function(comment){ %>
        <% if (comment) { %>
          <li>
            <h2><%= comment.author %></h2>
            <p><%= comment.content %></p>
          </li>
        <% } %>
      <% }); %>
    </ul>
</div>`